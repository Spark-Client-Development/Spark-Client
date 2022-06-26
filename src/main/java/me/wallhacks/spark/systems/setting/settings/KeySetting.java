package me.wallhacks.spark.systems.setting.settings;

import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.systems.setting.Setting;
import me.wallhacks.spark.util.MouseUtil;
import me.wallhacks.spark.util.StringUtil;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.function.Predicate;

public class KeySetting extends Setting<Integer> {
    public KeySetting(String name, SettingsHolder settingsHolder, int key, Predicate<Integer> visible) {
        super(key, name, settingsHolder,visible);
    }
    public KeySetting(String name, SettingsHolder settingsHolder, int key) {
        this(name, settingsHolder,key,null);
    }

    public int getKey() {
        return getValue();
    }

    public String getKeyName() {

        return StringUtil.getNameForKey(getKey());
    }

    public boolean isDown() {
        if (getValue() > -1) {
            return Keyboard.isKeyDown(getValue());
        } else if (getValue() < -1) {
            return Mouse.isButtonDown(MouseUtil.convertToMouse(getValue()));
        }
        return false;
    }

    public void setKey(int key) {
        setValue(key);
    }

    @Override
    public boolean setValueString(String value) {
        setValue(Integer.parseInt(value));
        return true;
    }

}