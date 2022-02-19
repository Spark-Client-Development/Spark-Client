package me.wallhacks.spark.gui.clickGui.settingScreens.kitSetting.guiKitEditor.itemList;

import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.systems.setting.settings.ListSelectSetting;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemListGuiItem extends GuiPanelBase {
    public ItemListGuiItem(Item item) {
        super(0,0,0,16);
        this.item = item;
    }
    Item item;

    public void setItem(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime)
    {
        super.renderContent(MouseX, MouseY, deltaTime);

        if(item != null)
        {
            RenderHelper.enableStandardItemLighting();
            ItemStack i =new ItemStack(item);
            mc.getRenderItem().renderItemAndEffectIntoGUI(i, posX, posY);
            GlStateManager.disableLighting();
            GlStateManager.enableAlpha();
            RenderHelper.disableStandardItemLighting();
        }

        width = 16;
        height = 16;
    }





    @Override
    public void onClickDown(int Mouse,int MouseX, int MouseY)
    {
        super.onClickDown(Mouse, MouseX, MouseY);


    }
    @Override
    public void onClick(int Mouse,int MouseX, int MouseY)
    {
        super.onClick(Mouse, MouseX, MouseY);

    }

}
