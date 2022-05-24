package me.wallhacks.spark.systems.module.modules.player;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.client.InputEvent;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.KeySetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.player.InventoryUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.IntSetting;

import java.util.Arrays;


@Module.Registration(name = "AutoArmor", description = "automatically manages armor")
public class AutoArmor extends Module {

    public static AutoArmor INSTANCE;
    public IntSetting safePercent = new IntSetting("SafePercent", this, 10, 0, 100);
    public IntSetting delay = new IntSetting("Delay", this, 5, 0, 20);
    ModeSetting mode = new ModeSetting("Mode", this, "ChestPlate", Arrays.asList("Elytra", "ChestPlate"));
    KeySetting swap = new KeySetting("Swap", this, -1);
    int counter = 0;

    public AutoArmor() {
        super();
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onKey(InputEvent event) {
        if (event.getKey() == swap.getKey())
            mode.increment();
    }

    private int findEquipSlot(int slot) {
        EntityEquipmentSlot type;
        if (slot == 5) {
            type = EntityEquipmentSlot.HEAD;
        } else if (slot == 6) {
            type = EntityEquipmentSlot.CHEST;
        } else if (slot == 7) {
            type = EntityEquipmentSlot.LEGS;
        } else if (slot == 8) {
            type = EntityEquipmentSlot.FEET;
        } else return -1;
        slot = -1;
        float bestScore = 0;
        boolean foundGood = false;
        for (int i = 0; i < mc.player.inventoryContainer.getInventory().size(); ++i) {
            ItemStack s = mc.player.inventoryContainer.getInventory().get(i);
            if (s.getItem() instanceof ItemArmor && ((ItemArmor) s.getItem()).armorType == type) {
                float score = InventoryUtil.getValueOfArmorItem(s);
                boolean good = mode.is("ChestPlate");
                if (getDurability(s) > safePercent.getValue()) {
                    if ((score > bestScore || (good && !foundGood)) && (good || !foundGood)) {
                        if (good)
                            foundGood = true;
                        bestScore = score;
                        slot = i;
                    }
                }
            } else if (s.getItem() instanceof ItemElytra && type == EntityEquipmentSlot.CHEST) {
                boolean good = mode.is("Elytra");
                float score = InventoryUtil.getValueOfArmorItem(s);
                if (getDurability(s) > safePercent.getValue()) {
                    if ((score > bestScore || (good && !foundGood)) && (good || !foundGood)) {
                        if (good)
                            foundGood = true;
                        bestScore = score;
                        slot = i;
                    }
                }
            }
        }
        return slot;
    }

    private int findEquipSlotMend(int slot) {
        EntityEquipmentSlot type;
        if (slot == 5) {
            type = EntityEquipmentSlot.HEAD;
        } else if (slot == 6) {
            type = EntityEquipmentSlot.CHEST;
        } else if (slot == 7) {
            type = EntityEquipmentSlot.LEGS;
        } else if (slot == 8) {
            type = EntityEquipmentSlot.FEET;
        } else return -1;
        for (int i = 0; i < mc.player.inventoryContainer.getInventory().size(); ++i) {
            ItemStack s = mc.player.inventoryContainer.getInventory().get(i);
            if ((s.getItem() instanceof ItemArmor && ((ItemArmor) s.getItem()).armorType == type) || (s.getItem() instanceof ItemElytra && type == EntityEquipmentSlot.CHEST)) {
                if (getDurability(s) < FastUse.INSTANCE.takeOffVal.getValue())
                    return i;
            }
        }
        return -1;
    }

    private double getDurability(ItemStack s) {
        double dam_left = s.getMaxDamage() - s.getItemDamage();
        return (dam_left / s.getMaxDamage()) * 100;
    }



    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        counter++;
        if (counter > delay.getValue() && !(nullCheck() || mc.currentScreen instanceof GuiContainer && !(mc.currentScreen instanceof GuiInventory))) {
            for (int i = 5; i <= 8; i++) {
                ItemStack itemStack = mc.player.inventoryContainer.getSlot(i).getStack();
                if (!FastUse.INSTANCE.shouldPause()) {
                    int armorSlot = findEquipSlot(i);
                    if (armorSlot != -1 && armorSlot != i) {
                        if (itemStack.isEmpty()) {
                            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, armorSlot, 0, ClickType.QUICK_MOVE, mc.player);
                            mc.playerController.updateController();
                        } else
                            InventoryUtil.moveItem(armorSlot, i);
                        counter = 0;
                        return;
                    }
                } else {
                    if (itemStack.isEmpty()) {
                        int armorSlot = findEquipSlotMend(i);
                        if (armorSlot == -1)
                            continue;
                        Spark.logger.info(getDurability(mc.player.inventoryContainer.getSlot(armorSlot).getStack()));
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, armorSlot, 0, ClickType.QUICK_MOVE, mc.player);
                        mc.playerController.updateController();
                        counter = 0;
                        return;
                    }
                }
            }
        }
    }

}


