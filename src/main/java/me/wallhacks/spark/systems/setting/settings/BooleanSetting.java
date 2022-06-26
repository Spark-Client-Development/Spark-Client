package me.wallhacks.spark.systems.setting.settings;

import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.systems.setting.Setting;

import java.util.function.Predicate;


public class BooleanSetting extends Setting<Boolean> implements Toggleable {
    public BooleanSetting(String name, SettingsHolder settingsHolder, boolean value, Predicate<Boolean> visible) {
        super(value, name, settingsHolder,visible);
    }

    public BooleanSetting(String name, SettingsHolder settingsHolder, boolean value) {
        this(name, settingsHolder,value,(Predicate<Boolean> )null);

    }

    @Override
    public void toggle() {
        setValue(!getValue());
    }

    @Override
    public boolean isOn() {
        return getValue();
    }

    @Override
    public boolean setValueString(String value) {
        setValue(Boolean.parseBoolean(value));
        return true;
    }
}

