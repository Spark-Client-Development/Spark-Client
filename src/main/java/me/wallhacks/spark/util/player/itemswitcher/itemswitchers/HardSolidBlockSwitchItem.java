package me.wallhacks.spark.util.player.itemswitcher.itemswitchers;

import net.minecraft.item.ItemStack;



public class HardSolidBlockSwitchItem extends SolidBlockSwitchItem {
	
	public HardSolidBlockSwitchItem(){
		super();
	}
	public float isItemGood(ItemStack item){

		return super.isItemBlock(item.getItem()) && super.isItemGood(item) > 0 ? getItemBlock(item.getItem()).getExplosionResistance(null) + super.isItemGood(item) : 0;
	}

}