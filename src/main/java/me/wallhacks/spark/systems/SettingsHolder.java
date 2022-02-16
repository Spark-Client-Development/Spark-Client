package me.wallhacks.spark.systems;

import me.wallhacks.spark.systems.setting.Setting;
import me.wallhacks.spark.util.objects.Vec2i;

import java.util.ArrayList;

public abstract class SettingsHolder {

    static int lastId = 0;
    public final int modId;



    public SettingsHolder(){
        settings = new ArrayList<Setting<?>>();

        modId = lastId;
        lastId++;
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
