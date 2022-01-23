package me.wallhacks.spark.util.player.itemswitcher.itemswitchers;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;



public class SolidBlockSwitchItem extends BlockSwitchItem {
	public float isItemGood(ItemStack item){
		return (isItemBlock(item.getItem()) && getMat(item).isSolid()) ? (getMat(item).isOpaque() ? 2.2f : 2) : 0;
	}
	public Material getMat(ItemStack item){
		return getItemBlock(item.getItem()).material;
	}
	public SolidBlockSwitchItem(){
		super();
	}
}