package me.wallhacks.spark.util.player.itemswitcher.itemswitchers;


import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class SpecBlockSwitchItem extends BlockSwitchItem {
    public SpecBlockSwitchItem(Block b){
        super();
        block = b;
    }
    final Block block;
    public float isItemGood(ItemStack item){
        return (super.isItemBlock(item.getItem()) && getItemBlock(item.getItem()).equals(block)) ? 1 : 0;
    }

}
