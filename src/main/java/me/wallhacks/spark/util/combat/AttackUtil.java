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
        if (player == mc.player)
            return false;
        return !Spark.socialManager.isFriend(player.getName());
    }

    public static boolean CanAttackPlayer(EntityPlayer e,double dis){

        if(mc.player.getDistance(e) > dis)
            return false;
        return canAttackPlayer(e);
    }

    public static boolean CanAttackEntity(EntityLivingBase e,double dis){
        if(e == null || e.isDead || e.getHealth() + e.getAbsorptionAmount() <= 0)
            return false;
        if(mc.player.getDistance(e) > dis)
            return false;
        if(e instanceof EntityPlayer)
            return canAttackPlayer((EntityPlayer) e);
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
