package me.wallhacks.spark.systems.setting.settings;

import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.systems.setting.Setting;

import java.util.List;
import java.util.function.Predicate;

public class ModeSetting extends Setting<String> implements EnumSetting {
    private final List<String> modeNames;

    public ModeSetting(String name, SettingsHolder settingsHolder, String value, List<String> modeNames, Predicate<String> visible) {

        super(value, name, settingsHolder,visible);

        this.modeNames = modeNames;
    }
    public ModeSetting(String name, SettingsHolder settingsHolder, String value, List<String> modeNames) {
        this(name,settingsHolder,value,modeNames,null);
    }

    public List<String> getModes() {
        return this.modeNames;
    }

    @Override
    public void increment() {
        int modeIndex = (getValueIndex() + 1) % getModes().size();
        setValue(getModes().get(modeIndex));
    }

    public boolean is(String value) {
        return getValue().equals(value);
    }

    @Override
    public String getValueName() {
        return getValue();
    }

    public boolean isValueName(String value) {
        return getValue().equals(value);
    }

    @Override
    public int getValueIndex() {
        return modeNames.indexOf(getValueName());
    }

    @Override
    public boolean setValueString(String value) {
         setValue(value);
         return true;
    }


    public boolean setValueWithIndex(int value) {

        if(value < 0 || value >= modeNames.size())
            return false;
        return setValueString(modeNames.get(value));
    }
}
