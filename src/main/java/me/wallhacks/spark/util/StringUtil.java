package me.wallhacks.spark.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.Spark;
import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.player.EntityPlayer;
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
}
