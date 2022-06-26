package me.wallhacks.spark.systems.module.modules.player;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.manager.SystemManager;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.SettingGroup;
import me.wallhacks.spark.util.FileUtil;
import me.wallhacks.spark.util.objects.Timer;
import me.wallhacks.spark.util.player.InventoryUtil;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.item.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.ItemListSelectSetting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Module.Registration(name = "InventoryManager", description = "Steals from chests")
public class InventoryManager extends Module {

    BooleanSetting onlyIfInventoryOpen = new BooleanSetting("OnlyInInventory", this, true);
    IntSetting delay = new IntSetting("Delay", this, 5, 0, 30);

    BooleanSetting SortInventory = new BooleanSetting("SortInventory", this, false);

    BooleanSetting RemoveNonKits = new BooleanSetting("RemoveNonKitItems", this, false, v -> SortInventory.isOn());
    BooleanSetting RemoveUseless = new BooleanSetting("RemoveUseless", this, false);

    ItemListSelectSetting useless = new ItemListSelectSetting("Useless", this, new Item[]{Item.getItemFromBlock(Blocks.NETHERRACK), Items.ROTTEN_FLESH});

    SettingGroup stealer = new SettingGroup("Stealer", this);
    BooleanSetting autoSteal = new BooleanSetting("Auto", stealer, false);
    IntSetting stealDelay = new IntSetting("Delay", stealer, 5, 0, 30);
    BooleanSetting stealKitNeeded = new BooleanSetting("OnlyForKit", stealer, false);


    public boolean isAuto(GuiContainer container) {
        return autoSteal.isOn() && !(container instanceof GuiChest && ((GuiChest)container).lowerChestInventory.getDisplayName().getUnformattedComponentText().equals("Ender Chest"));
    }


    HashMap<String,Item[]> kits = new HashMap<>();
    List<String> kitNames = new ArrayList<>();

    public Map<String, Item[]> getKits() {
        return kits;

    }
    
    public List<String> getKitNames() {
		return kitNames;
	}

    public static InventoryManager instance;
    public InventoryManager() {
        instance = this;

        LoadKits();
    }



    public String currentKit;

    Item[] perfectInventory(){
        return kits.containsKey(currentKit) ? kits.get(currentKit) : null;
    }


    public void selectKit(String name){
        if(kits.containsKey(name))
            currentKit = name;
        refreshSelected();
    }
    public void setKitFromInventory(String name,boolean hotbarOnly){

        Item[] inv = new Item[36];
        for (int i = 0; i < Math.min(inv.length,hotbarOnly ? 9 : 36) ; i++) {
            if(mc.player.inventory.getStackInSlot(i).getItem() == Items.AIR)
                inv[i] = null;
            else
                inv[i] = mc.player.inventory.getStackInSlot(i).getItem();
        }
        kits.put(name,inv);
        kitNames.add(name);
    }
    public void setKit(String name,Item[] inv){
        kits.put(name,inv);
        kitNames.add(name);
        refreshSelected();
    }
    public void deleteKit(String name){

        kits.remove(name);
        kitNames.remove(name);
        refreshSelected();
    }
    public boolean createEmptyKit(String name){
        for (int i = 0; i < 300; i++) {
            String newN = name+(i == 0 ? "" : i);
            if(!kits.containsKey(newN))
            {
                setKit(newN,new Item[36]);
                return true;
            }
        }
        return false;
    }
    public void refreshSelected() {
        if(kits.size() <= 0)
            currentKit = null;
        else if(!kits.containsKey(currentKit))
            currentKit = kits.keySet().iterator().next();

    }






    int curSlotIndex = 0;
    Timer timer = new Timer();


    @Override
    public void onEnable() {
        curSlotIndex = 0;

    }

    public boolean isCleaning (){
        return canCleaning() && !isDoneCleaning() && this.isEnabled();
    }

    boolean canCleaning (){
        return ((!(mc.currentScreen instanceof GuiContainer) || mc.currentScreen instanceof GuiInventory) && (!onlyIfInventoryOpen.isOn() || mc.currentScreen instanceof GuiInventory));
    }


    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {

        if(!timer.passedMs(0))
            return;

        if((mc.currentScreen instanceof GuiChest || mc.currentScreen instanceof GuiShulkerBox) && isStealing){
            if(isStealing)
                handleContainer();
        }
        else
        {
            isStealing = false;

            if(canCleaning())
            {
                if(isDoneCleaning()){
                    if(onlyIfInventoryOpen.isOn())
                        return;
                }
                Clean();

            }
            else
                curSlotIndex = 0;
        }









    }


    boolean isDoneCleaning(){
        return curSlotIndex >= 36;
    }
    void Clean(){

        if(isDoneCleaning())
            curSlotIndex = 0;

        while (!isDoneCleaning()) {
            handleSlot(curSlotIndex);
            //we need this check here LOL
            curSlotIndex++;
            if(!timer.passedMs(0))
                return;


        }




    }



