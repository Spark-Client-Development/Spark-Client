package me.wallhacks.spark.util.player.itemswitcher;

import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.player.InventoryUtil;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import java.util.Arrays;
import java.util.List;


public class ItemSwitcher implements MC {



	public static abstract class SwitchResult {

	}

	public static class NoSwitchResult extends SwitchResult {
		final EnumHand hand;

		public NoSwitchResult(EnumHand hand) {
			this.hand = hand;
		}

		public EnumHand getHand() {
			return hand;
		}
	}
	public static class HotbarSwitchResult extends SwitchResult {
		final EnumHand hand;
		final int slot;
		public HotbarSwitchResult(EnumHand hand, int slot) {
			this.hand = hand;
			this.slot = slot;
		}

		public EnumHand getHand() {
			return hand;
		}

		public int getSlot() {
			return slot;
		}
	}
	public static class InventorySwitchResult extends SwitchResult {
		final int slot;
		public InventorySwitchResult(int slot) {
			this.slot = slot;
		}
		public int getSlot() {
			return slot;
		}


	}



	public static List<String> modes = Arrays.asList("Normal","Silent","Const","Off");

	public enum usedHand {
		Mainhand,Offhand,Both
	}
	public enum switchType {
		Normal,Silent, Const,NoSwitch
	}


	public static int FindStackInInventory(SwitchItem input,boolean allowOffhand)
	{

		int best = -1;
		float bestValue = 0;

		for (int i = 0; i < mc.player.inventoryContainer.getInventory().size(); i++) {
			if (i == 0 || i == 5 || i == 6 || i == 7 || i == 8)
				continue;
			if(!allowOffhand && i == 45)
				continue;

			final ItemStack s = mc.player.inventoryContainer.getInventory().get(i);
			if (s.isEmpty())
				continue;
			float value = input.isItemGood(s);
			if(i == mc.player.inventory.currentItem+36)
				value*=1.1;
			else if(i == 45)
				value*=1.1;
			if (value > bestValue) {
				best = i;
				bestValue = value;
			}
		}

		return best;
	}



	public static void ConstSwitch(int slot,int slot1) {


		mc.playerController.windowClick(0,(slot), 0, ClickType.PICKUP,  mc.player);

		mc.playerController.windowClick(0,(slot1), 0, ClickType.PICKUP,  mc.player);
	}
}
