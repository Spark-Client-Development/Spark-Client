package me.wallhacks.spark.util.player.itemswitcher.itemswitchers;


import me.wallhacks.spark.Spark;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class SpecBlockSwitchItem extends BlockSwitchItem {
    public SpecBlockSwitchItem(Block b){
        super();
        block = b;
    }
    final Block block;
    public float isItemGood(ItemStack item){

        if(block == Blocks.SKULL)
            return (item.getItem() == Items.SKULL) ? 1 : 0;
        return (super.isItemBlock(item.getItem()) && getItemBlock(item.getItem()).equals(block)) ? 1 : 0;
    }

}
