package me.wallhacks.spark.util.objects;


import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Vec2d {
    public double x;
    public double y;

    public Vec2d(double inX, double inY){
        x = inX;
        y = inY;
    }

    public String toString(){
        return "Vec2d("+x+","+y+")";
    }

    @Override
    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof Vec2d))
        {
            return false;
        }
        else
        {
            Vec2d vec3i = (Vec2d)p_equals_1_;

            if (this.x != vec3i.x)
            {
                return false;
            }
            else
            {
                return this.y == vec3i.y;
            }
        }
    }

    public double length(){
        return Math.sqrt(x*x + y*y);
    }
    public Vec2d normalized() {
        if(length() <= 0)
            return new Vec2d(x,y);
        return new Vec2d(x/length(),y/length());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(x).
                append(y).
                toHashCode();
    }
}

