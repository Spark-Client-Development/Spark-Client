package me.wallhacks.spark.systems.module.modules.misc;

import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.manager.SystemManager;
import me.wallhacks.spark.systems.module.Module;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.item.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;

@Module.Registration(name = "ChestStealer", description = "Steals from chests")
public class ChestStealer extends Module {



    BooleanSetting auto = new BooleanSetting("Auto", this, false);
    IntSetting delay = new IntSetting("Delay", this, 10, 5, 30);

    boolean isStealing = false;




    public boolean isAuto() {
        return auto.isOn();
    }

    private long time;
    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        if((mc.currentScreen instanceof GuiChest || mc.currentScreen instanceof GuiShulkerBox) && isStealing){
            if(System.currentTimeMillis() - this.time >= delay.getValue()*10)
            {
                if(isStealing)
                    steal();
            }
        }else
            isStealing = false;

    }

    public void StartSteal(){
        isStealing = true;
        slot = 0;
        time = System.currentTimeMillis();
    }

    int slot = 0;
    void steal(){
        if (mc.currentScreen instanceof GuiChest || mc.currentScreen instanceof GuiShulkerBox)
        {
            Container container = ((GuiContainer) mc.currentScreen).inventorySlots;

            while(slot < (mc.currentScreen instanceof GuiShulkerBox ? ((GuiShulkerBox) mc.currentScreen).inventory : ((GuiChest) mc.currentScreen).lowerChestInventory).getSizeInventory())
            {
                ItemStack l_Stack = container.getSlot(slot).getStack();

                if (!l_Stack.isEmpty() && l_Stack.getItem() != Items.AIR)
                {
                    if(!SystemManager.getModule(InventoryManager.class).isEnabled() || SystemManager.getModule(InventoryManager.class).KeepItemStack(l_Stack))
                    {
                        mc.playerController.windowClick(container.windowId, slot, 0, ClickType.QUICK_MOVE,  mc.player);
                        time = System.currentTimeMillis()+(long) (Math.random()*5*delay.getValue());
                        return;
                    }
                }
                slot++;

            }

            isStealing = false;
            slot = 0;

        }
    }
    public static ChestStealer getInstance(){
        return SystemManager.getModule(ChestStealer.class);
    }
}
