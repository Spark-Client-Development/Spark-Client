package me.wallhacks.spark.util;

import me.wallhacks.spark.util.objects.Vec2d;
import me.wallhacks.spark.util.objects.Vec2i;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class MathUtil {
    public static double lerp(double current,double target,double lerp){
        current -= (current-target)*MathHelper.clamp(lerp,0, 1);

        return current;

    }

    //thanks to Richard from here: https://stackoverflow.com/questions/17692922/check-is-a-point-x-y-is-between-two-points-drawn-on-a-straight-line
    public static boolean inLine(Vec2d A, Vec2d B, Vec2d C, double tolerance)
    {
        double minX = Math.min(A.x, B.x) - tolerance;
        double maxX = Math.max(A.x, B.x) + tolerance;
        double minY = Math.min(A.y, B.y) - tolerance;
        double maxY = Math.max(A.y, B.y) + tolerance;

        //Check C is within the bounds of the line
        if (C.x >= maxX || C.x <= minX || C.y <= minY || C.y >= maxY)
        {
            return false;
        }

        // Check for when AB is vertical
        if (A.x == B.x)
        {
            if (Math.abs(A.x - C.x) >= tolerance)
            {
                return false;
            }
            return true;
        }

        // Check for when AB is horizontal
        if (A.y == B.y)
        {
            if (Math.abs(A.y - C.y) >= tolerance)
            {
                return false;
            }
            return true;
        }


        // Check distance of the point form the line
        double distFromLine = Math.abs(((B.x - A.x)*(A.y - C.y))-((A.x - C.x)*(B.y - A.y))) / Math.sqrt((B.x - A.x) * (B.x - A.x) + (B.y - A.y) * (B.y - A.y));

        if (distFromLine >= tolerance)
        {
            return false;
        }
        else
        {
            return true;
        }
    }



    public static Vec2d clamp(Vec2d in, Vec2d min, Vec2d max, Vec2d relative) {

        Vec2d dir = new Vec2d(in.x-relative.x,in.y-relative.y).normalized();





        double x = MathHelper.clamp(in.x,min.x,max.x);
        double y = MathHelper.clamp(in.y,min.y,max.y);

        if(in.x == x && in.y == y)
            return new Vec2d(x,y);

        if(dir.x == 0 || dir.y == 0)
            return new Vec2d(x,y);

        double len = Math.max(Math.abs(relative.x-x),Math.abs(relative.y-y))*2;

        x = relative.x + (dir.x) * len;
        y = relative.y + (dir.y) * len;


        return new Vec2d(x,y);


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
    public static double getDistanceFromTo(Vec2d from, Vec2d to) {
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
        return round(value,scale);
    }
    public static int round(double value, double scale) {
        return (int) (Math.round(value / scale) * scale);
    }

}

