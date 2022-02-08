package me.wallhacks.spark.systems;

import me.wallhacks.spark.systems.setting.Setting;

import java.util.ArrayList;

public abstract class SettingsHolder {

    public SettingsHolder(){
        settings = new ArrayList<Setting<?>>();
    }

    private ArrayList<Setting<?>> settings;

    public void addSetting(Setting<?> setting) {
        if(settings != null && !settings.contains(setting))
            settings.add(setting);
    }

    public ArrayList<Setting<?>> getSettings() {
        return settings;
    }

    public void onConfigLoad(){

    }

    public void onConfigSave(){

    }

    public String getName() {
        return "";
    }

}
