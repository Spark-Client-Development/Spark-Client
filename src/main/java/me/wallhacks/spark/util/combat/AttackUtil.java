package me.wallhacks.spark.util.combat;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.util.MC;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AttackUtil implements MC {

    public static boolean canAttackPlayer(EntityPlayer player){
        if(player == null || player.isDead || player.getHealth() + player.getAbsorptionAmount() <= 0)
            return false;
        if (player == mc.player)
            return false;
        return !Spark.socialManager.isFriend(player.getName());
    }

    public static boolean canAttackPlayer(EntityPlayer e, double dis){

        if(mc.player.getDistance(e) > dis)
            return false;
        return canAttackPlayer(e);
    }

    public static boolean canAttackEntity(EntityLivingBase e, double dis){
        if(e instanceof EntityPlayer)
            return canAttackPlayer((EntityPlayer) e,dis);
        if(e == null || e.isDead || e.getHealth() + e.getAbsorptionAmount() <= 0)
            return false;
        if(mc.player.getDistance(e) > dis)
            return false;

        return true;
    }

    public static boolean isArmourLow(EntityLivingBase e){

        for (ItemStack stack : e.getArmorInventoryList())
        {

            if (stack != null)
            {
                final Item item = stack.getItem();
                if (item != Items.AIR)
                {
                    if(stack.getMaxDamage() - stack.getItemDamage() < 50)
                        return true;
                }
            }

        }

        return false;
    }



}
