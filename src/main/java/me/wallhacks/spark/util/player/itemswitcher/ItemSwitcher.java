package me.wallhacks.spark.util.player.itemswitcher;

import me.wallhacks.spark.util.MC;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;


public class ItemSwitcher implements MC {


	public static EnumHand Switch(SwitchItem switcher,switchType type){
		
		SwitchResult res = getCalculateAction(switcher,type);

		if(res == null)
			return null;

		mc.player.inventory.currentItem = res.slot;

		
		return res.hand;
		
		
	}

	//predict what item will switcher switch to
	public static Item predictItem(SwitchItem switcher,switchType type) {
		SwitchResult res = getCalculateAction(switcher,type);
		if(res == null)
			return null;

		if(res.hand == EnumHand.OFF_HAND)
			return mc.player.getHeldItemOffhand().getItem();

		return mc.player.inventory.getStackInSlot(res.slot).getItem();
	}

	//gets action that needs to be done for switching to item
	public static SwitchResult getCalculateAction(SwitchItem switcher,switchType type){


		float Best = 0;
		EnumHand hand = null;

		if(type == switchType.Both || type == switchType.Offhand || type == switchType.NoSwitch)
		{
			float offhandVal = switcher.isItemGood(mc.player.getHeldItemOffhand());
			if(offhandVal > Best)
			{
				Best = offhandVal;
				hand = EnumHand.OFF_HAND;
			}
		}

		int newSlot = mc.player.inventory.currentItem;
		if(type == switchType.Both || type == switchType.Mainhand)
		{
			int i = 0;


			while(i < 9){

				float val = switcher.isItemGood(mc.player.inventory.getStackInSlot(i));
				if(i == mc.player.inventory.currentItem)
					val*=1.1f;
				if(val > Best)
				{
					newSlot = i;
					Best = val;
					hand = EnumHand.MAIN_HAND;
				}

				i++;
			}
		}
		else if (type == switchType.NoSwitch) {

			float val = switcher.isItemGood(mc.player.getHeldItemMainhand())*1.1f;
			if(val > Best)
			{
				Best = val;
				hand = EnumHand.MAIN_HAND;
			}
		}

		if(Best > 0)
			return new SwitchResult(hand,newSlot);
		else
			return null;


	}


	public static class SwitchResult {
		final EnumHand hand;
		final int slot;
		public SwitchResult(EnumHand hand,int slot) {
			this.hand = hand;
			this.slot = slot;
		}
	}

	public enum switchType {
		Mainhand,Offhand,Both,NoSwitch
	}

}
