package me.wallhacks.spark.systems.clientsetting.clientsettings.clickguitheme;

import me.wallhacks.spark.systems.clientsetting.clientsettings.GuiSettings;

import java.awt.*;

public abstract class ClickGuiTheme {


    //general
    public Color getMainColor() {
        return GuiSettings.getInstance().getMainColor();
    }


    public Color getContrastColor() {
        return  new Color(150,150,150,255);
    }
    public boolean getBrighten() {
        return true;
    }
    public Color getGuiSubPanelBackgroundColor() {
        return new Color(32,44,57,100);
    }
    public Color getGuiMainPanelBackgroundColor() {
        return new Color(40,56,69,100);
    }
    public Color getGuiScreenBackgroundColor() {
        return  new Color(20,20,30,180);
    }



    //settings
    public Color getGuiSettingFieldColor() {
        return new Color(40,56,69,140);
    }


    public Color getGuiHandelSliderColor() {
        return getMainColor();
    }
    public Color getGuiFilledBackgroundSliderColor() {
        return getMainColor();
    }
    public Color getGuiBackgroundSliderColor() {
        return new Color(112, 112, 115,140);
    }
}
