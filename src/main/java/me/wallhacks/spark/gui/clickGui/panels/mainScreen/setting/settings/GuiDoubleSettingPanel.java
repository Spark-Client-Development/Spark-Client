package me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.settings;

import me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.GuiSettingPanel;
import me.wallhacks.spark.gui.panels.GuiPanelInputField;
import me.wallhacks.spark.gui.panels.GuiSlider;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.util.StringUtil;

public class GuiDoubleSettingPanel extends GuiSettingPanel<DoubleSetting> {

    public GuiDoubleSettingPanel(DoubleSetting setting) {
        super(setting);

        guiPanelInputField = new GuiPanelInputField(0,0,0,0,0);
        guiSlider = new GuiSlider((int)setting.getMin(),(int)setting.getMax());

        guiSlider.setValue(setting.getValue());
        guiPanelInputField.setText(setting.getValue().toString());


    }

    final GuiPanelInputField guiPanelInputField;
    final GuiSlider guiSlider;

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);

        double rounded = Math.round(guiSlider.getValue() / getSetting().getSliderStep())*getSetting().getSliderStep();
        if(rounded != getSetting().getValue())
        {
            if(guiSlider.isSelected())
                getSetting().setNumber(rounded);
            else
                guiSlider.setValue(getSetting().getValue());
        }


        if(guiPanelInputField.isFocused())
            try {
                String s = guiPanelInputField.getText();
                if(s.endsWith("."))s=s+"0";
                if(s.equals(""))s = "0";
                if(s.equals("-"))s = "-1";

                double parse = Double.parseDouble(s);
                if(parse != getSetting().getValue())
                {
                    getSetting().setNumber(parse);
                    guiSlider.setValue(parse);
                }



            }
            catch (NumberFormatException e)
            {
                guiPanelInputField.setText(StringUtil.fmt(getSetting().getValue()));
            }
        else
            guiPanelInputField.setText(StringUtil.fmt(getSetting().getValue()));

        int inputFieldWidth = 25;
        int inputFieldHeight = 14;

        fontManager.drawString(getSetting().getName(),posX,posY+4, guiSettings.getContrastColor().getRGB());


        int x = 0;

        guiPanelInputField.setBackGroundColor(guiSettings.getGuiSettingFieldColor().getRGB());
        guiPanelInputField.setPositionAndSize(posX+x,posY+4+ 1 +  fontManager.getTextHeight(),inputFieldWidth,inputFieldHeight);
        guiPanelInputField.renderContent(MouseX, MouseY, deltaTime);

        x += inputFieldWidth;
        guiSlider.setPositionAndSize(posX+x+8,posY+4+ 1+  fontManager.getTextHeight(),width-x-16,inputFieldHeight);

        guiSlider.renderContent(MouseX,MouseY,deltaTime);



        height = inputFieldHeight + 4 +  fontManager.getTextHeight();


    }




}
