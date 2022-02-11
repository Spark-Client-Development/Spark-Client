package me.wallhacks.spark.systems.hud.huds;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.combat.HoleUtil;
import me.wallhacks.spark.util.objects.Timer;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import me.wallhacks.spark.systems.clientsetting.clientsettings.HudSettings;
import me.wallhacks.spark.systems.module.modules.combat.CrystalAura;
import me.wallhacks.spark.systems.module.modules.combat.KillAura;
import me.wallhacks.spark.systems.module.modules.render.NameTags;

@HudElement.Registration(name = "TargetHud", posX = 0.5, posY = 0, height = 73, width = 150, description = "Shows info about your current target")
public class TargetHud extends HudElement {
    int progress;
    Timer timer = new Timer();

    @Override
    public void draw(float time) {
        if (getTarget() != null) {
            EntityPlayer target = getTarget();
            this.setBackGround(true);
            fontManager.getLargeFont().drawText((target).getName(), getRenderPosX() + 38, getRenderPosY() + 5, -1);
            MC.mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Items.TOTEM_OF_UNDYING), (int) getRenderPosX() + 35, (int) getRenderPosY() + 19);
            fontManager.drawString("Pops " + TextFormatting.RED + Spark.popManager.getTotemPops(target), (int)getRenderPosX() + 50, (int)getRenderPosY() + 22, -1);
            try {
                drawPing((int)getRenderPosX() + 37, (int)getRenderPosY() + 39, MC.mc.getConnection().getPlayerInfo(target.getUniqueID()).getResponseTime());
                fontManager.drawString("Ping " + NameTags.getPingText(MC.mc.getConnection().getPlayerInfo(target.getUniqueID()).getResponseTime()) + MC.mc.getConnection().getPlayerInfo(target.getUniqueID()).getResponseTime(), (int)getRenderPosX() + 50, (int)getRenderPosY() + 39, -1);
            } catch (NullPointerException ignored) {
            }
            fontManager.getBadaboom().drawText(holeString(target), getRenderPosX() + 35, getRenderPosY() + 55, -1);
            double percent = (target.getAbsorptionAmount() + target.getHealth()) / 36;
            int bar = (int) (150 * percent);
            if (progress != bar) {
                if (progress > bar) {
                    progress = (int) Math.max(bar, progress - timer.getPassedTimeMs() / 3);
                } else {
                    progress = (int) Math.min(bar, progress + timer.getPassedTimeMs() / 3);
                }
            }
            timer.reset();
            Gui.drawRect((int)getRenderPosX(), (int)getRenderPosY() + getHeight() - 3, (int)getRenderPosX() + progress, (int)getRenderPosY() + getHeight(), HudSettings.getInstance().getGuiHudMainColor().getRGB());
            GlStateManager.enableTexture2D();
            int offset = 0;
            for (ItemStack itemStack : target.inventory.armorInventory) {
                if (itemStack.isEmpty()) {
                    offset++;
                    continue;
                }
                MC.mc.getRenderItem().zLevel = 200.0f;
                MC.mc.getRenderItem().renderItemOverlayIntoGUI(MC.mc.fontRenderer, itemStack, (int)getRenderPosX() + 130, (int)getRenderPosY() + 50 - (offset * 15), "");
                MC.mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, (int)getRenderPosX() + 130, (int)getRenderPosY() + 50 - (offset * 15));
                MC.mc.getRenderItem().renderItemOverlayIntoGUI(MC.mc.fontRenderer, itemStack, (int)getRenderPosX() + 130, (int)getRenderPosY() + 50 - (offset * 15), "");
                offset++;
                MC.mc.getRenderItem().zLevel = 0.0f;
            }
            try {
                GuiInventory.drawEntityOnScreen((int)getRenderPosX() + 18, (int)getRenderPosY() + getHeight() - 7, 29, 0.0f, 0.0f, (EntityPlayer) target);
            } catch (ReportedException ignored) {
            }
        } else setBackGround(false);
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
            if (!(MC.mc.world.getBlockState(pos).getBlock() instanceof BlockAir)) {
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
        double dist = 15;
        for (Entity entity : MC.mc.world.loadedEntityList) {
            if (entity instanceof EntityPlayer) {
                if (entity != MC.mc.player) {
                    if (MC.mc.player.getDistance(entity) > dist)
                        continue;
                    double pDist = MC.mc.player.getDistance(entity);
                    if (pDist < dist) {
                        dist = pDist;
                        returnEntity = (EntityPlayer) entity;
                    }
                }
            }
        }
        return returnEntity;
    }
}