package me.wallhacks.spark.systems.setting.settings;

import com.github.lunatrius.core.util.vector.Vector2d;
import me.wallhacks.spark.util.objects.Vec2d;

public interface NumberSetting {

    public default double getNumber() {
        return 0;
    }

    public default Vec2d getMinMax() {
        return null;
    }



    public default void setNumber(double value) {

    }



}
