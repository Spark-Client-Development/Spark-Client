package me.wallhacks.spark.systems.hud.huds;

import me.wallhacks.spark.systems.clientsetting.clientsettings.HudSettings;
import me.wallhacks.spark.systems.hud.AlignedHudElement;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.util.GuiUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@HudElement.Registration(name = "ArmorHud", description = "Shows you your fps", posX = 0.5, posY = 0.1, width = 62, height = 16)
public class ArmorHud extends AlignedHudElement {
    ResourceLocation[] armor = new ResourceLocation[] {
            new ResourceLocation("textures/icons/armor/helmet.png"),
            new ResourceLocation("textures/icons/armor/chestplate.png"),
            new ResourceLocation("textures/icons/armor/leggings.png"),
            new ResourceLocation("textures/icons/armor/shoes.png")
    };
    @Override
    public void draw(float delta) {
        for (int i = 5; i <= 8; i++) {
            ItemStack s = mc.player.inventoryContainer.getSlot(i).getStack();
            if (s.isEmpty()) {
                GuiUtil.drawCompleteImage(getRenderPosX() + 15 * (i - 5), getRenderPosY(), 16, 16, armor[i-5], HudSettings.getInstance().getGuiHudMainColor());
            }
        }


        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableTexture2D();
        mc.getRenderItem().zLevel = 200.0f;


        for (int i = 5; i <= 8; i++) {
            ItemStack s = mc.player.inventoryContainer.getSlot(i).getStack();
            if (!s.isEmpty()) {

                mc.renderItem.renderItemAndEffectIntoGUI(s, getRenderPosX() + 15 * (i - 5), getRenderPosY());
                mc.renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, s, getRenderPosX() + 15 * (i - 5), getRenderPosY(), null);

            }
        }
        mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
    }
}
