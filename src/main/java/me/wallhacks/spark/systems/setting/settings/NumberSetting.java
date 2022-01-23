package me.wallhacks.spark.systems.setting.settings;

public interface NumberSetting {

    public default double getNumber() {
        return 0;
    }

    public default double getMin() {
        return 0;
    }

    public default double getMax() {
        return 0;
    }

    public default void setNumber(double value) {

    }



}
