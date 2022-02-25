package me.wallhacks.spark.systems.module.modules.player;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PacketSendEvent;
import me.wallhacks.spark.event.player.PlayerDamageBlockEvent;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.player.InventoryUtil;
import me.wallhacks.spark.util.player.itemswitcher.ItemSwitcher;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.ItemForMineSwitchItem;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


@Module.Registration(name = "AutoTool", description = "auto tool thing")
public class AutoTool extends Module {

    public static AutoTool instance;
    public AutoTool(){
        instance = this;
    }

    BooleanSetting silent = new BooleanSetting("Silent", this, false);


    @SubscribeEvent
    public void onPlayerDamageBlockEventPre(PlayerDamageBlockEvent.Pre event) {

        Spark.switchManager.Switch(new ItemForMineSwitchItem(mc.world.getBlockState(event.getPos())), ItemSwitcher.usedHand.Mainhand);

    }



}


