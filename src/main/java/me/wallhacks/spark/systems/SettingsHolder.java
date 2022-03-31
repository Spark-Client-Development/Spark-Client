package me.wallhacks.spark.systems;

import me.wallhacks.spark.systems.setting.Setting;
import me.wallhacks.spark.util.objects.Vec2i;

import java.util.ArrayList;

public abstract class SettingsHolder {

    protected static int lastId = 0;
    public final int modId;
    
    private ArrayList<Setting<?>> settings;

    public SettingsHolder(){
        this.settings = new ArrayList<Setting<?>>();

        this.modId = lastId;
        lastId++;
    }

    public void addSetting(Setting<?> setting) {
        if(!this.settings.contains(setting))
            this.settings.add(setting);
    }

    public ArrayList<Setting<?>> getSettings() {
        return this.settings;
    }

    public void onConfigLoad(){}

    public void onConfigSave(){}

    public String getName() {
        return "";
    }
}
