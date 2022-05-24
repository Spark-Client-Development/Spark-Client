package me.wallhacks.spark.manager;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.player.InventoryUtil;
import me.wallhacks.spark.util.player.itemswitcher.ItemSwitcher;
import me.wallhacks.spark.util.player.itemswitcher.SwitchItem;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;

public class SwitchManager implements MC {
    public SwitchManager() {
        Spark.eventBus.register(this);
    }

    int delay = 0;

    boolean didInventorySwitch = false;
    int fromInvSlot;
    int toInvSlot;
    int realSlot;




    boolean scheduleHotbarSwitch = false;
    int switchTo = 0;
    int switchFrom = 0;
    int hotbarSwitchDelay = 0;

    public void setScheduleHotbarSwitch(int switchTo,int hotbarSwitchDelay){
        if(scheduleHotbarSwitch)
            return;
        scheduleHotbarSwitch = true;
        this.switchFrom = mc.player.inventory.currentItem;
        this.switchTo = switchTo;
        this.hotbarSwitchDelay = hotbarSwitchDelay;
    }

    public void setDoInvSwitch(int fromInvSlot,int toInvSlot,int delay){
        if(didInventorySwitch)
            return;
        didInventorySwitch = true;
        this.delay = delay;
        this.fromInvSlot = fromInvSlot;
        this.toInvSlot = toInvSlot;
    }




    public void OnLateUpdate() {

        if(didInventorySwitch)
        {
            if(delay <= 0)
            {
                if(fromInvSlot != toInvSlot)
                    InventoryUtil.constSwitchMove(fromInvSlot,toInvSlot);

                didInventorySwitch = false;
            }
            delay--;

        }
        if(realSlot != -1)
        {
            mc.player.inventory.currentItem = realSlot;
            mc.playerController.syncCurrentPlayItem();
            realSlot = -1;
        }

        if(scheduleHotbarSwitch)
        {
            if(hotbarSwitchDelay <= 0)
            {
                if(switchFrom == mc.player.inventory.currentItem){
                    mc.player.inventory.currentItem = switchTo;
                    mc.playerController.syncCurrentPlayItem();
                }

                scheduleHotbarSwitch = false;
            }
            hotbarSwitchDelay--;

        }

    }







    public EnumHand SwitchUsingHotbar(SwitchItem switcher, ItemSwitcher.usedHand handType, boolean sync){

        ItemSwitcher.SwitchResult res = getCalculateAction(switcher, handType, ItemSwitcher.switchType.Normal);

        if(res instanceof ItemSwitcher.HotbarSwitchResult)
        {
            mc.player.inventory.currentItem = ((ItemSwitcher.HotbarSwitchResult)res).getSlot();
            if(sync)
                mc.playerController.syncCurrentPlayItem();
            return ((ItemSwitcher.HotbarSwitchResult)res).getHand();
        }

        return null;
    }


    public EnumHand Switch(SwitchItem switcher, ItemSwitcher.usedHand handType, String mode) {
        return Switch(switcher,handType, getModeFromString(mode));

    }

    public EnumHand Switch(SwitchItem switcher, ItemSwitcher.usedHand handType, String mode,int delay) {
        return Switch(switcher,handType, getModeFromString(mode),delay);

    }

    public ItemSwitcher.switchType getModeFromString(String mode) {
        if(mode.equalsIgnoreCase("off"))
            return ItemSwitcher.switchType.NoSwitch;
        if(mode.equalsIgnoreCase("fastswap"))
            return ItemSwitcher.switchType.SwitchBack;
        if(mode.equalsIgnoreCase("constSwitch"))
            return ItemSwitcher.switchType.Const;
        try {
            return ItemSwitcher.switchType.valueOf(mode);
        }
        catch(Exception e) {
            return ItemSwitcher.switchType.Normal;
        }
    }

    public EnumHand Switch(SwitchItem switcher, ItemSwitcher.usedHand handType) {
        return Switch(switcher,handType, ItemSwitcher.switchType.Normal);

    }

    public EnumHand Switch(SwitchItem switcher, ItemSwitcher.usedHand handType, ItemSwitcher.switchType switchType) {
        return Switch(switcher,handType,switchType,5);
    }