    void handleSlot(int slot){
        int s = InventoryUtil.getSlotIdFromInventoryId(slot);
        ItemStack itemStack = (ItemStack) mc.player.inventoryContainer.getInventory().get(s);

        if(itemStack.getItem() == null || itemStack.getItem() == Items.AIR)
            return;

        if(!KeepItemStack(itemStack))
        {
            InventoryUtil.throwItem(s);
            timer.delayRandom(delay.getValue()*10,delay.getValue()*3);
        }
        else if(SortInventory.isOn()) {

            if (perfectInventory() != null) {

                for (int i = 0; i < perfectInventory().length; i++) {

                    Item item = perfectInventory()[i];
                    int sloti = InventoryUtil.getSlotIdFromInventoryId(i);
                    if (sloti != s && item != null) {
                        if (!invenotrySortIsItemSame(item, mc.player.inventoryContainer.getInventory().get(sloti).getItem()))
                            if (invenotrySortIsItemSame(item, itemStack.getItem()))
                                if (perfectInventory()[slot] == null || !invenotrySortIsItemSame(perfectInventory()[slot], itemStack.getItem())) {
                                    InventoryUtil.moveItem(s, sloti);
                                    timer.delayRandom(delay.getValue()*10, delay.getValue()*3);
                                    break;
                                }
                    }
                }
            }
        }
    }

    boolean invenotrySortIsItemSame(Item i1,Item i2){

        if(i1.equals(i2))
            return true;
        if((i1 instanceof ItemSword && i2 instanceof ItemSword) ||
                (i1 instanceof ItemAxe && i2 instanceof ItemAxe) ||
                (i1 instanceof ItemShield && i2 instanceof ItemShield) ||
                (i1 instanceof ItemPickaxe && i2 instanceof ItemPickaxe))
            return true;

        return false;
    }



    public boolean KeepItemStack(ItemStack itemStack){
        if(RemoveNonKits.isOn() && itemNeededInPerfectKit(itemStack) == -1)
            return false;
        if(RemoveUseless.isOn() && useless.isValueSelected(itemStack.getItem()))
            return false;
        return true;
    }


    public int itemNeededInPerfectKit(ItemStack itemStack){

        if (perfectInventory() != null) {

            for (int i = 0; i < perfectInventory().length; i++) {

                Item item = perfectInventory()[i];
                int sloti = InventoryUtil.getSlotIdFromInventoryId(i);
                if (item != null && !invenotrySortIsItemSame(mc.player.inventoryContainer.getInventory().get(sloti).getItem(),item) && invenotrySortIsItemSame(item,itemStack.getItem())) {
                    return i;
                }
            }
        }

        return -1;
    }









    boolean isStealing = false;

    public void StartSteal(){
        isStealing = true;
        timer.delayRandom(stealDelay.getValue()*10,stealDelay.getValue()*3);
    }


    void handleContainer(){
        if (mc.currentScreen instanceof GuiChest || mc.currentScreen instanceof GuiShulkerBox)
        {
            Container container = ((GuiContainer) mc.currentScreen).inventorySlots;

            ArrayList<Integer> list = new ArrayList<>();

            int slots = (mc.currentScreen instanceof GuiShulkerBox ? ((GuiShulkerBox) mc.currentScreen).inventory : ((GuiChest) mc.currentScreen).lowerChestInventory).getSizeInventory();

            int slot = 0;
            while(slot < slots)
            {
                ItemStack l_Stack = container.getSlot(slot).getStack();

                if (!l_Stack.isEmpty() && l_Stack.getItem() != Items.AIR)
                {
                    if(KeepItemStack(l_Stack))
                    {
                        int i = itemNeededInPerfectKit(l_Stack);

                        if(i >= 0)
                        {
                            int convert = i;
                            if(convert < 9)
                                convert+=27;
                            else
                                convert-=9;


                            InventoryUtil.moveItemInContainer(slot,convert + slots);
                            timer.delayRandom(stealDelay.getValue()*10,stealDelay.getValue()*3);
                            return;

                        }
                        else
                            list.add(slot);
                    }
                }
                slot++;

            }

            if(!stealKitNeeded.isOn())
            while (list.size() > 0)
            {
                mc.playerController.windowClick(container.windowId, list.get(0), 0, ClickType.QUICK_MOVE,  mc.player);
                timer.delayRandom(stealDelay.getValue()*10,stealDelay.getValue()*3);
                return;
            }



            isStealing = false;
            slot = 0;

        }
    }







    String getKitsFile(){
        String base = Spark.ParentPath.getAbsolutePath() + ""+System.getProperty("file.separator")+""+this.getName()+""+System.getProperty("file.separator")+"";
        return base + "kits.sex";
    }


