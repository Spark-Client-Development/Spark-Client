package me.wallhacks.spark.systems;

import me.wallhacks.spark.systems.setting.Setting;
import me.wallhacks.spark.systems.setting.SettingGroup;

import java.util.ArrayList;

public abstract class SettingsHolder {

    protected static int lastId = 0;
    public final int modId;
    
    private ArrayList<Setting<?>> settings;
    private ArrayList<SettingGroup> groups;
    public SettingsHolder(){
        this.settings = new ArrayList<Setting<?>>();
        this.groups = new ArrayList<>();
        this.modId = lastId;
        lastId++;
    }

    public void addSetting(Setting<?> setting) {
        if(!this.settings.contains(setting))
            this.settings.add(setting);
    }

    public void addGroup(SettingGroup group) {
        groups.add(group);
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
