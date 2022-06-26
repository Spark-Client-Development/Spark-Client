package me.wallhacks.spark.systems.module.modules.render;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.SettingGroup;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.MathUtil;
import me.wallhacks.spark.util.objects.Timer;
import me.wallhacks.spark.util.render.ColorUtil;
import me.wallhacks.spark.util.render.RenderUtil;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.opengl.GL11.*;

@Module.Registration(name = "NameTags", description = "NameTags with more info then default")
public class NameTags extends Module {
    public static NameTags INSTANCE;
    public DoubleSetting scale = new DoubleSetting("Scale", this, 2.0, 0.0, 10.0);
    public DoubleSetting scaleByDistance = new DoubleSetting("ScaleByDistance", this, 1,0,1);
    public ColorSetting outlineColor = new ColorSetting("Outline", this, new Color(0, 187, 255, 174));

    SettingGroup inline = new SettingGroup("Inline", this);
    public BooleanSetting health = new BooleanSetting("Health", inline, true);
    public BooleanSetting ping = new BooleanSetting("Ping", inline, true);
    public BooleanSetting gamemode = new BooleanSetting("GameMode", inline, false);
    public BooleanSetting healthBar = new BooleanSetting("HealthBar", inline, false);

    SettingGroup above = new SettingGroup("Above", this);
    public BooleanSetting armor = new BooleanSetting("Armor", above, true);
    public BooleanSetting durability = new BooleanSetting("Durability", above, false);
    public BooleanSetting enchants = new BooleanSetting("Enchants", above, false);
    public BooleanSetting mainhand = new BooleanSetting("Mainhand", above, true);
    public BooleanSetting offhand = new BooleanSetting("Offhand", above, true);
    public ConcurrentHashMap<Entity, Float> healthMap = new ConcurrentHashMap<>();
    public NameTags() {
        INSTANCE = this;
    }
    me.wallhacks.spark.util.objects.Timer timer = new Timer();
    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (nullCheck() || mc.renderEngine == null || mc.getRenderManager().options == null)
            return;

