package me.wallhacks.spark.util.player.itemswitcher.itemswitchers;

import me.wallhacks.spark.util.player.itemswitcher.SwitchItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public class ItemForFightSwitchItem extends SwitchItem {
	public ItemForFightSwitchItem(Entity b,boolean _useAttackSpeed){
		super();
		entity = b;
		useAttackSpeed = _useAttackSpeed;
	}
	public ItemForFightSwitchItem(Entity b){
		super();
		entity = b;
		useAttackSpeed = true;
	}
	public ItemForFightSwitchItem(){
		super();
		entity = null;
		useAttackSpeed = true;
	}
	final boolean useAttackSpeed;
	final Entity entity;
	public float isItemGood(ItemStack it){
	
		float attackDam = 1f;
		if(it.getItem() instanceof ItemTool) 
			attackDam = ((ItemTool)it.getItem()).attackDamage;

		if(it.getItem() instanceof ItemSword) 
			attackDam = ((ItemSword)it.getItem()).attackDamage * (useAttackSpeed ? 1.6f : 1);
		
		if(entity instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)entity;
			if(ep.isHandActive() && ep.getHeldItem(ep.getActiveHand()).getItem() == Items.SHIELD)
			{
				if(it.getItem().getTranslationKey().contains("hatchet"));
					attackDam+=10;
			}		
		}
		
		
		return attackDam;
	}
}
