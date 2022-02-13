package me.wallhacks.spark.systems.setting.settings;

import com.google.common.base.Predicate;
import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.systems.setting.Setting;
import me.wallhacks.spark.util.objects.Vec2d;

public class DoubleSetting extends Setting<Double> implements NumberSetting {
    public DoubleSetting(String name, SettingsHolder settingsHolder, double value, double min, double max, double sliderStep, Predicate<Double> visible, String settingCategory) {
        super(value, name, settingsHolder,visible,settingCategory);

        this.sliderStep = sliderStep;
        this.minmax = new Vec2d(min,max);
    }


    public DoubleSetting(String name, SettingsHolder settingsHolder, double value, double sliderStep, Predicate<Double> visible, String settingCategory) {
        super(value, name, settingsHolder,visible,settingCategory);

        this.sliderStep = sliderStep;
        this.minmax = null;
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

    public DoubleSetting(String name, SettingsHolder settingsHolder, double value, double min, double max, Predicate<Double> visible) {
        this(name,settingsHolder,value,min,max,0.1,visible,"General");
    }

    public DoubleSetting(String name, SettingsHolder settingsHolder, double value, double min, double max) {
        this(name,settingsHolder,value,min,max,0.1);

    }

    public DoubleSetting(String name, SettingsHolder settingsHolder, double value, double min, double max,String settingCategory) {
        this(name,settingsHolder,value,min,max,0.1,null,settingCategory);
    }








    private final Vec2d minmax;

    private final double sliderStep;


    public double getSliderStep() {
        return this.sliderStep;
    }
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