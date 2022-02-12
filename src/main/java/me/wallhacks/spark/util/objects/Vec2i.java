package me.wallhacks.spark.util.objects;


import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Vec2i {
    public final int x;
    public final int y;

    public Vec2i(int inX,int inY){
        x = inX;
        y = inY;
    }

    public String toString(){
        return "Vec2i("+x+","+y+")";
    }

    @Override
    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof Vec2i))
        {
            return false;
        }
        else
        {
            Vec2i vec3i = (Vec2i)p_equals_1_;

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

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(x).
                append(y).
                toHashCode();
    }
}

