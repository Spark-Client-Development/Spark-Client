package me.wallhacks.spark.systems.setting.settings;

import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.systems.setting.Setting;
import me.wallhacks.spark.util.objects.Vec2d;
import me.wallhacks.spark.util.objects.Vec2i;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.awt.*;
import java.util.function.Predicate;

public class VectorSetting extends Setting<Vec3i> {
    public VectorSetting(String name, SettingsHolder settingsHolder, Vec3i value, Predicate<Vec3i> hasY, Predicate<Vec3i> visible) {
        super(value, name, settingsHolder,visible);

        this.hasY = hasY;
    }


    private final Predicate<Vec3i> hasY;

    public VectorSetting(String name, SettingsHolder settingsHolder, Vec3i value) {
        this(name,settingsHolder,value,null,null);

    }
    public VectorSetting(String name, SettingsHolder settingsHolder, Vec3i value, Predicate<Vec3i> hasY) {
        this(name,settingsHolder,value,hasY,null);

    }



    public boolean hasY() {
        return hasY == null ? true : hasY.test(getValue());
    }

    @Override
    public String getStringOfValue(Vec3i value) {
        return value.getX()+","+value.getY()+","+value.getZ();
    }

    @Override
    public boolean setValueString(String value) {
        String[] s = value.split(",");
        if(s.length < 3)
            return false;
        setValue(new Vec3i(Integer.parseInt(s[0]),Integer.parseInt(s[1]),Integer.parseInt(s[2])));
        return true;
    }


}
