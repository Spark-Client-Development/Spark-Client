package me.wallhacks.spark.systems.setting.settings;

import com.google.common.base.Predicate;
import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.systems.setting.Setting;

public class DoubleSetting extends Setting<Double> implements NumberSetting {
    public DoubleSetting(String name, SettingsHolder settingsHolder, double value, double min, double max, double sliderStep, Predicate<Double> visible, String settingCategory) {
        super(value, name, settingsHolder,visible,settingCategory);

        this.sliderStep = sliderStep;
        this.min = min;
        this.max = max;
    }

    public DoubleSetting(String name, SettingsHolder settingsHolder, double value, double min, double max,Predicate<Double> visible, String settingCategory) {
        this(name,settingsHolder,value,min,max,0.1,visible,settingCategory);

    }

    public DoubleSetting(String name, SettingsHolder settingsHolder, double value, double min, double max, double sliderStep,String settingCategory) {
        this(name,settingsHolder,value,min,max,sliderStep,null,settingCategory);

    }

    public DoubleSetting(String name, SettingsHolder settingsHolder, double value, double min, double max, double sliderStep) {
        this(name,settingsHolder,value,min,max,sliderStep,null,"General");

    }
    public DoubleSetting(String name, SettingsHolder settingsHolder, double value, double min, double max) {
        this(name,settingsHolder,value,min,max,0.1);

    }

    public DoubleSetting(String name, SettingsHolder settingsHolder, double value, double min, double max,String settingCategory) {
        this(name,settingsHolder,value,min,max,0.1,null,settingCategory);
    }







        private final double min;
    private final double max;

    private final double sliderStep;


    public double getSliderStep() {
        return this.sliderStep;
    }
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
        setValue(value);
    }

    @Override
    public boolean setValueString(String value) {
        setValue(Double.parseDouble(value));
        return true;
    }

    public float getFloatValue() {
        return getValue().floatValue();
    }

}