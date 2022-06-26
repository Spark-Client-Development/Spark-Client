package me.wallhacks.spark.systems.setting;

import me.wallhacks.spark.event.client.SettingChangeEvent;
import me.wallhacks.spark.systems.SettingsHolder;

import java.util.function.Predicate;

public class Setting<T> {
    private T value;
    private final T defaultValue;
    private final String name;
    private final SettingsHolder settingsHolder;


    private final Predicate<T> visible;

    public boolean isVisible() {
        if (this.visible == null) {
            return true;
        }
        return this.visible.test(this.getValue());
    }

    public Setting(T value, String name, SettingsHolder settingsHolder,Predicate<T> visible) {
        this.value = value;
        this.defaultValue = value;
        this.name = name;
        this.settingsHolder = settingsHolder;
        this.visible = visible;

        settingsHolder.addSetting(this);
    }



    public T getValue() {
        return this.value;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public void setValue(T value) {
        this.value = value;

        new SettingChangeEvent(this);


    }

    public String getName() {
        return this.name;
    }

    public SettingsHolder getSettingsHolder() {
        return settingsHolder;
    }

    public boolean setValueString(String value) {
    	return false;
    }
    protected String getStringOfValue(T value) {
        return value.toString();
    }

    public String getDefaultValueString() {
        return getStringOfValue(defaultValue);
    }
    public String getValueString() {
        return getStringOfValue(value);
    }

    public SettingsHolder getsettingsHolder() {
        return settingsHolder;
    }
}
