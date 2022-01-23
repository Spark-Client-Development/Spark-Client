package me.wallhacks.spark.systems.clientsetting.clientsettings;

import me.wallhacks.spark.systems.clientsetting.ClientSetting;
import me.wallhacks.spark.systems.clientsetting.clientsettings.clickguitheme.ClickGuiTheme;
import me.wallhacks.spark.systems.clientsetting.clientsettings.clickguitheme.guithemes.Classic;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.KeySetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import org.lwjgl.input.Keyboard;
import me.wallhacks.spark.systems.clientsetting.clientsettings.clickguitheme.guithemes.Light;
import me.wallhacks.spark.systems.clientsetting.clientsettings.clickguitheme.guithemes.Opaque;

import java.awt.*;
import java.util.Arrays;

@ClientSetting.Registration(name = "GuiSettings", description = "Settings for clickgui")
public class GuiSettings extends ClientSetting {

    public static GuiSettings INSTANCE;
    public GuiSettings() {
        super();
        INSTANCE = this;
    }

    KeySetting bind = new KeySetting("ClickGui",this, Keyboard.KEY_RSHIFT,"ClickGui");

    ModeSetting setting = new ModeSetting("Theme",this,"Classic", Arrays.asList("Light","Classic","Opaque"),"ClickGui");

    ClickGuiTheme[] themes = new ClickGuiTheme[]{new Light(),new Classic(),new Opaque()};


    ModeSetting arrowMode = new ModeSetting("Arrow",this,"Off",Arrays.asList("Off","Left","Right"),"ClickGui");
    BooleanSetting toggleSliders = new BooleanSetting("ToggleSwitches",this,false,"ClickGui");


    ColorSetting mainColor = new ColorSetting("ThemeColor",this, new Color(137, 80, 80,255),"ClickGui");












    BooleanSetting RenderCustomFont = new BooleanSetting("Custom",this,true,"Font");

    BooleanSetting RenderShadow = new BooleanSetting("FontShadow",this,false,"Font");



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





    public static GuiSettings getInstance(){
        return INSTANCE;
    }
}
