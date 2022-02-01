package me.wallhacks.spark.util;

import net.minecraft.block.Block;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.util.Random;

public class StringUtil {

    public static String getNameForKey(int bind) {


        if(bind > 0)
            return Keyboard.getKeyName(bind);
        if(bind <= -2)
            return "Mouse"+(-2+(-1*bind));

        return "None";
    }

    public static String BlockToText(Block b){

        return b.getLocalizedName();

    }

    public static String fmt(double d)
    {
        if(d == (long) d)
            return String.format("%d",(long)d);
        else if(d*10 == (long) (d*10))
            return String.format("%.1f",d);
        else
            return String.format("%.2f",d);
    }
    public static String fmt(double d,int decimals)
    {
        return String.format("%."+decimals+"f",d);
    }
    public static int getIntSeed(String seed) {
        int i = 0;


        int index = 1;
        for (Byte c : seed.getBytes())
            i = i + (c * index++);

        return i;
    }
}
