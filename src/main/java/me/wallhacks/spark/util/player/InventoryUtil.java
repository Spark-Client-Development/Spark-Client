package me.wallhacks.spark.util.player;

import me.wallhacks.spark.util.MC;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryUtil implements MC {


    public static int FindItemInInventory(Item input, boolean searchInHotbar,boolean searchInOffhand){
        List<Integer> l = FindItemsInInventory(input,searchInHotbar,searchInOffhand);
        if(l.size() >= 1)
            return l.get(0);
        return -1;
    }
    public static List<Integer> FindItemsInInventory(Item input, boolean searchInHotbar,boolean searchInOffhand)
    {
        ArrayList<Integer> l = new ArrayList<Integer>();

        for (int i = 1; i < mc.player.inventoryContainer.getInventory().size(); i++) {
            if (i == 0 || i == 5 || i == 6 || i == 7 || i == 8)
                continue;
            if(!searchInHotbar) if (i >= 36 && i <= 44) continue;
            if(!searchInOffhand) if (i == 45) continue;

            final ItemStack s = mc.player.inventoryContainer.getInventory().get(i);
            if (s.isEmpty())
                continue;
            if (s.getItem() == input) {
                l.add(i);
            }
        }

        return l;
    }

    public static boolean getHeldItem(Item item) {
        return mc.player.getHeldItemMainhand().getItem().equals(item);
    }

    public static boolean notInInv(Item itemOfChoice) {
        for (int i = 35; i >= 0; i--) {
            Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (item == itemOfChoice)
                return false;
        }
        return true;
    }

    public static float getValueOfArmorItem(ItemStack item){

        if(!(item.getItem() instanceof ItemArmor))
            return 0;

        int protLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, item);
        int blastLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, item);

        final ItemArmor armor = (ItemArmor) item.getItem();

        int ArmorValue = protLevel*2 + blastLevel + armor.damageReduceAmount*6;


        return ArmorValue;
    }

    public static float getValueOfWeaponItem(ItemStack item){

        int sharpLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, item);
        int fireLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, item);

        float Value = fireLevel*0.5f + sharpLevel;

        if(item.getItem() instanceof ItemSword)
            Value += ((ItemSword) item.getItem()).attackDamage * 1.6;
        if(item.getItem() instanceof ItemTool)
            Value += ((ItemTool) item.getItem()).attackDamage;




        return Value;
    }

    public static void throwItem(int slot){

        if (!(mc.currentScreen instanceof GuiInventory) && mc.currentScreen != null && mc.currentScreen instanceof GuiContainer)
            return;

        mc.playerController.windowClick(0, slot, 1, ClickType.THROW,  mc.player);


    }
    public static void moveItem(int slot,int slot2){


        if (!(mc.currentScreen instanceof GuiInventory) && mc.currentScreen != null && mc.currentScreen instanceof GuiContainer)
            return;

        boolean isTargetSlotEmpty = mc.player.inventoryContainer.getInventory().get(slot2).isEmpty();


        mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP,  mc.player);
        mc.playerController.windowClick(0, slot2, 0, ClickType.PICKUP,  mc.player);

        if(!isTargetSlotEmpty)
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP,  mc.player);

        mc.playerController.updateController();
    }
    public static Item[] getListOfItems(){
        ArrayList<Item> bs = new ArrayList<Item>();

        Item.REGISTRY.forEach( (Block) -> {
            bs.add(Block);
        });

        return ((List<Item>)bs).toArray(new Item[bs.size()]);
    }


    public static int getSlotIdFromInventoryId(int slot){
        int interSlot = slot;
        if (interSlot < 9 && interSlot >= 0)
            interSlot += 36;
        else if (interSlot > 35 && interSlot < 40)
            interSlot = (44-interSlot);
        else if (interSlot == 40)
            interSlot = 45;


        return interSlot;
    }

    public static Map<Integer, ItemStack> getInventory() {
        return getInvSlots(9, 35);
    }

    public static Map<Integer, ItemStack> getHotbar() {
        return getInvSlots(36, 44);
    }

    public static Map<Integer, ItemStack> getInvSlots(int current, final int last) {
        final Map<Integer, ItemStack> fullInventorySlots = new HashMap<>();
        while (current <= last) {
            fullInventorySlots.put(current, mc.player.inventoryContainer.getInventory().get(current));
            current++;
        }
        return fullInventorySlots;
    }
}