        List<EntityPlayer> nametagEntities = new ArrayList<>();
        mc.world.playerEntities.stream().filter(entity -> entity != null && entity != mc.getRenderViewEntity()).forEach(nametagEntities::add);
        nametagEntities.sort((o1,o2) -> Math.round(o2.getDistance(mc.getRenderViewEntity())-o1.getDistance(mc.getRenderViewEntity())));
        List<Entity> remove = new ArrayList<>();
        for (Entity e : healthMap.keySet()) {
            if (!nametagEntities.contains(e)) remove.add(e);
        }
        remove.forEach(entity -> healthMap.remove(entity));
        nametagEntities.forEach(entityPlayer -> {
            Entity viewEntity = mc.getRenderViewEntity();
            Vec3d nametagPosition = RenderUtil.interpolateEntityByTicks(entityPlayer, event.getPartialTicks());

            double x = nametagPosition.x;
            double distance = nametagPosition.y + 0.65;
            double z = nametagPosition.z;

            double y = distance + (entityPlayer.isSneaking() ? 0.0 : 0.08);

            nametagPosition = RenderUtil.interpolateEntityByTicks(viewEntity, event.getPartialTicks());

            double posX = viewEntity.posX;
            double posY = viewEntity.posY;
            double posZ = viewEntity.posZ;

            viewEntity.posX = nametagPosition.x;
            viewEntity.posY = nametagPosition.y;
            viewEntity.posZ = nametagPosition.z;



            distance = viewEntity.getDistance(x, distance, z);

            double distanceScale = scale.getValue() + (scale.getValue() / 5) * distance * scaleByDistance.getValue();

            String nameTag = generateNameTag(entityPlayer);
            float width = mc.fontRenderer.getStringWidth(nameTag) / 2;
            float height = mc.fontRenderer.FONT_HEIGHT;

            GlStateManager.pushMatrix();
            RenderHelper.enableStandardItemLighting();
            float newHP = (entityPlayer.getAbsorptionAmount() + entityPlayer.getHealth()) / 36;
            if (!healthMap.containsKey(entityPlayer)) {
                healthMap.put(entityPlayer, newHP);
            }
            float old = healthMap.get(entityPlayer);
            float change = 0.001f;
            change*=timer.getPassedTimeMs();
            newHP = MathHelper.clamp(newHP, old - change, old + change);
            healthMap.put(entityPlayer, newHP);
            float colorPercent = Math.min((entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount()) / 20, 1);
            drawNametag(nameTag, x, y, z, width, height, distanceScale, newHP, colorPercent, distance);
            GlStateManager.pushMatrix();

            Iterator<ItemStack> armorStack = entityPlayer.getArmorInventoryList().iterator();
            ArrayList<ItemStack> stacks = new ArrayList<>();

            if (offhand.getValue())
                stacks.add(entityPlayer.getHeldItemOffhand());

            while (armorStack.hasNext()) {
                ItemStack stack = armorStack.next();
                if (!stack.isEmpty() && armor.getValue())
                    stacks.add(stack);
            }

            if (mainhand.getValue())
                stacks.add(entityPlayer.getHeldItemMainhand());

            Collections.reverse(stacks);

            int offset = stacks.size();
            int offsetScaled = -(8 * offset);

            for (ItemStack stack : stacks) {
                renderItemStack(entityPlayer, stack, offsetScaled, -32, 0);
                renderItemEnchantments(stack, offsetScaled, -62);
                offsetScaled += 16;
            }

            GlStateManager.popMatrix();

            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
            GlStateManager.disablePolygonOffset();
            GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
            GlStateManager.popMatrix();

            mc.getRenderViewEntity().posX = posX;
            mc.getRenderViewEntity().posY = posY;
            mc.getRenderViewEntity().posZ = posZ;
        });
        timer.reset();
    }

    public String getEnchantName(Enchantment enchantment, int translated) {
        if (enchants.getValue()) {
            if (enchantment.getTranslatedName(translated).contains("Vanish"))
                return TextFormatting.RED + "Van";
            if (enchantment.getTranslatedName(translated).contains("Bind"))
                return TextFormatting.RED + "Bind";

            String substring = enchantment.getTranslatedName(translated);
            int translation = (translated > 1) ? 2 : 3;
            if (substring.length() > translation)
                substring = substring.substring(0, translation);

            StringBuilder builder = new StringBuilder();
            String rawString = substring;
            String finalString = builder.insert(0, rawString.substring(0, 1).toUpperCase()).append(substring.substring(1)).toString();

            if (translated > 1)
                finalString = new StringBuilder().insert(0, finalString).append(translated).toString();

            return finalString;
        }

        return "";
    }

    public void renderItemEnchantments(ItemStack itemStack, int x, int y) {
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        Iterator<Enchantment> iterator2;
        Iterator<Enchantment> iterator = iterator2 = EnchantmentHelper.getEnchantments(itemStack).keySet().iterator();

        float durabilityScaled = ((float) (itemStack.getMaxDamage() - itemStack.getItemDamage()) / (float) itemStack.getMaxDamage()) * 100.0f;

        int color = 0x1FFF00;

        if (durabilityScaled > 30 && durabilityScaled < 70)
            color = 0xFFFF00;
        else if (durabilityScaled <= 30)
            color = 0xFF0000;

        if (durability.getValue() && (itemStack.getItem() instanceof ItemArmor || itemStack.getItem() instanceof ItemTool)) {
            GlStateManager.disableDepth();
            mc.fontRenderer.drawString(new StringBuilder().insert(0, ((int) (durabilityScaled))).append('%').toString(), (float) (x * 2), (float) y - 8, color, false);
            GlStateManager.enableDepth();
        }

        if (enchants.getValue()) {
            while (iterator.hasNext()) {
                Enchantment enchantment;
                if ((enchantment = iterator2.next()) == null)
                    iterator = iterator2;

                else {
                    mc.fontRenderer.drawString(getEnchantName(enchantment, EnchantmentHelper.getEnchantmentLevel(enchantment, itemStack)), (float) (x * 2), (float) y, -1, false);

                    y += 8;
                    iterator = iterator2;
                }
            }
        }
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
    }

    public void renderItemStack(EntityPlayer entityPlayer, ItemStack itemStack, int x, int y, int scaled) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.clear(256);
        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -150.0f;
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        int scaledFinal = (scaled > 4) ? ((scaled - 4) * 8 / 2) : 0;
        mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, x, y + scaledFinal);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, itemStack, x, y + scaledFinal);
        mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GlStateManager.popMatrix();
    }

    public String generateNameTag(EntityPlayer entityPlayer) {
        try {
            return (Spark.socialManager.isFriend(entityPlayer) ? TextFormatting.AQUA : "") + entityPlayer.getGameProfile().getName() + generateGamemode(entityPlayer) + TextFormatting.RESET + getPingText(mc.getConnection().getPlayerInfo(entityPlayer.getUniqueID()).getResponseTime()) + generatePing(entityPlayer) + getHealthText(generateHealth(entityPlayer));
        } catch (Exception e) {

        }

        return "";
    }

    public float generateHealth(EntityPlayer entityPlayer) {
        return (float) MathUtil.roundAvoid(entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount(), 1);
    }

    public String generateGamemode(EntityPlayer entityPlayer) {
        if (gamemode.getValue()) {
            if (entityPlayer.isCreative())
                return " [C]";
            else if (entityPlayer.isSpectator())
                return " [I]";
            else
                return " [S]";
        } else
            return "";
    }

    public String generatePing(EntityPlayer entityPlayer) {
        if (!mc.isSingleplayer())
            return ping.getValue() ? " " + mc.getConnection().getPlayerInfo(entityPlayer.getUniqueID()).getResponseTime() + "ms" : "";
        else
            return ping.getValue() ? " -1 ms" : "";
    }

    public static TextFormatting getPingText(float ping) {
        if (ping <= 20)
            return TextFormatting.DARK_GREEN;
        else if (ping <= 50)
            return TextFormatting.GREEN;
        else if (ping <= 90)
            return TextFormatting.YELLOW;
        else if (ping <= 130)
            return TextFormatting.GOLD;
        else
            return TextFormatting.RED;
    }

    public static String getHealthText(float health) {
        if (!NameTags.INSTANCE.health.getValue())
            return "";
        if (health <= 4)
            return " " + TextFormatting.RED + health;
        else if (health <= 8)
            return " " + TextFormatting.GOLD + health;
        else if (health <= 12)
            return " " + TextFormatting.YELLOW + health;
        else if (health <= 16)
            return " " + TextFormatting.DARK_GREEN + health;
        else
            return " " + TextFormatting.GREEN + health;
    }

    public void drawNametag(String text, double x, double y, double z, float width, float height, double distanceScale, float percent, float colorPercent, double distance) {
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) y + 1.4f, (float) z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, (mc.gameSettings.thirdPersonView == 2) ? -1.0f : 1.0f, 0.0f, (float) 0);
        GlStateManager.scale(-(distanceScale / 100), -(distanceScale / 100), (distanceScale / 100));
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GuiUtil.rounded((int) - width - 1, (int) -height, (int) width + 2, healthBar.getValue() ? 8 : 4, new Color(0x80000006, true).getRGB(), 3);
        drawOutlineRoundedRect((int) - width - 1, (int) -height, (int) width + 2, healthBar.getValue() ? 8 : 4, outlineColor.getColor().getRGB(), 3, distance, distanceScale);
        if (healthBar.getValue()) {
            int length = (int) ((width * 2 - 1) * percent);
            length = Math.max(2, length);
            Color color = ColorUtil.lerpColor(new Color(0xBB0A0A),new Color(0x27DC00), colorPercent);
            GlStateManager.disableDepth();
            GuiUtil.rounded((int) - width + 1, 4, (int) (length - width + 1), 6, color.getRGB(), 1);
        }
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        mc.fontRenderer.drawString(text, (int)-width + 1, (int)-height + 3, -1, false);
    }




    public void drawOutlineRoundedRect(int x, int y, int right, int bottom, int color, int radius, double distance, double distanceScale) {


        double divisor = MathUtil.lerp(MathHelper.clamp(distance * (scale.getMax() + 1 - scale.getValue()), 1, 90), MathHelper.clamp((scale.getMax() + 1 - scale.getValue()) * 2, 1, distance), scaleByDistance.getValue());


        RenderUtil.drawPolygonOutline(0, 90, (int) (360 / divisor), x, y, radius, 1f, color);
        RenderUtil.drawPolygonOutline(90, 180, (int) (360 / divisor), right-radius*2, y, radius, 1f, color);
        RenderUtil.drawPolygonOutline(180, 270, (int) (360 / divisor), right-radius*2, bottom-radius*2, radius, 1f, color);
        RenderUtil.drawPolygonOutline(270, 360, (int) (360 / divisor), x , bottom-radius*2, radius, 1f, color);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GL11.glLineWidth((float) 1);

        float a = (float)(color >> 24 & 255) / 255.0F;
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;

        final Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x+radius,y, 0.0D).color(r, g, b, a).endVertex();
        bufferbuilder.pos(right-radius,y, 0.0D).color(r, g, b, a).endVertex();
        tessellator.draw();
        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right,y+radius, 0.0D).color(r, g, b, a).endVertex();
        bufferbuilder.pos(right,bottom-radius, 0.0D).color(r, g, b, a).endVertex();
        tessellator.draw();
        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x+radius,bottom, 0.0D).color(r, g, b, a).endVertex();
        bufferbuilder.pos(right-radius,bottom, 0.0D).color(r, g, b, a).endVertex();
        tessellator.draw();
        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x,y+radius, 0.0D).color(r, g, b, a).endVertex();
        bufferbuilder.pos(x,bottom-radius, 0.0D).color(r, g, b, a).endVertex();
        tessellator.draw();
        GL11.glDisable(GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}

