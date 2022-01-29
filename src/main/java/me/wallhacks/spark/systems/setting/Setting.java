package me.wallhacks.spark.systems.setting;

import me.wallhacks.spark.event.client.SettingChangeEvent;
import me.wallhacks.spark.systems.SettingsHolder;

import java.util.function.Predicate;

public abstract class Setting<T> {
    private T value;
    private final String name;
    private final SettingsHolder settingsHolder;
    private final String settingCategory;


    private final Predicate<T> visible;

    public boolean isVisible() {
        if (this.visible == null) {
            return true;
        }
        return this.visible.test(this.getValue());
    }

    public Setting(T value, String name, SettingsHolder settingsHolder,Predicate<T> visible, String settingCategory) {
        this.value = value;
        this.name = name;
        this.settingsHolder = settingsHolder;
        this.settingCategory = settingCategory;
        this.visible = visible;

        settingsHolder.addSetting(this);
    }



    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
        new SettingChangeEvent(this);
        settingsHolder.onSettingChange();
    }

    public String getName() {
        return this.name;
    }

    public String getCategory() {
        return this.settingCategory;
    }




    public boolean setValueString(String value) {
    	return false;
    }
    public String getValueString() {
        return getValue().toString();
    }

    public SettingsHolder getsettingsHolder() {
        return settingsHolder;
    }
}
