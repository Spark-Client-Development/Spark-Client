package me.wallhacks.spark.systems.setting.settings;

import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.systems.setting.Setting;

import java.util.function.Predicate;

public class StringSetting extends Setting<String> {
    public StringSetting(String name, SettingsHolder settingsHolder, String value, Predicate<String> visible) {
        super(value, name, settingsHolder,visible);


    }

    public StringSetting(String name, SettingsHolder settingsHolder, String value) {
        this(name, settingsHolder,value,null);
    }

    @Override
    public boolean setValueString(String value) {
        setValue(value);
        return true;
    }

}