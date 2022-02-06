package me.wallhacks.spark.systems.setting.settings;

import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.systems.setting.Setting;
import me.wallhacks.spark.util.render.ColorUtil;

import java.awt.*;
import java.util.function.Predicate;

public class ColorSetting extends Setting<Color> {

    public ColorSetting(String name, SettingsHolder settingsHolder, Color color, boolean allowChangeAlpha, Predicate<Color> visible, String settingCategory) {
        super(color, name, settingsHolder,visible,settingCategory);
        this.rainbow = Rainbow.OFF;
        this.allowChangeAlpha = allowChangeAlpha;
    }
    public ColorSetting(String name, SettingsHolder settingsHolder, Color color,boolean allowChangeAlpha,Predicate<Color> visible) {
        this(name,settingsHolder,color,allowChangeAlpha,visible,"General");
    }

    public ColorSetting(String name, SettingsHolder settingsHolder, Color color,boolean allowChangeAlpha) {
        this(name,settingsHolder,color,allowChangeAlpha, (Predicate<Color>) null);
    }
    public ColorSetting(String name, SettingsHolder settingsHolder, Color color,boolean allowChangeAlpha,String settingCategory) {
        this(name,settingsHolder,color,allowChangeAlpha,null,settingCategory);
    }

    public ColorSetting(String name, SettingsHolder settingsHolder, Color color,Predicate<Color> visible) {
        this(name,settingsHolder,color,true,visible);
    }
    public ColorSetting(String name, SettingsHolder settingsHolder, Color color,Predicate<Color> visible,String settingCategory) {
        this(name,settingsHolder,color,true,visible,settingCategory);
    }
    public ColorSetting(String name, SettingsHolder settingsHolder, Color color) {
        this(name,settingsHolder,color,(Predicate<Color>) null);
    }
    public ColorSetting(String name, SettingsHolder settingsHolder, Color color, String settingCategory) {
        this(name,settingsHolder,color,true,null,settingCategory);
    }

    final boolean allowChangeAlpha;
    private Rainbow rainbow;

    public boolean isAllowChangeAlpha() {
        return allowChangeAlpha;
    }

    public Color getColor(double alphaMultiply) {
        Color c = getColor();
        return new Color(c.getRed(),c.getGreen(),c.getBlue(),(int)(c.getAlpha()*alphaMultiply));
    }

    public Color getColor() {
        if (rainbow != Rainbow.OFF) {
            int speed = 0;
            if (rainbow == Rainbow.SLOW) {
                speed = 1;
            } else if (rainbow == Rainbow.MEDIUM) {
                speed = 2;
            } else if (rainbow == Rainbow.FAST) {
                speed = 4;
            } else {
                speed = 16;
            }
            Color c = ColorUtil.fromHSB(((System.currentTimeMillis() * speed)/2 % (360 * 32)) / (360f * 32), ColorUtil.getSaturation(getValue()), ColorUtil.getBrightness(getValue()));
            setValue(new Color(c.getRed(), c.getGreen(), c.getBlue(), getValue().getAlpha()));
        }
        return getValue();
    }



    public int getRGB() {
        return getColor().getRGB();
    }

    public void setColor(Color color) {
        setValue(color);
    }

    public void setRainbow (Rainbow value) {
        rainbow = value;
    }

    public Rainbow getRainbow () {
        return rainbow;
    }

    @Override
    public boolean setValueString(String value) {
        String[] list = value.split(",");
        setValue(new Color(Integer.parseInt(list[0]),Integer.parseInt(list[1]),Integer.parseInt(list[2]),Integer.parseInt(list[3])));
        setRainbow(Rainbow.values()[Integer.parseInt(list[4])]);
        return true;
    }
    @Override
    public String getValueString() {
        return getColor().getRed()+","+getColor().getGreen()+","+getColor().getBlue()+","+getColor().getAlpha()+","+getRainbow().ordinal();
    }

    public enum Rainbow {
        OFF("Off"),
        SLOW("Slow"),
        MEDIUM("Medium"),
        FAST("Fast"),
        PSYCHO("Psycho");

        private final String name;

        Rainbow(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public Rainbow next() {
            // No bounds checking required here, because the last instance overrides
            try {
                return values()[ordinal() + 1];
            } catch (ArrayIndexOutOfBoundsException e) {
                return values()[0];
            }
        }
    }
}
