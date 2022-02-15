package me.wallhacks.spark.systems.clientsetting.clientsettings;

import me.wallhacks.spark.systems.clientsetting.ClientSetting;
import me.wallhacks.spark.systems.clientsetting.clientsettings.clickguitheme.ClickGuiTheme;
import me.wallhacks.spark.systems.clientsetting.clientsettings.clickguitheme.guithemes.Classic;
import me.wallhacks.spark.systems.clientsetting.clientsettings.clickguitheme.guithemes.Light;
import me.wallhacks.spark.systems.clientsetting.clientsettings.clickguitheme.guithemes.Opaque;
import me.wallhacks.spark.systems.setting.settings.*;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Arrays;

@ClientSetting.Registration(name = "ClientConfig", description = "Config for basic client settings")
public class ClientConfig extends ClientSetting {

    StringSetting Prefix = new StringSetting("Prefix",this,".","Commands");
    DoubleSetting fadeTime = new DoubleSetting("FadeTime", this, 0.5, 0.1, 5.0, "Render");
    public ColorSetting friendColor = new ColorSetting("FriendColor", this, new Color(0x35BABA), "Render");

    KeySetting bind = new KeySetting("ClickGui",this, Keyboard.KEY_RSHIFT,"ClickGui");

    ModeSetting setting = new ModeSetting("Theme",this,"Classic", Arrays.asList("Light","Classic","Opaque"),"ClickGui");

    ClickGuiTheme[] themes = new ClickGuiTheme[]{new Light(),new Classic(),new Opaque()};


    ModeSetting arrowMode = new ModeSetting("Arrow",this,"Off",Arrays.asList("Off","Left","Right"),"ClickGui");
    BooleanSetting toggleSliders = new BooleanSetting("ToggleSwitches",this,false,"ClickGui");


    ColorSetting mainColor = new ColorSetting("ThemeColor",this, new Color(137, 80, 80,255),"ClickGui");

    BooleanSetting RenderCustomFont = new BooleanSetting("Custom",this,true,"ClickGui");

    BooleanSetting RenderShadow = new BooleanSetting("FontShadow",this,false,"ClickGui");

    public BooleanSetting SaveMap = new BooleanSetting("SaveData", this, true, "MapManager");
    public ColorSetting PlayerOutlineColor = new ColorSetting("HeadOutline", this, new Color(19, 231, 142, 253), "MapManager");


    public boolean getCustomFontEnabled(){
        return RenderCustomFont.isOn();
    }
    public boolean getFontShadow(){
        return RenderShadow.isOn();
    }
    public int getArrowMode(){
        return arrowMode.getValueIndex();
    }
    public boolean getToggleSliders(){
        return toggleSliders.isOn();
    }

    public static int spacing = 4;

    public int getBind() {
        return bind.getKey();
    }

    public Color getMainColor() {
        return mainColor.getColor();
    }


    //general
    public Color getContrastColor() {
        return themes[setting.getValueIndex()].getContrastColor();
    }
    public boolean getBrighten() {
        return themes[setting.getValueIndex()].getBrighten();
    }

    public Color getGuiSubPanelBackgroundColor() {
        return themes[setting.getValueIndex()].getGuiSubPanelBackgroundColor();
    }
    public Color getGuiMainPanelBackgroundColor() {
        return themes[setting.getValueIndex()].getGuiMainPanelBackgroundColor();
    }
    public Color getGuiScreenBackgroundColor() {
        return themes[setting.getValueIndex()].getGuiScreenBackgroundColor();
    }


    //settings
    public Color getGuiSettingFieldColor() {
        return themes[setting.getValueIndex()].getGuiSettingFieldColor();
    }
    public Color getGuiHandelSliderColor() {
        return themes[setting.getValueIndex()].getGuiHandelSliderColor();
    }
    public Color getGuiFilledBackgroundSliderColor() {
        return themes[setting.getValueIndex()].getGuiFilledBackgroundSliderColor();
    }
    public Color getGuiBackgroundSliderColor() {
        return themes[setting.getValueIndex()].getGuiBackgroundSliderColor();
    }

    public BooleanSetting rpc = new BooleanSetting("rpc", this, true,"rpc");
    public BooleanSetting rpcusername = new BooleanSetting("username", this, true,"rpc");
    public BooleanSetting rpcip = new BooleanSetting("server", this, true,"rpc");

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
