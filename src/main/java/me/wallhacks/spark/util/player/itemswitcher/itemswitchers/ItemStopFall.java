package me.wallhacks.spark.util.player.itemswitcher.itemswitchers;

import me.wallhacks.spark.util.player.itemswitcher.SwitchItem;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;


public class ItemStopFall extends BlockSwitchItem {

	public ItemStopFall(){
		super();
	}
	@Override
	public float isItemGood(ItemStack item){


		if(item.getItem() == Items.WATER_BUCKET)
			return 3;
		if(isItemBlock(item.getItem()))
		{
			Block b = getItemBlock(item.getItem());

			if(b == Blocks.WEB)
				return 2;
			if(b == Blocks.HAY_BLOCK)
				return 1;

		}



		return 0;
	}

}