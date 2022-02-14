package me.wallhacks.spark.util.player.itemswitcher.itemswitchers;

import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;

public class ShulkerSwitchItem extends BlockSwitchItem {
    @Override
    public float isItemGood(ItemStack item){
        return item.getItem() instanceof ItemShulkerBox ? 1 : 0;
    }
}
