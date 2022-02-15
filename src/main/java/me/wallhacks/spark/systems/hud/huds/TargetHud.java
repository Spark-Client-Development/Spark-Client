package me.wallhacks.spark.systems.hud.huds;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.MathUtil;
import me.wallhacks.spark.util.StringUtil;
import me.wallhacks.spark.util.combat.HoleUtil;
import me.wallhacks.spark.util.objects.Timer;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.render.ColorUtil;
import net.minecraft.block.BlockAir;
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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import me.wallhacks.spark.systems.clientsetting.clientsettings.HudSettings;
import me.wallhacks.spark.systems.module.modules.combat.CrystalAura;
import me.wallhacks.spark.systems.module.modules.combat.KillAura;
import me.wallhacks.spark.systems.module.modules.render.NameTags;

import java.awt.*;

@HudElement.Registration(name = "TargetHud", posX = 0.5, posY = 0, height = 73, width = 150, description = "Shows info about your current target")
public class TargetHud extends HudElement {

    BooleanSetting useRange = new BooleanSetting("UseRange",this,false);
    IntSetting range = new IntSetting("Range",this,100,20,100, v -> useRange.isOn());

    BooleanSetting original = new BooleanSetting("Originalook",this,true);



    double smoothHealth = 0;

    @Override
    public void draw(float deltaTime) {
        EntityPlayer target = getTarget();
        if (target != null) {

            setBackGround(true);

            double health = target.getAbsorptionAmount() + target.getHealth();
            smoothHealth = MathUtil.moveTwards(smoothHealth,health,deltaTime*0.1);

            double smoothPercent = MathHelper.clamp((smoothHealth) / 36,0,1);

            Color healthColor = ColorUtil.getColorBasedOnHealthPercent((float)smoothPercent);


            fontManager.getLargeFont().drawText((target).getName(), getRenderPosX() + 38, getRenderPosY() + 5, HudSettings.getInstance().getGuiHudSecondColor().getRGB());
            mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Items.TOTEM_OF_UNDYING), (int) getRenderPosX() + 35, (int) getRenderPosY() + 19);


            fontManager.drawString("Pops " + TextFormatting.RED + Spark.popManager.getTotemPops(target), (int)getRenderPosX() + 50, (int)getRenderPosY() + 22 + 2, HudSettings.getInstance().getGuiHudSecondColor().getRGB());

            NetworkPlayerInfo playerInfo = mc.getConnection().getPlayerInfo(target.getUniqueID());
            if(playerInfo != null)
            {
                drawPing((int)getRenderPosX() + 37, (int)getRenderPosY() + 39, playerInfo.getResponseTime());
                fontManager.drawString("Ping " + NameTags.getPingText(mc.getConnection().getPlayerInfo(target.getUniqueID()).getResponseTime()) + mc.getConnection().getPlayerInfo(target.getUniqueID()).getResponseTime(), (int)getRenderPosX() + 50, (int)getRenderPosY() + 39 + 2,HudSettings.getInstance().getGuiHudSecondColor().getRGB());

            }

            if(original.isOn()) {
                fontManager.getBadaboom().drawText(holeString(target), getRenderPosX() + 35, getRenderPosY() + 55, HudSettings.getInstance().getGuiHudSecondColor().getRGB());
            }
            else
            {
                int x = fontManager.drawString("Health ", getRenderPosX() + 10, getRenderPosY() + 56 + 2, HudSettings.getInstance().getGuiHudSecondColor().getRGB());

                x = fontManager.drawString(StringUtil.fmt(health, 1), x, getRenderPosY() + 56 + 2, healthColor.getRGB());

                x = fontManager.drawString("Distance " + StringUtil.fmt(PlayerUtil.getDistance(target.getPositionVector()), 1) + "m", x + 8, getRenderPosY() + 56 + 2, HudSettings.getInstance().getGuiHudSecondColor().getRGB());
            }

            Gui.drawRect((int)getRenderPosX(), (int)getRenderPosY() + getHeight() - 3, (int)getRenderPosX() + (int)(smoothPercent*getWidth()), (int)getRenderPosY() + getHeight(), original.isOn() ? HudSettings.getInstance().getGuiHudMainColor().getRGB() : healthColor.getRGB());


            GlStateManager.enableTexture2D();
            RenderHelper.enableStandardItemLighting();
            mc.getRenderItem().zLevel = 200.0f;
            int offset = 0;
            for (ItemStack itemStack : target.inventory.armorInventory) {
                offset++;
                if (itemStack.isEmpty())
                    continue;

                mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, (int)getRenderPosX() + getWidth()-20, (int)getRenderPosY() + 62 - (offset * 15));
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, (int)getRenderPosX() + getWidth()-20, (int)getRenderPosY() +  + 62 - (offset * 15), "");

            }

            GlStateManager.color(1,1,1,1);
            RenderHelper.disableStandardItemLighting();
            mc.getRenderItem().zLevel = 0.0f;

            try {
                if(original.isOn())
                    GuiInventory.drawEntityOnScreen((int)getRenderPosX() + 18, (int)getRenderPosY() + getHeight() - 10, 29, 0.0f, 0.0f, (EntityPlayer) target);
                else
                    GuiInventory.drawEntityOnScreen((int)getRenderPosX() + 18, (int)getRenderPosY() + 51, 25, 0.0f, 0.0f, (EntityPlayer) target);
            }
            catch (ReportedException ignored) {
            }

        }
        else
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
        bufferbuilder.pos((double)(x + 0), (double)(y + height), (double)200).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + height), (double)200).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + width), (double)(y + 0), (double)200).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double)(x + 0), (double)(y + 0), (double)200).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
        tessellator.draw();
    }

    private String holeString(EntityPlayer target) {
        if (getTarget() != null) {
            BlockPos pos = new BlockPos(target.posX, target.posY + 0.9, target.posZ);
            if (!(mc.world.getBlockState(pos).getBlock() instanceof BlockAir)) {
                return (ChatFormatting.DARK_RED + "Burrowed!");
            }
            if (HoleUtil.isInHole(target)) {
                if (HoleUtil.isBedRockHole(pos)) {
                    return ChatFormatting.GREEN + "In Bedrock Hole";
                } else {
                    return ChatFormatting.YELLOW + "In obsidian Hole!";
                }
            } else {
                return ChatFormatting.RED + "Unsafe!";
            }
        }
        return "";
    }

    public EntityPlayer getTarget() {

        if (CrystalAura.targetEntity instanceof EntityPlayer) return (EntityPlayer) CrystalAura.targetEntity;
        if (KillAura.killaurTarget instanceof EntityPlayer) return (EntityPlayer) KillAura.killaurTarget;
        EntityPlayer returnEntity = null;
        double closest = useRange.isOn() ? range.getValue() : Double.MAX_VALUE;
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityPlayer) {
                if (entity != mc.player) {
                    if (mc.player.getDistance(entity) > closest)
                        continue;
                    double pDist = mc.player.getDistance(entity);
                    if (pDist < closest) {
                        closest = pDist;
                        returnEntity = (EntityPlayer) entity;
                    }
                }
            }
        }
        if(returnEntity == null && isInHudEditor())
            return mc.player;
        return returnEntity;
    }
}