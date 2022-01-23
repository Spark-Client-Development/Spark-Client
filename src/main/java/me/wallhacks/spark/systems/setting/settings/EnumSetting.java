package me.wallhacks.spark.systems.setting.settings;

public interface EnumSetting {



    public default void increment() {

    }

    public default String getValueName() {
        return "";
    }

    public default int getValueIndex() {
        return 0;
    }
}