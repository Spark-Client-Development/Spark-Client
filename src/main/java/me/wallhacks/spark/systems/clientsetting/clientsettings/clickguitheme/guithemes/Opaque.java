package me.wallhacks.spark.systems.clientsetting.clientsettings.clickguitheme.guithemes;

import me.wallhacks.spark.systems.clientsetting.clientsettings.GuiSettings;
import me.wallhacks.spark.systems.clientsetting.clientsettings.clickguitheme.ClickGuiTheme;

import java.awt.*;

public class Opaque extends ClickGuiTheme {



    //general
    public Color getMainColor() {
        return GuiSettings.getInstance().getMainColor();
    }


    public Color getContrastColor() {
        return  new Color(150,150,150,255);
    }
    public Color getGuiSubPanelBackgroundColor() {
        return new Color(32,44,57, 211);
    }
    public Color getGuiMainPanelBackgroundColor() {
        return new Color(40,56,69, 186);
    }
    public Color getGuiScreenBackgroundColor() {
        return  new Color(20,20,30,180);
    }

    //settings
    public Color getGuiSettingFieldColor() {
        return new Color(40,56,69, 189);
    }


    public Color getGuiHandelSliderColor() {
        return getMainColor();
    }
    public Color getGuiFilledBackgroundSliderColor() {
        return getMainColor();
    }
    public Color getGuiBackgroundSliderColor() {
        return new Color(40,56,69,140);
    }

}
