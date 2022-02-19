package me.wallhacks.spark.util.player.itemswitcher.itemswitchers;

import me.wallhacks.spark.util.player.itemswitcher.SwitchItem;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;

public class BlockSwitchItem extends SwitchItem {
	public BlockSwitchItem(){
		super();
	}
	public float isItemGood(ItemStack item){
		return isItemBlock(item.getItem()) ? 1 : 0;
	}
	public boolean isItemBlock(Item item){
		return item instanceof ItemBlock || item instanceof ItemBlockSpecial || item instanceof ItemSkull || item == Items.SKULL;
	}
	public Block getItemBlock(Item item){

		
		if(item instanceof ItemBlock)
			return ((ItemBlock)item).getBlock();
		else if(item instanceof ItemBlockSpecial)
			return ((ItemBlockSpecial)item).getBlock();
		else if(item == Items.SKULL)
			return Blocks.SKULL;
		else if(item instanceof ItemSkull)
			return Blocks.SKULL;
		else
			return null;
	}

}
