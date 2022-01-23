package me.wallhacks.spark.systems.setting.settings;

public interface Toggleable {

    public default void toggle() {

    }


    public default boolean isOn() {
        return true;
    }
}