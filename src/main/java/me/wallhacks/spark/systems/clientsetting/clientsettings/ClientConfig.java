package me.wallhacks.spark.systems.clientsetting.clientsettings;

import me.wallhacks.spark.systems.clientsetting.ClientSetting;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.systems.setting.settings.StringSetting;

import java.awt.*;

@ClientSetting.Registration(name = "ClientConfig", description = "Anti cheat config")
public class ClientConfig extends ClientSetting {

    StringSetting Prefix = new StringSetting("Prefix",this,".","Commands");
    DoubleSetting fadeTime = new DoubleSetting("FadeTime", this, 0.5, 0.1, 5.0);

    public ColorSetting friendColor = new ColorSetting("FriendColor",this, new Color(18, 55, 177,186),"Render");

    public BooleanSetting rpc = new BooleanSetting("rpc", this, true);

    public double getFadeTime() {
        return fadeTime.getValue();
    }

    public String getChatPrefix(){
        return  Prefix.getValueString();
    }

    public static ClientConfig INSTANCE;

    public ClientConfig() {
        super();
        INSTANCE = this;
    }

    public static ClientConfig getInstance(){
        return INSTANCE;
    }
}
