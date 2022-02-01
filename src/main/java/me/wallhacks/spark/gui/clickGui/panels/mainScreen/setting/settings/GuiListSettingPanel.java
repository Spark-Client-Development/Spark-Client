package me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.settings;

import me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.GuiSettingPanel;
import me.wallhacks.spark.gui.clickGui.settingScreens.listScreen.ListSettingScreen;
import me.wallhacks.spark.gui.panels.GuiPanelButton;
import me.wallhacks.spark.systems.setting.settings.ListSelectSetting;

public class GuiListSettingPanel extends GuiSettingPanel<ListSelectSetting> {



    public GuiListSettingPanel(ListSelectSetting setting) {
        super(setting);


    }



    GuiPanelButton button = new GuiPanelButton(() -> { mc.displayGuiScreen(new ListSettingScreen(getSetting()));},"Select");

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);



        int FieldSizeX = fontManager.getTextWidth("Select")+6;




        int FieldSizeY = 14;

        fontManager.drawString(getSetting().getName(),posX,posY+4, guiSettings.getContrastColor().getRGB());

        int y = posY+4+ fontManager.getTextHeight()/2-FieldSizeY/2;

        int x = posX + width - FieldSizeX;


        button.setPositionAndSize(x,y,FieldSizeX,FieldSizeY);
        button.setOverrideColor(guiSettings.getGuiSettingFieldColor());
        button.renderContent(MouseX,MouseY,deltaTime);



        height = 6 +  fontManager.getTextHeight();





    }





}
