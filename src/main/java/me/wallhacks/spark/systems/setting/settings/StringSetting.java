package me.wallhacks.spark.systems.setting.settings;

import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.systems.setting.Setting;

import java.util.function.Predicate;

public class StringSetting extends Setting<String> {
    public StringSetting(String name, SettingsHolder settingsHolder, String value, Predicate<String> visible, String settingCategory) {
        super(value, name, settingsHolder,visible,settingCategory);


    }

    public StringSetting(String name, SettingsHolder settingsHolder, String value, String settingCategory) {
        this(name, settingsHolder,value,null,settingCategory);


    }




    @Override
    public boolean setValueString(String value) {
        setValue(value);
        return true;
    }

}