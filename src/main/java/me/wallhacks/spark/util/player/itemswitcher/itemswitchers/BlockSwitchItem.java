package me.wallhacks.spark.util.player.itemswitcher.itemswitchers;

import me.wallhacks.spark.util.player.itemswitcher.SwitchItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;

public class BlockSwitchItem extends SwitchItem {
	public BlockSwitchItem(){
		super();
	}
	public float isItemGood(ItemStack item){
		return isItemBlock(item.getItem()) ? 1 : 0;
	}
	public boolean isItemBlock(Item item){
		return item instanceof ItemBlock || item instanceof ItemBlockSpecial;
	}
	public Block getItemBlock(Item item){
	
	
		
		
		
		if(item instanceof ItemBlock)
			return ((ItemBlock)item).getBlock();
		else if(item instanceof ItemBlockSpecial)
			return ((ItemBlockSpecial)item).getBlock();
		else
			return null;
	}

}
