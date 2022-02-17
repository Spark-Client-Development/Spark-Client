package me.wallhacks.spark.manager;

import io.netty.util.internal.ConcurrentSet;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.util.MC;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scala.Int;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataTrackingManager implements MC {

    public DataTrackingManager() {
        Spark.eventBus.register(this);
        MixPotions();
    }

    ConcurrentSet<Item> toTrack = new ConcurrentSet<>();
    HashMap<Item,Integer> inventoryItems = new HashMap<>();

    HashMap<Integer,Collection<PotionEffect>> potionMap = new HashMap<Integer, Collection<PotionEffect>>();



    void MixPotions() {

        ArrayList<PotionEffect> possiblePotions = new ArrayList<PotionEffect>();

        possiblePotions.add(new PotionEffect(MobEffects.STRENGTH,10,0));
        possiblePotions.add(new PotionEffect(MobEffects.STRENGTH,10,1));

        possiblePotions.add(new PotionEffect(MobEffects.SPEED,10,0));
        possiblePotions.add(new PotionEffect(MobEffects.SPEED,10,1));

        possiblePotions.add(new PotionEffect(MobEffects.ABSORPTION,10,3));

        possiblePotions.add(new PotionEffect(MobEffects.REGENERATION,10,0));
        possiblePotions.add(new PotionEffect(MobEffects.REGENERATION,10,1));

        possiblePotions.add(new PotionEffect(MobEffects.RESISTANCE,10,0));

        possiblePotions.add(new PotionEffect(MobEffects.FIRE_RESISTANCE,10,0));

        possiblePotions.add(new PotionEffect(MobEffects.WEAKNESS,10,0));

        long limit = 1 << possiblePotions.size(); // this is 2^length

        for (long l = 1; l < limit; l++) {
            Set<PotionEffect> subSet = new LinkedHashSet<>();
            for (int i = 0; i < possiblePotions.size(); i++) {
                if ((l & (1 << i)) > 0) {
                    subSet.add(possiblePotions.get(i));
                }
            }
            potionMap.put(PotionUtils.getPotionColorFromEffectList(subSet),new HashSet<>(subSet));
        }

    }




        public int getAmountOfItem(Item item) {
        if(!inventoryItems.containsKey(item))
            inventoryItems.put(item,getItems(item));
        toTrack.add(item);
        return inventoryItems.get(item);
    }

    int getItems(Item itemToSearch) {
        int l = 0;
        for(int i = 0; i < mc.player.inventory.getSizeInventory(); i++){

            if(mc.player.inventory.getStackInSlot(i) instanceof ItemStack){
                if(mc.player.inventory.getStackInSlot(i).getItem() == itemToSearch){
                    l+=mc.player.inventory.getStackInSlot(i).stackSize;
                }
            }
        }
        return l;
    }


    @SubscribeEvent
    void OnUpdate(PlayerUpdateEvent event) {

        Set<Item> set = inventoryItems.keySet();
        for (Item item : toTrack) {
            inventoryItems.put(item,getItems(item));
            set.remove(item);
        }

        for (Item item : set) {
            inventoryItems.remove(item);
        }

        //populate
        toTrack.clear();

    }





    public Collection<PotionEffect> potionEffectsForLiving(EntityLivingBase entity) {
        int i = ((Integer)entity.dataManager.get(EntityLivingBase.POTION_EFFECTS)).intValue();

        if (potionMap.containsKey(i))
            return potionMap.get(i);
        return new ArrayList<>();
    }


}
