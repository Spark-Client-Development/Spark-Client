package me.wallhacks.spark.systems.module.modules.mics;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.manager.SystemManager;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.FileUtil;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.objects.Timer;
import me.wallhacks.spark.util.player.InventoryUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.ItemListSelectSetting;

import java.util.ArrayList;
import java.util.HashMap;

@Module.Registration(name = "InventoryManager", description = "Steals from chests")
public class InventoryManager extends Module {


    BooleanSetting onlyIfInventoryOpen = new BooleanSetting("OnlyInInventory", this, true, "Cleaning");
    IntSetting delay = new IntSetting("Delay", this, 5, 10, 20, "Cleaning");

    BooleanSetting SortInventory = new BooleanSetting("SortInventory", this, false, "Sort");

    BooleanSetting RemoveDoubleEquipment = new BooleanSetting("RemoveBadEquip", this, false, "Remove");
    BooleanSetting RemoveUseless = new BooleanSetting("RemoveUseless", this, false, "Remove");

    ItemListSelectSetting useless = new ItemListSelectSetting("Useless", this, new Item[]{Item.getItemFromBlock(Blocks.NETHERRACK), Items.ROTTEN_FLESH
    }, "Remove");


    public HashMap<String,Item[]> kits = new HashMap<>();
    public String currentKit;

    Item[] perfectInventory(){
        return kits.get(currentKit);
    }


    public void selectKit(String name){
        if(kits.containsKey(name))
            currentKit = name;
        else if(currentKit == null)
            currentKit = kits.keySet().iterator().next();
    }
    public void setKitFromInventory(String name,boolean hotbarOnly){

        Item[] inv = new Item[36];
        for (int i = 0; i < Math.min(inv.length,hotbarOnly ? 9 : 36) ; i++) {
            if(MC.mc.player.inventory.getStackInSlot(i).getItem() == Items.AIR)
                inv[i] = null;
            else
                inv[i] = MC.mc.player.inventory.getStackInSlot(i).getItem();
        }
        kits.put(name,inv);
    }
    public void setKit(String name,Item[] inv){
        kits.put(name,inv);
        if(currentKit == null)
            currentKit = name;
    }
    public void deleteKit(String name){
        if(kits.size() == 1)
            return;
        kits.remove(name);
        if(name == currentKit)
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
        return ((!(MC.mc.currentScreen instanceof GuiContainer) || MC.mc.currentScreen instanceof GuiInventory) && (!onlyIfInventoryOpen.isOn() || MC.mc.currentScreen instanceof GuiInventory));
    }


    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {


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


    boolean isDoneCleaning(){
        return curSlotIndex >= 36;
    }
    void Clean(){

        if(isDoneCleaning())
            curSlotIndex = 0;

        while (!isDoneCleaning()) {
            if(!timer.passedMs(0))
                return;


            handleSlot(curSlotIndex);


            curSlotIndex++;
        }




    }



    void handleSlot(int slot){
        int s = InventoryUtil.getSlotIdFromInventoryId(slot);
        ItemStack itemStack = (ItemStack) MC.mc.player.inventoryContainer.getInventory().get(s);

        Spark.logger.info("Clean slot " + slot);

        if(!KeepItemStack(itemStack))
        {
            InventoryUtil.throwItem(s);
            timer.delayRandom(delay.getValue(),50);
        }
        else if(SortInventory.isOn())
        {

            for (int i = 0; i < perfectInventory().length; i++) {
                Item item = perfectInventory()[i];
                int sloti = InventoryUtil.getSlotIdFromInventoryId(i);
                if(sloti != s && item != null)
                {
                    if(!invenotrySortIsItemSame(item, MC.mc.player.inventoryContainer.getInventory().get(sloti).getItem()))
                        if(invenotrySortIsItemSame(item,itemStack.getItem()))
                            if(perfectInventory()[slot] == null || !invenotrySortIsItemSame(perfectInventory()[slot],itemStack.getItem()))
                            {
                                InventoryUtil.moveItem(s,sloti);
                                timer.delayRandom(delay.getValue(),50);
                                break;
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
        if(RemoveDoubleEquipment.isOn() && !KeepEquipmentItemStack(itemStack))
            return false;
        if(RemoveUseless.isOn() && useless.isValueSelected(itemStack.getItem()))
            return false;
        return true;
    }






    //only keep best
    public boolean KeepEquipmentItemStack(ItemStack EquipmentItemStack){
        Item EquipmentItem = EquipmentItemStack.getItem();


        if(EquipmentItem instanceof ItemArmor || EquipmentItem instanceof ItemTool || EquipmentItem instanceof ItemSword)
        {
            for(int i = 0; i < MC.mc.player.inventoryContainer.getInventory().size(); i++){

                if(MC.mc.player.inventory.getStackInSlot(i) instanceof ItemStack){
                    ItemStack itemStack = (ItemStack) MC.mc.player.inventoryContainer.getInventory().get(i);

                    if(itemStack != EquipmentItemStack) {
                        if(!keepComparedTo(EquipmentItemStack,itemStack))
                            return false;

                    }



                }
            }
        }

        return true;
    }

    boolean keepComparedTo(ItemStack current, ItemStack other){
        Item currentItem = current.getItem();
        Item otherItem = other.getItem();

        if(currentItem instanceof ItemArmor && otherItem instanceof ItemArmor && ((ItemArmor)currentItem).armorType.equals(((ItemArmor)otherItem).armorType)){
            return (InventoryUtil.getValueOfArmorItem(current) >= InventoryUtil.getValueOfArmorItem(other));
        }
        if(currentItem instanceof ItemTool && otherItem instanceof ItemTool){
            ItemTool Ctool = (ItemTool) currentItem;
            ItemTool Otool = (ItemTool) otherItem;

            if(Ctool.effectiveBlocks.size() == Otool.effectiveBlocks.size()){
                return (Ctool.efficiency >= Otool.efficiency);
            }
        }

        if((currentItem instanceof ItemSword && otherItem instanceof ItemSword)){
            return (InventoryUtil.getValueOfWeaponItem(current) >= InventoryUtil.getValueOfWeaponItem(other));
        }

        return true;
    }








    String getKitsFile(){
        String base = Spark.configManager.ParentPath.getAbsolutePath() + "\\"+this.getName()+"\\";
        return base + "kits.sex";
    }

    @Override
    public void onConfigLoad() {
        super.onConfigLoad();

        try {
            String s = FileUtil.read(getKitsFile());
            if(s != null) {
                String[] List = s.split("\n");
                SystemManager.getModule(InventoryManager.class).currentKit = List[0];
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
                Item[] items = new Item[36];
                items[0] = Items.DIAMOND_SWORD;
                items[1] = Items.GOLDEN_APPLE;
                items[8] = Items.DIAMOND_PICKAXE;
                setKit("Default",items);
            }
        } catch (Exception e) {
            Spark.logger.info("Failed to load kits");
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigSave() {
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
