package me.wallhacks.spark.util.player.itemswitcher.itemswitchers;

import me.wallhacks.spark.util.player.itemswitcher.SwitchItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SpecItemSwitchItem extends SwitchItem {
    public SpecItemSwitchItem(Item b){
        super();
        it = b;
    }
    final Item it;
    public float isItemGood(ItemStack item){
        return it.equals(item.getItem()) ? 1 : 0;
    }

}
