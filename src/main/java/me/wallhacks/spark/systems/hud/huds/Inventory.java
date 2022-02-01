package me.wallhacks.spark.systems.hud.huds;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.systems.module.modules.combat.CrystalAura;
import me.wallhacks.spark.systems.module.modules.combat.KillAura;
import me.wallhacks.spark.systems.module.modules.render.NameTags;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.util.MathUtil;
import me.wallhacks.spark.util.StringUtil;
import me.wallhacks.spark.util.combat.AttackUtil;
import me.wallhacks.spark.util.combat.HoleUtil;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;

@HudElement.Registration(name = "PlayerInventory", posX = 1, posY = 0, height = 16*3+6, width = 16*9+6, description = "Shows your inventory")
public class Inventory extends HudElement {




    @Override
    public void draw(float deltaTime) {

        super.draw(deltaTime);

        int x = getRenderPosX();
        int y = getRenderPosY();

        x += 3;
        y += 3 - 16;

        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableTexture2D();
        mc.getRenderItem().zLevel = 200.0f;
        for (int i = 9; i < 36; i++)
        {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            int offsetX = (i % 9) * 16;
            int offsetY = (i / 9) * 16;
            mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, offsetX + x, offsetY + y);
            mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, offsetX + x, offsetY + y, null);
        }
        mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();


    }


}
