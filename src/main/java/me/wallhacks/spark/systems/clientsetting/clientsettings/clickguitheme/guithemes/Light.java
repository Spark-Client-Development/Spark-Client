package me.wallhacks.spark.systems.clientsetting.clientsettings.clickguitheme.guithemes;

import me.wallhacks.spark.systems.clientsetting.clientsettings.GuiSettings;
import me.wallhacks.spark.systems.clientsetting.clientsettings.clickguitheme.ClickGuiTheme;

import java.awt.*;

public class Light extends ClickGuiTheme {

    public boolean getBrighten() {
        return false;
    }
    //general
    public Color getMainColor() {
        return GuiSettings.getInstance().getMainColor();
    }


    public Color getContrastColor() {
        return  new Color(34, 34, 49, 180);
    }

    public Color getGuiSubPanelBackgroundColor() {
        return new Color(190, 197, 201, 199);
    }
    public Color getGuiMainPanelBackgroundColor() {
        return new Color(171, 168, 168, 199);
    }
    public Color getGuiScreenBackgroundColor() {
        return  new Color(38, 38, 54, 180);
    }

    //settings
    public Color getGuiSettingFieldColor() {
        return new Color(107, 107, 107, 187);
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
