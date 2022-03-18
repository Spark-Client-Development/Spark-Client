package me.wallhacks.spark.util;

import me.wallhacks.spark.util.objects.Vec2i;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class MathUtil {
    public static double lerp(double current,double target,double lerp){
        current -= (current-target)*MathHelper.clamp(lerp,0, 1);

        return current;

    }
    public static double moveTwards(double current,double target,double step){
        if(target > current)
            current = Math.min(current+step, target);
        else if(target < current)
            current = Math.max(current-step, target);
        return current;

    }


    public static double square(double value) {
        return value*value;
    }

    public static float lerp(float current,float target,float lerp){
        current -= (current-target)*Math.min(lerp, 1);

        return current;

    }
    public static float moveTwards(float current,float target,float step){
        if(target > current)
            current = Math.min(current+step, target);
        else if(target < current)
            current = Math.max(current-step, target);
        return current;

    }
    public static double getDistanceFromTo(Vec3d from, Vec3d to) {
        double f = from.x - to.x;
        double f1 = from.y - to.y;
        double f2 = from.z - to.z;
        return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
    }
    public static double getDistanceFromTo(Vec2i from, Vec2i to) {
        double f = from.x - to.x;
        double f1 = from.y - to.y;
        return MathHelper.sqrt(f * f + f1 * f1);
    }

    public static double roundToClosest(double num, double low, double high) {
		double d1 = num - low;
		double d2 = high - num;
		if(d2 > d1) {
			return low;
		} else {
			return high;
		}
	}

    public static double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}

