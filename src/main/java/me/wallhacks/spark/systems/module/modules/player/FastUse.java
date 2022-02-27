package me.wallhacks.spark.systems.module.modules.player;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.event.player.UpdateWalkingPlayerEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.KeySetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.player.InventoryUtil;
import me.wallhacks.spark.util.player.itemswitcher.ItemSwitcher;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.SpecItemSwitchItem;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;

@Module.Registration(name = "FastUse", description = "uses exp with packets")
public class FastUse extends Module {
    BooleanSetting crystal = new BooleanSetting("Crystals", this, false);
    BooleanSetting blocks = new BooleanSetting("Blocks", this, false);
    BooleanSetting exp = new BooleanSetting("EXP", this, true);
    BooleanSetting fireworks = new BooleanSetting("FireWorks", this, false);
    KeySetting bind = new KeySetting("PacketEXP", this, -1);
    ModeSetting switchMode = new ModeSetting("Switch", this, "Silent", Arrays.asList("Normal", "Silent","Const","NoSwitch"));
    IntSetting takeOffVal = new IntSetting("TakeOffPercent", this, 101, 0, 101);
    IntSetting packets = new IntSetting("Packets", this, 1, 1, 5);
    public static FastUse INSTANCE;
    private boolean shouldPause = false;
    boolean didshit = false;


    public FastUse() {
        INSTANCE = this;
    }

    public boolean shouldPause() {
        return shouldPause;
    }

    @Override
    public void onEnable() {
        shouldPause = false;
    }

    @Override
    public void onDisable() {
        shouldPause = false;
    }


    @SubscribeEvent
    public final void onUpdateWalkingPlayerEvent(PlayerUpdateEvent event) {
        didshit = false;
        if (bind.isDown() && mc.currentScreen == null)
            useXp();
        shouldPause = didshit;
    }



    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        if (InventoryUtil.getHeldItem(Items.END_CRYSTAL) && crystal.getValue() || InventoryUtil.getHeldItem(Items.FIREWORKS) && fireworks.getValue())
            mc.rightClickDelayTimer = 0;
        else if(InventoryUtil.getHeldItem(Items.EXPERIENCE_BOTTLE) && exp.getValue()){
            mc.rightClickDelayTimer = 0;
        }
        else if (Block.getBlockFromItem(mc.player.getHeldItemMainhand().getItem()).getDefaultState().isFullBlock() && blocks.getValue())
            mc.rightClickDelayTimer = 0;
    }

    int findExpInHotbar() {
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.EXPERIENCE_BOTTLE) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    void useXp() {
        if (findExpInHotbar() == -1) return;
        didshit = true;

        ItemSwitcher.SwitchResult res = Spark.switchManager.getCalculateAction(new SpecItemSwitchItem(Items.EXPERIENCE_BOTTLE), ItemSwitcher.usedHand.Both,Spark.switchManager.getModeFromString(switchMode.getValue()));

        if (res != null && takeArmorOff()) {
            Spark.rotationManager.setFakePitch(90,2);
            Spark.switchManager.Switch(res,Spark.switchManager.getModeFromString(switchMode.getValue()),7);
            for (int i = 0; i < packets.getValue(); i++) {
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            }
        }
    }

    ItemStack getArmor(int first) {
        return mc.player.inventoryContainer.getInventory().get(first);
    }

    boolean takeArmorOff() {
        boolean done = true;
        for (int slot = 5; slot <= 8; slot++) {
            ItemStack item;
            item = getArmor(slot);
            double max_dam = item.getMaxDamage();
            double dam_left = item.getMaxDamage() - item.getItemDamage();
            double percent = (dam_left / max_dam) * 100;
            if (percent >= takeOffVal.getValue()) {
                if (InventoryUtil.notInInv(Items.AIR) || item.equals(Items.AIR)) {
                    continue;
                }
                if (AutoArmor.INSTANCE.isEnabled()) {
                    if (AutoArmor.INSTANCE.counter <= AutoArmor.INSTANCE.delay.getValue()) {
                        continue;
                    }
                    AutoArmor.INSTANCE.counter = 0;
                }
                mc.playerController.windowClick(0, slot, 0, ClickType.QUICK_MOVE, mc.player);
                mc.playerController.updateController();
            }
            if (percent < takeOffVal.getValue()) {
                done = false;
            }
        }
        return !done;
    }
}
