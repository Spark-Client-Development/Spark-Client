package me.wallhacks.spark.systems.hud.huds;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.clientsetting.clientsettings.HudSettings;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.systems.module.modules.combat.CrystalAura;
import me.wallhacks.spark.systems.module.modules.combat.KillAura;
import me.wallhacks.spark.systems.module.modules.render.NameTags;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.MathUtil;
import me.wallhacks.spark.util.StringUtil;
import me.wallhacks.spark.util.combat.AttackUtil;
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
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.Arrays;

@HudElement.Registration(name = "MiniMe", posX = 1, posY = 0.5, height = 66, width = 33, drawBackground = false, description = "Shows a small version of yourself")
public class MiniMe extends HudElement {


    IntSetting scale = new IntSetting("Scale",this,30,15,40);

    @Override
    public void draw(float deltaTime) {

        if(mc.player == null)
            return;

        setHeight(scale.getValue()*2);
        setWidth(scale.getValue());

        GlStateManager.color(1, 1, 1, 1);
        RenderHelper.disableStandardItemLighting();
        mc.getRenderItem().zLevel = 0.0f;

        try {
            GuiInventory.drawEntityOnScreen((int) getRenderPosX()+getWidth()/2, (int) getRenderPosY()+getHeight(), getWidth(), 0.0f, 0.0f, mc.player);
        } catch (ReportedException ignored) {
        }

    }



}