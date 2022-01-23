package me.wallhacks.spark.systems.clientsetting.clientsettings;

import me.wallhacks.spark.systems.clientsetting.ClientSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;

import java.awt.*;

@ClientSetting.Registration(name = "HudSettings", description = "Settings for clickgui")
public class HudSettings extends ClientSetting {

    public static HudSettings INSTANCE;
    public HudSettings() {
        super();
        INSTANCE = this;
    }

    ColorSetting hudMainColor = new ColorSetting("MainColor",this, new Color(137, 80, 80,255),"Hud");
    ColorSetting hudSecondColor = new ColorSetting("SecondColor",this, new Color(150,150,150,255),"Hud");
    ColorSetting hudBackgroundColor = new ColorSetting("BackgroundColor",this, new Color(20,20,30,180),"Hud");

    public Color getGuiHudMainColor() {
        return hudMainColor.getColor();
    }
    public Color getGuiHudSecondColor() {
        return hudSecondColor.getColor();
    }
    public Color getGuiHudListBackgroundColor() {
        return hudBackgroundColor.getColor();
    }


    public static HudSettings getInstance(){
        return INSTANCE;
    }
}