    public void LoadKits() {
        super.onConfigLoad();

        try {
            String s = FileUtil.read(getKitsFile());
            if(s != null) {
                String[] List = s.split("\n");
                currentKit = List[0];
                for (int i = 1; i < List.length; i++) {
                    String m = List[i];
                    String[] split = m.split(":");
                    Item[] items = new Item[36];
                    for(int y = 0; y < items.length; y++)
                        items[y] = Item.getItemById(Integer.parseInt(split[1].split(",")[y]));
                    setKit(split[0],items);

                }
                selectKit(List[0]);
            }
            else
            {
                //default kits
                {
                    Item[] items = new Item[36];
                    items[0] = Items.DIAMOND_SWORD;
                    items[1] = Items.GOLDEN_APPLE;
                    items[8] = Items.DIAMOND_PICKAXE;
                    setKit("Basic",items);
                }
                {
                    Item[] items = new Item[36];
                    items[0] = Items.DIAMOND_SWORD;
                    items[1] = Items.GOLDEN_APPLE;
                    items[1] = Items.END_CRYSTAL;
                    items[3] = Items.POTIONITEM;
                    items[4] = Items.CHORUS_FRUIT;
                    items[5] = Items.EXPERIENCE_BOTTLE;
                    items[6] = Item.getItemFromBlock(Blocks.ENDER_CHEST);
                    items[7] = Item.getItemFromBlock(Blocks.OBSIDIAN);
                    items[8] = Items.DIAMOND_PICKAXE;
                    setKit("Dvd",items);
                }
                {
                    Item[] items = new Item[36];
                    items[0] = Items.DIAMOND_HOE;
                    items[1] = Items.DIAMOND_PICKAXE;
                    items[2] = Items.DIAMOND_SHOVEL;
                    items[3] = Items.DIAMOND_AXE;
                    items[4] = Items.DIAMOND_SWORD;
                    items[5] = Items.GOLDEN_APPLE;
                    items[6] = Items.ENDER_PEARL;
                    items[7] = Items.COOKED_BEEF;
                    items[8] = Item.getItemFromBlock(Blocks.OBSIDIAN);
                    items[9] = Items.ENDER_PEARL;
                    items[10] = Items.ENDER_PEARL;
                    items[11] = Items.ENDER_PEARL;
                    items[12] = Items.ENDER_PEARL;
                    items[13] = Items.ENDER_PEARL;
                    items[14] = Item.getItemFromBlock(Blocks.OBSIDIAN);;
                    items[15] = Item.getItemFromBlock(Blocks.OBSIDIAN);;
                    items[16] = Item.getItemFromBlock(Blocks.OBSIDIAN);
                    items[17] = Item.getItemFromBlock(Blocks.OBSIDIAN);
                    items[18] = Items.TOTEM_OF_UNDYING;
                    items[19] = Items.TOTEM_OF_UNDYING;
                    items[20] = Items.TOTEM_OF_UNDYING;
                    items[21] = Items.TOTEM_OF_UNDYING;
                    items[22] = Items.TOTEM_OF_UNDYING;
                    items[23] = Items.TOTEM_OF_UNDYING;
                    items[24] = Items.TOTEM_OF_UNDYING;
                    items[25] = Items.TOTEM_OF_UNDYING;
                    items[26] = Items.TOTEM_OF_UNDYING;
                    items[27] = Items.TOTEM_OF_UNDYING;
                    items[28] = Items.TOTEM_OF_UNDYING;
                    items[29] = Items.TOTEM_OF_UNDYING;
                    items[30] = Items.TOTEM_OF_UNDYING;
                    items[31] = Items.TOTEM_OF_UNDYING;
                    items[32] = Items.TOTEM_OF_UNDYING;
                    items[33] = Items.TOTEM_OF_UNDYING;
                    items[34] = Items.TOTEM_OF_UNDYING;
                    items[35] = Items.TOTEM_OF_UNDYING;
                    setKit("Wallhacks",items);
                }

            }
        } catch (Exception e) {
            Spark.logger.info("Failed to load kits");
            e.printStackTrace();
        }
    }


    public void SaveKits() {
        super.onConfigSave();

        try {
            ArrayList<String> lines = new ArrayList<String>();

            lines.add(SystemManager.getModule(InventoryManager.class).currentKit);


            for (String n:kits.keySet()) {
                String in = n+":";
                Item[] items = kits.get(n);
                for(int y = 0; y < items.length; y++)
                    in = in + Item.getIdFromItem(items[y]) + ",";
                lines.add(in);
            }

            String content = "";
            for(String e : lines)
                content = content + e + "\n";

            FileUtil.write(getKitsFile(),content);

        } catch (Exception e) {
            Spark.logger.info("Failed to save kits");
            e.printStackTrace();
        }
    }
}
