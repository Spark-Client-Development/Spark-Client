package me.wallhacks.spark.systems.clientsetting.clientsettings;

import me.wallhacks.spark.systems.clientsetting.ClientSetting;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;

import java.awt.*;

@ClientSetting.Registration(name = "HudSettings", description = "Settings for clickgui")
public class HudSettings extends ClientSetting {

    public static HudSettings INSTANCE;
    public HudSettings() {
        super();
        INSTANCE = this;
    }

    ColorSetting hudMainColor = new ColorSetting("GuiColor",this, new Color(137, 80, 80,255));
    BooleanSetting infoBackGrounds = new BooleanSetting("InfoBackgrounds",this,true);


    Color hudSecondColor = new Color(150,150,150,255);
    Color hudBackgroundColor = new Color(20,20,30,180);

    public Color getGuiHudMainColor() {
        return hudMainColor.getColor();
    }
    public Color getGuiHudSecondColor() {
        return hudSecondColor;
    }

    public boolean getInfoBackGrounds() {
        return infoBackGrounds.isOn();
    }

    public Color getGuiHudListBackgroundColor() {
        return hudBackgroundColor;
    }


    public static HudSettings getInstance(){
        return INSTANCE;
    }
}
