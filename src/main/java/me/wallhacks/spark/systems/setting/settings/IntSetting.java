package me.wallhacks.spark.systems.setting.settings;

import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.systems.setting.Setting;
import me.wallhacks.spark.util.objects.Vec2d;
import me.wallhacks.spark.util.objects.Vec2i;

import java.util.function.Predicate;

public class IntSetting extends Setting<Integer> implements NumberSetting {
    public IntSetting(String name, SettingsHolder settingsHolder, int value, int min, int max, Predicate<Integer> visible, String settingCategory) {
        super(value, name, settingsHolder,visible,settingCategory);

        this.minmax = new Vec2d(min,max);

    }

    public IntSetting(String name, SettingsHolder settingsHolder, int value, Predicate<Integer> visible, String settingCategory) {
        super(value, name, settingsHolder,visible,settingCategory);

        this.minmax = null;

    }

    public IntSetting(String name, SettingsHolder settingsHolder, int value, int min, int max, Predicate<Integer> visible) {
        this(name,settingsHolder,value,min,max,visible,"General");

    }
    public IntSetting(String name, SettingsHolder settingsHolder, int value, int min, int max) {
        this(name,settingsHolder,value,min,max, (Predicate<Integer>) null);

    }

    public IntSetting(String name, SettingsHolder settingsHolder, int value) {
        this(name,settingsHolder,value, (Predicate<Integer>) null, "General");

    }

    public IntSetting(String name, SettingsHolder settingsHolder, int value, int min, int max,String settingCategory) {
        this(name,settingsHolder,value,min,max,null,settingCategory);

    }

    private final Vec2d minmax;


    @Override
    public Vec2d getMinMax() {
        return this.minmax;
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
