package me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.settings;

import me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.GuiSettingPanel;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelInputField;
import me.wallhacks.spark.systems.setting.settings.StringSetting;

public class GuiStringSettingPanel extends GuiSettingPanel<StringSetting> {

    public GuiStringSettingPanel(StringSetting setting) {
        super(setting);

        guiPanelInputField = new GuiPanelInputField(0,0,0,0,0);

        guiPanelInputField.setText(setting.getValue().toString());


    }

    final GuiPanelInputField guiPanelInputField;


    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);

        if(guiPanelInputField.isFocused())
            getSetting().setValueString(guiPanelInputField.getText());
        else
            guiPanelInputField.setText(getSetting().getValue());

        int inputFieldWidth = 50;
        int inputFieldHeight = 14;

        fontManager.drawString(getSetting().getName(),posX,posY+4, guiSettings.getContrastColor().getRGB());


        int x = 0;

        guiPanelInputField.setBackGroundColor(guiSettings.getGuiSettingFieldColor().getRGB());
        guiPanelInputField.setPositionAndSize(posX+x,posY+4+ 1 +  fontManager.getTextHeight(),inputFieldWidth,inputFieldHeight);
        guiPanelInputField.renderContent(MouseX, MouseY, deltaTime);




        height = inputFieldHeight + 4 +  fontManager.getTextHeight();


    }


}
