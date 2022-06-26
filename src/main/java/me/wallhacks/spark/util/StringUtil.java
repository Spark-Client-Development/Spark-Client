package me.wallhacks.spark.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.Spark;
import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;

import java.util.Random;

public class StringUtil {

    public static String getNameForKey(int bind) {


        if(bind > 0)
            return Keyboard.getKeyName(bind);
        if(bind <= -2)
            switch (bind) {
                case -2:
                    return "RClick";
                case -3:
                    return "LClick";
                case -4:
                    return "MClick";
                default:
                    return "Mouse" + (-bind - 2);
            }

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
    public static long getIntSeed(String seed) {

        try {
            return (Long.parseLong(seed));
        }
        catch (Exception e)
        {

        }

        int i = 0;


        int index = 1;
        for (Byte c : seed.getBytes())
            i = i + (c * index++);

        return i;
    }

    public static String getServerName(ServerData data) {
        return data == null ? "Singleplayer" : data.serverIP;
    }

    public static String getDeathString(EntityPlayer player,int pops) {
        if (Spark.socialManager.isFriend(player.getName())) {
            return "you just let " + ChatFormatting.AQUA + player.getName() + ChatFormatting.RESET + " die after popping "
                    + ChatFormatting.RED + ChatFormatting.BOLD
                    + pops + ChatFormatting.RESET + (pops == 1 ? " totem" : " totems");
        } else {
            return ChatFormatting.RED + player.getName() + ChatFormatting.RESET + " just died after popping "
                    + ChatFormatting.RED + ChatFormatting.BOLD
                    + pops + ChatFormatting.RESET + (pops == 1 ? " totem" : " totems");
        }
    }

    public static String getPopString(EntityPlayer player,int pops) {
        if (Spark.socialManager.isFriend(player.getName())) {
            return "ur friend " + ChatFormatting.AQUA + player.getName() + ChatFormatting.RESET + " has now popped "
                    + ChatFormatting.RED + ChatFormatting.BOLD
                    + pops + ChatFormatting.RESET + (pops == 1 ? " totem" : " totems") + " go help them";
        } else {
            return ChatFormatting.RED + player.getName() + ChatFormatting.RESET + " has now popped "
                    + ChatFormatting.RED + ChatFormatting.BOLD
                    + pops + ChatFormatting.RESET + (pops == 1 ? " totem" : " totems");
        }
    }

    public static String millisecondToElapsedTime(int milli) {
        int i = milli/1000;
        int j = i / 60;
        i %= 60;
        return i < 10 ? j + ":0" + i : j + ":" + i;
    }

    public static String SpeedConvertFromShort(double currentSpeed,String shortForm) {

        if(shortForm.equalsIgnoreCase("mps")){

            currentSpeed = currentSpeed * 20f;
        }
        else if(shortForm.equalsIgnoreCase("kmph"))
        {

            currentSpeed = ((currentSpeed * 20) / 1000)*60*60;
        }
        else if(shortForm.equalsIgnoreCase("mpmin"))
        {

            currentSpeed = ((currentSpeed * 20))*60;
        }

        return StringUtil.fmt(currentSpeed,1);
    }
    public static String SpeedUnitShortToLong(String shortForm) {
        String end = "m/tick";
        if(shortForm.equalsIgnoreCase("mps")){
            end = "m/s";
        }
        else if(shortForm.equalsIgnoreCase("kmph"))
        {
            end = "km/h";
        }
        else if(shortForm.equalsIgnoreCase("mpmin"))
        {
            end = "m/min";
        }

        return end;
    }
}
