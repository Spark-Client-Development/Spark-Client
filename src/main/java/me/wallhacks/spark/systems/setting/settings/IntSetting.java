package me.wallhacks.spark.systems.setting.settings;

import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.systems.setting.Setting;

import java.util.function.Predicate;

public class IntSetting extends Setting<Integer> implements NumberSetting {
    public IntSetting(String name, SettingsHolder settingsHolder, int value, int min, int max, Predicate<Integer> visible) {
        super(value, name, settingsHolder,visible);

        this.min = min;
        this.max = max;
    }

    public IntSetting(String name, SettingsHolder settingsHolder, int value, int min, int max) {
        this(name,settingsHolder,value,min,max, (Predicate<Integer>) null);

    }

    private final int min;
    private final int max;

    @Override
    public double getMin() {
        return this.min;
    }
    @Override
    public double getMax() {
        return this.max;
    }


    @Override
    public double getNumber() {
        return getValue();
    }

    @Override
    public void setNumber(double value) {
        setValue((int) Math.round(value));
    }

    @Override
    public boolean setValueString(String value) {
        setValue(Integer.parseInt(value));
        return true;
    }


}
