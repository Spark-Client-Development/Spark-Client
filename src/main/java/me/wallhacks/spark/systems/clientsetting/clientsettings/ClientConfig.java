package me.wallhacks.spark.systems.clientsetting.clientsettings;

import me.wallhacks.spark.systems.clientsetting.ClientSetting;
import me.wallhacks.spark.systems.setting.settings.*;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Arrays;

@ClientSetting.Registration(name = "ClientConfig", description = "Config for basic client settings")
public class ClientConfig extends ClientSetting {

    StringSetting Prefix = new StringSetting("Prefix",this,".");
    DoubleSetting fadeTime = new DoubleSetting("FadeTime", this, 0.5, 0.1, 5.0);
    public ColorSetting friendColor = new ColorSetting("FriendColor", this, new Color(0x35BABA));

    KeySetting bind = new KeySetting("ClickGui",this, Keyboard.KEY_RSHIFT);

    ColorSetting mainColor = new ColorSetting("ThemeColor",this, new Color(137, 80, 80,255));

    BooleanSetting RenderCustomFont = new BooleanSetting("Custom",this,true);

    BooleanSetting RenderShadow = new BooleanSetting("FontShadow",this,false);



    public boolean getCustomFontEnabled(){
        return RenderCustomFont.isOn();
    }
    public boolean getFontShadow(){
        return RenderShadow.isOn();
    }

    public static int spacing = 4;

    public int getBind() {
        return bind.getKey();
    }

    public Color getMainColor() {
        return mainColor.getColor();
    }

    public BooleanSetting rpc = new BooleanSetting("rpc", this, true);
    public BooleanSetting rpcusername = new BooleanSetting("username", this, true);
    public BooleanSetting rpcip = new BooleanSetting("server", this, true);

    public double getFadeTime() {
        return fadeTime.getValue();
    }

    public String getChatPrefix(){
        return  Prefix.getValue();
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
