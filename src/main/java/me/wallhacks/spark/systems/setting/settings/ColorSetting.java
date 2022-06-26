package me.wallhacks.spark.systems.setting.settings;

import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.systems.setting.Setting;
import me.wallhacks.spark.util.render.ColorUtil;
import me.wallhacks.spark.util.render.RenderUtil;

import java.awt.*;
import java.util.function.Predicate;

public class ColorSetting extends Setting<SparkColor> {



    public ColorSetting(String name, SettingsHolder settingsHolder, Color color, boolean allowChangeAlpha, Predicate<SparkColor> visible) {
        super(new SparkColor(color), name, settingsHolder,visible);



        offset = (int) (Math.random() * (360 * 32));

        this.allowChangeAlpha = allowChangeAlpha;
    }

    public ColorSetting(String name, SettingsHolder settingsHolder, Color color,boolean allowChangeAlpha) {
        this(name,settingsHolder,color,allowChangeAlpha,null);
    }

    public ColorSetting(String name, SettingsHolder settingsHolder, Color color,Predicate<SparkColor> visible) {
        this(name,settingsHolder,color,true,visible);
    }

    public ColorSetting(String name, SettingsHolder settingsHolder, Color color) {
        this(name,settingsHolder,color,true,null);
    }

    final boolean allowChangeAlpha;

    final int offset;

    public boolean isAllowChangeAlpha() {
        return allowChangeAlpha;
    }

    public Color getColor(double alphaMultiply) {
        Color c = getColor();
        return new Color(c.getRed(),c.getGreen(),c.getBlue(),(int)(c.getAlpha()*alphaMultiply));
    }

    public Color getColor() {
        if (getValue().rainbow != SparkColor.Rainbow.OFF) {
            int speed = 0;
            if (getValue().rainbow == SparkColor.Rainbow.SLOW) {
                speed = 1;
            } else if (getValue().rainbow == SparkColor.Rainbow.MEDIUM) {
                speed = 2;
            } else if (getValue().rainbow == SparkColor.Rainbow.FAST) {
                speed = 4;
            } else {
                speed = 16;
            }



            Color c = ColorUtil.fromHSB((((System.currentTimeMillis() * speed)/2 + offset) % (360 * 32)) / (360f * 32), ColorUtil.getSaturation(getValue().color), ColorUtil.getBrightness(getValue().color));
            getValue().color = (new Color(c.getRed(), c.getGreen(), c.getBlue(), getValue().color.getAlpha()));
        }
        return getValue().color;
    }



    public int getRGB() {
        return getColor().getRGB();
    }

    public void setColor(Color color) {
        getValue().color = (color);
    }

    public void setRainbow (SparkColor.Rainbow value) {
        getValue().rainbow = value;
    }

    public SparkColor.Rainbow getRainbow () {
        return getValue().rainbow;
    }

    @Override
    public boolean setValueString(String value) {
        String[] list = value.split(",");
        setColor(new Color(Integer.parseInt(list[0]),Integer.parseInt(list[1]),Integer.parseInt(list[2]),Integer.parseInt(list[3])));
        setRainbow(SparkColor.Rainbow.values()[Integer.parseInt(list[4])]);
        return true;
    }

    @Override
    protected String getStringOfValue(SparkColor value) {
        return value.color.getRed()+","+value.color.getGreen()+","+value.color.getBlue()+","+value.color.getAlpha()+","+value.rainbow.ordinal();
    }


}
