package me.wallhacks.spark.gui.clickGui.settingScreens.kitSetting.kitList;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.gui.panels.GuiPanelButton;
import me.wallhacks.spark.systems.module.modules.misc.InventoryManager;
import me.wallhacks.spark.systems.setting.settings.ListSelectSetting;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GuiKitItem extends GuiPanelBase {
    public GuiKitItem(GuiKitList listSelectSetting, String selection) {
        super(0,0,0,16);
        this.selection = selection;
        this.listSelectSetting = listSelectSetting;
    }
    String selection;
    GuiKitList listSelectSetting;

    GuiPanelButton LoadButton = new GuiPanelButton(() -> {
        InventoryManager.instance.selectKit(selection);
    },"Load");

    GuiPanelButton DeleteButton = new GuiPanelButton(() -> {
        InventoryManager.instance.deleteKit(selection);
        listSelectSetting.refresh();
    },"Delete");



    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime)
    {
        super.renderContent(MouseX, MouseY, deltaTime);

        boolean loaded = InventoryManager.instance.currentKit.equals(selection);




        int FieldSizeY = 14;

        int xp = fontManager.drawString(selection,posX,posY+4,loaded ? guiSettings.getContrastColor().brighter().getRGB() : guiSettings.getContrastColor().getRGB());


        int y = posY+4+ fontManager.getTextHeight()/2-FieldSizeY/2;

        int x = posX + width;




        int FieldSizeX = 0;


        LoadButton.setText(loaded ? "Selected" : "Select");
        FieldSizeX = fontManager.getTextWidth(LoadButton.getText())+6;
        x-=FieldSizeX+4;

        LoadButton.setPositionAndSize(x,y,FieldSizeX,FieldSizeY);
        LoadButton.setOverrideColor(guiSettings.getMainColor());
        LoadButton.renderContent(MouseX,MouseY,deltaTime);


        FieldSizeX = fontManager.getTextWidth(DeleteButton.getText())+6;
        x-=FieldSizeX+4;

        DeleteButton.setPositionAndSize(x,y,FieldSizeX,FieldSizeY);
        DeleteButton.setOverrideColor(guiSettings.getGuiSettingFieldColor());
        DeleteButton.renderContent(MouseX,MouseY,deltaTime);





        height = 8 + fontManager.getTextHeight();



    }





    @Override
    public void onClickDown(int Mouse,int MouseX, int MouseY)
    {
        super.onClickDown(Mouse, MouseX, MouseY);

        listSelectSetting.kitSettingGui.setEditKit(selection);

    }


}
