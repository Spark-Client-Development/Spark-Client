package me.wallhacks.spark.util.player.itemswitcher.itemswitchers;

import me.wallhacks.spark.util.player.InventoryUtil;
import me.wallhacks.spark.util.player.itemswitcher.SwitchItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public class ItemForMineSwitchItem extends SwitchItem {
	public ItemForMineSwitchItem(IBlockState b){
		super();
		block = b;

	}
	final IBlockState block;
	public float isItemGood(ItemStack item){
		
		float speed = InventoryUtil.getDestroySpeed(item,block);
		return speed;
	}
}
