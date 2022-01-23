package me.wallhacks.spark.systems.module.modules.player;

import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.player.InventoryUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.IntSetting;


@Module.Registration(name = "AutoArmor", description = "automatically manages armor")
public class AutoArmor extends Module {

    public static AutoArmor INSTANCE;
    public IntSetting safePercent = new IntSetting("SafePercent", this, 10, 0, 100);
    public IntSetting delay = new IntSetting("Delay", this, 5, 0, 20);
    int counter = 0;

    public AutoArmor() {
        super();
        INSTANCE = this;
    }

    private int findArmorSlot(int item, boolean flag) {
        int slot = -1;
        float bestValue = 0;
        EntityEquipmentSlot type;
        if (item == 5) {
            type = EntityEquipmentSlot.HEAD;
        } else if (item == 6) {
            type = EntityEquipmentSlot.CHEST;
        } else if (item == 7) {
            type = EntityEquipmentSlot.LEGS;
        } else if (item == 8) {
            type = EntityEquipmentSlot.FEET;
        } else return -1;

        for (int i = 0; i < MC.mc.player.inventoryContainer.getInventory().size(); ++i) {
            ItemStack s = MC.mc.player.inventoryContainer.getInventory().get(i);
            if (s.getItem() != Items.AIR) {
                if (s.getItem() instanceof ItemArmor) {
                    final ItemArmor armor = (ItemArmor) s.getItem();
                    if (armor.armorType == type) {
                        float value = InventoryUtil.getValueOfArmorItem(s);
                        double dam_left = s.getMaxDamage() - s.getItemDamage();
                        double percent = (dam_left / s.getMaxDamage()) * 100;
                        if (percent < safePercent.getValue()) {
                            if (flag) {
                                value *= 1.5;
                            } else {
                                value *= 0.5;
                            }
                        }
                        if (value > bestValue) {
                            bestValue = value;
                            slot = i;
                        }
                    }
                }
            }
        }
        return slot;
    }

    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        counter++;
        if (counter > delay.getValue() && !(nullCheck() || MC.mc.currentScreen instanceof GuiContainer && !(MC.mc.currentScreen instanceof GuiInventory))) {
            for (int i = 5; i <= 8; i++) {
                if (!FastUse.INSTANCE.shouldPause()) {
                    ItemStack itemStack = MC.mc.player.inventoryContainer.getSlot(i).getStack();
                    int armorSlot = findArmorSlot(i, false);
                    if (armorSlot != -1 && armorSlot != i) {
                        if (itemStack.isEmpty()) {
                            MC.mc.playerController.windowClick(MC.mc.player.inventoryContainer.windowId, armorSlot, 0, ClickType.QUICK_MOVE, MC.mc.player);
                            MC.mc.playerController.updateController();
                        } else
                            InventoryUtil.moveItem(armorSlot, i);
                        counter = 0;
                        break;
                    }
                } else {
                    ItemStack itemStack = MC.mc.player.inventoryContainer.getSlot(i).getStack();
                    if (itemStack.isEmpty()) {
                        int armorSlot = findArmorSlot(i, true);
                        if (armorSlot == -1) continue;
                        ItemStack armor = MC.mc.player.inventoryContainer.getInventory().get(armorSlot);
                        double dam_left = armor.getMaxDamage() - armor.getItemDamage();
                        double percent = (dam_left / armor.getMaxDamage()) * 100;
                        if (percent < FastUse.INSTANCE.takeOffVal.getValue()) {
                            MC.mc.playerController.windowClick(MC.mc.player.inventoryContainer.windowId, armorSlot, 0, ClickType.QUICK_MOVE, MC.mc.player);
                            MC.mc.playerController.updateController();
                        }
                    }
                }
            }
        }
    }
}


