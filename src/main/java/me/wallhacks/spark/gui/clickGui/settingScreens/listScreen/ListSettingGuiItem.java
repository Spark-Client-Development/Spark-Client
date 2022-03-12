package me.wallhacks.spark.gui.clickGui.settingScreens.listScreen;

import me.wallhacks.spark.gui.clickGui.panels.mainScreen.moduleslist.GuiModulePanel;
import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.systems.setting.settings.ListSelectSetting;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.objects.MCStructures;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class ListSettingGuiItem extends GuiPanelBase {
    public ListSettingGuiItem(ListSelectSetting listSelectSetting,Object selection) {
        super(0,0,0,16);
        this.selection = selection;
        this.listSelectSetting = listSelectSetting;

    }
    Object selection;
    ListSelectSetting listSelectSetting;




    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime)
    {
        super.renderContent(MouseX, MouseY, deltaTime);

        //toggle box
        int disToTop = 4;
        int boxSize = height - disToTop*2;



        drawRect(this.posX+2, this.posY+disToTop, this.posX+2+boxSize, this.posY+disToTop+boxSize, guiSettings.getContrastColor().getRGB());

        if(listSelectSetting.isValueSelected(selection))
            drawRect(this.posX+2+1, this.posY+disToTop+1, this.posX+2+boxSize-1, this.posY+disToTop+boxSize-1, guiSettings.getMainColor().getRGB());

        int textOffset = boxSize + 4;

        if(selection instanceof Block)
        {
            RenderHelper.enableStandardItemLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack((Block)selection), posX + textOffset, posY);
            RenderHelper.disableStandardItemLighting();

            textOffset += 15;
        }
        else if(selection instanceof Item)
        {
            RenderHelper.enableStandardItemLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack((Item)selection), posX + textOffset, posY);
            RenderHelper.disableStandardItemLighting();

            textOffset += 15;
        }
        else if(selection instanceof MCStructures)
        {
            GuiUtil.drawCompleteImage(posX + textOffset, posY,12,12,((MCStructures) selection).getResourceLocation(), Color.WHITE);

            textOffset += 15;
        }


        fontManager.drawString(listSelectSetting.getValueDisplayString(selection), this.posX + 4 + textOffset, this.posY + (this.height - 8) / 2, guiSettings.getContrastColor().getRGB());



    }





    @Override
    public void onClickDown(int Mouse,int MouseX, int MouseY)
    {
        super.onClickDown(Mouse, MouseX, MouseY);

        listSelectSetting.toggle(selection);

    }
    @Override
    public void onClick(int Mouse,int MouseX, int MouseY)
    {
        super.onClick(Mouse, MouseX, MouseY);

    }

}
