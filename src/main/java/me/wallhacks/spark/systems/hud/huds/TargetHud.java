package me.wallhacks.spark.systems.hud.huds;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.clientsetting.clientsettings.HudSettings;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.systems.module.modules.combat.CrystalAura;
import me.wallhacks.spark.systems.module.modules.combat.KillAura;
import me.wallhacks.spark.systems.module.modules.render.NameTags;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.MathUtil;
import me.wallhacks.spark.util.StringUtil;
import me.wallhacks.spark.util.combat.AttackUtil;
import me.wallhacks.spark.util.objects.Pair;
import me.wallhacks.spark.util.render.ColorUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;

@HudElement.Registration(name = "TargetHud", posX = 0.5, posY = 0, height = 73, width = 170, description = "Shows info about your current target")
public class TargetHud extends HudElement {
    double smoothHealth = 0;
    ResourceLocation resourceLocation = new ResourceLocation("textures/icons/distance.png");

    @Override
    public void draw(float deltaTime) {
        EntityPlayer target = getTarget();
        if (target != null) {

            setBackGround(true);

            double health = target.getAbsorptionAmount() + target.getHealth();
            smoothHealth = MathUtil.moveTwards(smoothHealth, health, deltaTime * 0.1);

            double smoothPercent = MathHelper.clamp((smoothHealth) / 36, 0, 1);

            fontManager.getLargeFont().drawText((target).getName(), getRenderPosX() + 41, getRenderPosY() + 5, HudSettings.getInstance().getGuiHudSecondColor().getRGB());
            mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Items.TOTEM_OF_UNDYING), (int) getRenderPosX() + 38, (int) getRenderPosY() + 19);
            int strength = 0;
            boolean weakness = false;
            int strengthTicks = 0;
            int weaknessTicks = 0;
            if (Spark.potionManager.trackMap.containsKey(target))
                for (Pair<PotionEffect, Integer> p : Spark.potionManager.trackMap.get(target)) {
                    if (p.getKey().getPotion() == MobEffects.STRENGTH) {
                        strength += p.getKey().getAmplifier() + 1;
                        strengthTicks = Math.max(strengthTicks, p.getValue());
                        continue;
                    }
                    if (p.getKey().getPotion() == MobEffects.WEAKNESS) {
                        weaknessTicks = Math.max(weaknessTicks, p.getValue());
                        weakness = true;
                    }
                }
            strength = Math.min(strength, 2);
            ItemStack strengthS = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM, 1), strength == 2 ? PotionTypes.STRONG_STRENGTH : PotionTypes.STRENGTH);
            ItemStack weaknessS = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM, 1), PotionTypes.WEAKNESS);
            mc.renderItem.renderItemAndEffectIntoGUI(strengthS, getRenderPosX() + 90, getRenderPosY() + 19);
            mc.renderItem.renderItemAndEffectIntoGUI(weaknessS, getRenderPosX() + 90, getRenderPosY() + 19 + 17);
            fontManager.drawString((strength == 0 ? ChatFormatting.RED + "Inactive" : "" + ChatFormatting.RED + strength + " " + ChatFormatting.GRAY + StringUtils.ticksToElapsedTime(strengthTicks)), (int) getRenderPosX() + 106, (int) getRenderPosY() + 24, HudSettings.getInstance().getGuiHudSecondColor().getRGB());
            fontManager.drawString((!weakness ? ChatFormatting.RED + "Inactive" : ChatFormatting.DARK_PURPLE + "Weak" + ChatFormatting.GRAY + " " + StringUtils.ticksToElapsedTime(weaknessTicks)), (int) getRenderPosX() + 106, (int) getRenderPosY() + 41, HudSettings.getInstance().getGuiHudSecondColor().getRGB());
            ColorUtil.glColor(new Color(-1));
            fontManager.drawString(ChatFormatting.GRAY + "Pops " + TextFormatting.RED + Spark.popManager.getTotemPops(target), (int) getRenderPosX() + 54, (int) getRenderPosY() + 24, HudSettings.getInstance().getGuiHudSecondColor().getRGB());
            fontManager.drawString(ChatFormatting.GRAY + "Distance " + ChatFormatting.RESET + MathUtil.roundAvoid(mc.player.getDistance(target), 2) + "M", getRenderPosX() + 54, getRenderPosY() + 58, 0x466378);
            NetworkPlayerInfo playerInfo = mc.getConnection().getPlayerInfo(target.getUniqueID());
            if (playerInfo != null) {
                drawPing((int) getRenderPosX() + 41, (int) getRenderPosY() + 39, playerInfo.getResponseTime());
                fontManager.drawString(ChatFormatting.GRAY + "Ping " + NameTags.getPingText(mc.getConnection().getPlayerInfo(target.getUniqueID()).getResponseTime()) + mc.getConnection().getPlayerInfo(target.getUniqueID()).getResponseTime(), (int) getRenderPosX() + 54, (int) getRenderPosY() + 41, HudSettings.getInstance().getGuiHudSecondColor().getRGB());
            }
            GuiUtil.drawCompleteImage(getRenderPosX() + 40, getRenderPosY() + 55, 11, 12, resourceLocation, Color.WHITE);
            Gui.drawRect((int) getRenderPosX(), (int) getRenderPosY() + getHeight() - 3, (int) getRenderPosX() + (int) (smoothPercent * getWidth()), (int) getRenderPosY() + getHeight(), HudSettings.getInstance().getGuiHudMainColor().getRGB());

            GlStateManager.enableTexture2D();
            RenderHelper.enableStandardItemLighting();
            mc.getRenderItem().zLevel = 200.0f;
            int offset = 0;
            for (ItemStack itemStack : target.inventory.armorInventory) {
                offset++;
                if (itemStack.isEmpty())
                    continue;

                mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, (int) getRenderPosX() + getWidth() - 20, (int) getRenderPosY() + 62 - (offset * 15));
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, (int) getRenderPosX() + getWidth() - 20, (int) getRenderPosY() + +62 - (offset * 15), "");

            }


            GlStateManager.color(1, 1, 1, 1);
            RenderHelper.disableStandardItemLighting();
            mc.getRenderItem().zLevel = 0.0f;

            try {
                GuiInventory.drawEntityOnScreen((int) getRenderPosX() + 20, (int) getRenderPosY() + 66, 33, 0.0f, 0.0f, (EntityPlayer) target);
            } catch (ReportedException ignored) {
            }

        } else
            setBackGround(false);
    }

    protected void drawPing(int x, int y, int ping) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GuiPlayerTabOverlay.ICONS);
        byte j;
        if (ping < 0) {
            j = 5;
        } else if (ping < 50) {
            j = 0;
        } else if (ping < 100) {
            j = 1;
        } else if (ping < 200) {
            j = 2;
        } else if (ping < 400) {
            j = 3;
        } else {
            j = 4;
        }

        this.drawTexturedModalRect(x, y, 0, 176 + j * 8, 10, 8);
    }

    public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double) (x + 0), (double) (y + height), (double) 200).tex((double) ((float) (textureX + 0) * 0.00390625F), (double) ((float) (textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double) (x + width), (double) (y + height), (double) 200).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) (textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double) (x + width), (double) (y + 0), (double) 200).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) (textureY + 0) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double) (x + 0), (double) (y + 0), (double) 200).tex((double) ((float) (textureX + 0) * 0.00390625F), (double) ((float) (textureY + 0) * 0.00390625F)).endVertex();
        tessellator.draw();
    }

    public EntityPlayer getTarget() {
        if (isValid(CrystalAura.instance.getTarget()))
            return (EntityPlayer) CrystalAura.instance.getTarget();
        if (isValid(KillAura.instance.getTarget()))
            return (EntityPlayer) KillAura.instance.getTarget();
        EntityPlayer returnEntity = null;
        double closest = 20;
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityPlayer) {
                if (AttackUtil.canAttackPlayer((EntityPlayer) entity)) {
                    if (mc.player.getDistance(entity) > closest)
                        continue;
                    closest = mc.player.getDistance(entity);
                    returnEntity = (EntityPlayer) entity;
                }
            }
        }
        if (returnEntity == null && isInHudEditor())
            return mc.player;
        return returnEntity;
    }

    boolean isValid(Entity entityPlayer) {
        return entityPlayer instanceof EntityPlayer && mc.world.loadedEntityList.contains(entityPlayer);
    }
}