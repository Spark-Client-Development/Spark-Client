package me.wallhacks.spark.manager;

import io.netty.util.internal.ConcurrentSet;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.util.MC;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

public class ItemTrackerManager implements MC {

    ConcurrentSet<Item> toTrack = new ConcurrentSet<>();
    HashMap<Item, Integer> inventoryItems = new HashMap<>();


    public ItemTrackerManager() {
        Spark.eventBus.register(this);

    }




    public int getAmountOfItem(Item item) {
        if (!inventoryItems.containsKey(item))
            inventoryItems.put(item, getItems(item));
        toTrack.add(item);
        return inventoryItems.get(item);
    }

    int getItems(Item itemToSearch) {
        int l = 0;
        for (int i = 0; i < mc.player.inventory.getSizeInventory(); i++) {

            if (mc.player.inventory.getStackInSlot(i) instanceof ItemStack) {
                if (mc.player.inventory.getStackInSlot(i).getItem() == itemToSearch) {
                    l += mc.player.inventory.getStackInSlot(i).stackSize;
                }
            }
        }
        return l;
    }


    @SubscribeEvent
    void OnUpdate(PlayerUpdateEvent event) {

        Set<Item> set = inventoryItems.keySet();
        for (Item item : toTrack) {
            inventoryItems.put(item, getItems(item));
            set.remove(item);
        }

        for (Item item : set) {
            inventoryItems.remove(item);
        }

        //populate
        toTrack.clear();

    }


}
