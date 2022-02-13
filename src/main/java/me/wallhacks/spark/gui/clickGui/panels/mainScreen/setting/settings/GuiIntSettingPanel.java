package me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.settings;

import me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.GuiSettingPanel;
import me.wallhacks.spark.gui.panels.GuiPanelInputField;
import me.wallhacks.spark.gui.panels.GuiSlider;
import me.wallhacks.spark.systems.setting.settings.IntSetting;

public class GuiIntSettingPanel extends GuiSettingPanel<IntSetting> {

    public GuiIntSettingPanel(IntSetting setting) {
        super(setting);

        guiPanelInputField = new GuiPanelInputField(0,0,0,0,0);
        guiSlider = setting.getMinMax() == null ? null : new GuiSlider((int)setting.getMinMax().x,(int)setting.getMinMax().y);

        if(guiSlider != null)
            guiSlider.setValue(setting.getValue());
        guiPanelInputField.setText(setting.getValue().toString());


    }

    final GuiPanelInputField guiPanelInputField;
    final GuiSlider guiSlider;

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);

        if(guiSlider != null)
        {
            int rounded = (int)Math.round(guiSlider.getValue());
            if(rounded != getSetting().getValue())
            {
                if(guiSlider.isSelected())
                    getSetting().setNumber(rounded);
                else
                    guiSlider.setValue(getSetting().getValue());

            }
        }



        if(guiPanelInputField.isFocused())
            try {
                String s = guiPanelInputField.getText();
                if(s.endsWith("."))s=s+"0";
                if(s.equals(""))s = "0";
                if(s.equals("-"))s = "-1";
                int parse = Integer.parseInt(s);
                if(parse != getSetting().getValue())
                {
                    getSetting().setNumber(parse);
                    if(guiSlider != null)
                        guiSlider.setValue(parse);
                }



            }
            catch (NumberFormatException e)
            {
                guiPanelInputField.setText(getSetting().getValue().toString());
            }
        else
            guiPanelInputField.setText(getSetting().getValue().toString());

        int inputFieldWidth = 25;
        int inputFieldHeight = 14;

        fontManager.drawString(getSetting().getName(),posX,posY+4, guiSettings.getContrastColor().getRGB());


        int x = 0;

        guiPanelInputField.setBackGroundColor(guiSettings.getGuiSettingFieldColor().getRGB());
        guiPanelInputField.setPositionAndSize(posX+x,posY+4+ 1 +  fontManager.getTextHeight(),inputFieldWidth,inputFieldHeight);
        guiPanelInputField.renderContent(MouseX, MouseY, deltaTime);

        x += inputFieldWidth;

        if(guiSlider != null)
        {
            guiSlider.setPositionAndSize(posX+x+8,posY+4+ 1 +  fontManager.getTextHeight(),width-x-16,inputFieldHeight);

            guiSlider.renderContent(MouseX,MouseY,deltaTime);
        }




        height = inputFieldHeight + 4 +  fontManager.getTextHeight();


    }


}