    public EnumHand Switch(SwitchItem switcher, ItemSwitcher.usedHand handType, ItemSwitcher.switchType switchType,int switchBackDelay){

        if(switchType == ItemSwitcher.switchType.Const)
           InventoryUtil.PlaceDownItemInMoveItemStack();


        ItemSwitcher.SwitchResult res = getCalculateAction(switcher, handType, switchType);

        return Switch(res,switchType,switchBackDelay);
    }
    public EnumHand Switch(ItemSwitcher.SwitchResult res,ItemSwitcher.switchType switchType,int switchBackDelay){


        if(res instanceof ItemSwitcher.NoSwitchResult)
        {
            return ((ItemSwitcher.NoSwitchResult)res).getHand();
        }
        else if(res instanceof ItemSwitcher.HotbarSwitchResult)
        {
            int old = mc.player.inventory.currentItem;
            if(switchType == ItemSwitcher.switchType.Silent && realSlot == -1)
                realSlot = old;

            mc.player.inventory.currentItem = ((ItemSwitcher.HotbarSwitchResult)res).getSlot();
            mc.playerController.syncCurrentPlayItem();

            if(switchType == ItemSwitcher.switchType.SwitchBack)
            {
                if(!scheduleHotbarSwitch)
                    setScheduleHotbarSwitch(old,switchBackDelay);
                else if(switchFrom == mc.player.inventory.currentItem)
                    hotbarSwitchDelay = switchBackDelay;

            }

            return ((ItemSwitcher.HotbarSwitchResult)res).getHand();
        }
        else if(res instanceof ItemSwitcher.InventorySwitchResult)
        {

            int slot = ((ItemSwitcher.InventorySwitchResult)res).getSlot();
            if(slot != mc.player.inventory.currentItem+36)
            {
                InventoryUtil.constSwitchMove(slot,mc.player.inventory.currentItem+36);
                setDoInvSwitch(mc.player.inventory.currentItem+36,slot,switchBackDelay);
            }
            else
                delay = switchBackDelay;


            return EnumHand.MAIN_HAND;
        }


        return null;
    }



    //predict what item will switcher switch to
    public Item predictItem(SwitchItem switcher, ItemSwitcher.usedHand handType, ItemSwitcher.switchType type) {
        ItemSwitcher.SwitchResult res = getCalculateAction(switcher,handType,type);
        if(res instanceof ItemSwitcher.NoSwitchResult)
        {
            return mc.player.getHeldItem(((ItemSwitcher.NoSwitchResult)res).getHand()).item;
        }
        else if(res instanceof ItemSwitcher.HotbarSwitchResult)
        {
            if(((ItemSwitcher.HotbarSwitchResult)res).getHand() == EnumHand.OFF_HAND)
                return mc.player.getHeldItemOffhand().getItem();

            return mc.player.inventory.getStackInSlot(((ItemSwitcher.HotbarSwitchResult)res).getSlot()).getItem();
        }
        else if(res instanceof ItemSwitcher.InventorySwitchResult)
        {

            return mc.player.inventoryContainer.getInventory().get(((ItemSwitcher.InventorySwitchResult)res).getSlot()).getItem();
        }


        return null;
    }

    //gets action that needs to be done for switching to item
    public ItemSwitcher.SwitchResult getCalculateAction(SwitchItem switcher, ItemSwitcher.usedHand handType, ItemSwitcher.switchType type){

        if(type == ItemSwitcher.switchType.NoSwitch || handType == ItemSwitcher.usedHand.Offhand)
        {
            float main = ((handType == ItemSwitcher.usedHand.Both || handType == ItemSwitcher.usedHand.Mainhand) ? switcher.isItemGood(mc.player.getHeldItemMainhand()) : 0);
            float off = ((handType == ItemSwitcher.usedHand.Both || handType == ItemSwitcher.usedHand.Offhand) ? switcher.isItemGood(mc.player.getHeldItemOffhand()) : 0);

            if(main > 0 || off > 0)
                return new ItemSwitcher.NoSwitchResult(main > off ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
        }
        else if(type == ItemSwitcher.switchType.Const)
        {
            int id = ItemSwitcher.FindStackInInventory(switcher,handType != ItemSwitcher.usedHand.Mainhand);

            if(id == 45)
                return new ItemSwitcher.NoSwitchResult(EnumHand.OFF_HAND);
            if(id == mc.player.inventory.currentItem+36)
                return new ItemSwitcher.InventorySwitchResult(id);
            if(id != -1 && !didInventorySwitch)
                return new ItemSwitcher.InventorySwitchResult(id);
        }
        else if(type == ItemSwitcher.switchType.Normal || type == ItemSwitcher.switchType.Silent || type == ItemSwitcher.switchType.SwitchBack)
        {
            float Best = 0;
            EnumHand hand = null;

            if(handType == ItemSwitcher.usedHand.Both || handType == ItemSwitcher.usedHand.Offhand || type == ItemSwitcher.switchType.NoSwitch)
            {
                float offhandVal = switcher.isItemGood(mc.player.getHeldItemOffhand());
                if(offhandVal > Best)
                {
                    Best = offhandVal;
                    hand = EnumHand.OFF_HAND;
                }
            }

            int newSlot = mc.player.inventory.currentItem;
            if(handType == ItemSwitcher.usedHand.Both || handType == ItemSwitcher.usedHand.Mainhand)
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
            else if (type == ItemSwitcher.switchType.NoSwitch) {

                float val = switcher.isItemGood(mc.player.getHeldItemMainhand())*1.1f;
                if(val > Best)
                {
                    Best = val;
                    hand = EnumHand.MAIN_HAND;
                }
            }

            if(Best > 0)
                return new ItemSwitcher.HotbarSwitchResult(hand, newSlot);


        }

        return null;



    }


}
