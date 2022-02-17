package me.wallhacks.spark.systems.module.modules.combat;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.player.InventoryUtil;
import me.wallhacks.spark.util.player.itemswitcher.ItemSwitcher;
import me.wallhacks.spark.util.player.itemswitcher.SwitchItem;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.SpecItemSwitchItem;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTippedArrow;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "Quiver", description = "shoots arrows to get positive potion effects")
public class Quiver extends Module {
    private int timer = 0;
    private int stage = 1;
    private int returnSlot = -1;
    private int oldHotbar;

    @Override
    public void onEnable() {
        oldHotbar = mc.player.inventory.currentItem;
    }

    @Override
    public void onDisable() {
        timer = 0;
        this.stage = 0;
        mc.gameSettings.keyBindUseItem.pressed = false;
        mc.player.inventory.currentItem = oldHotbar;
        if (returnSlot != -1) {
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, returnSlot, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, 0, ClickType.PICKUP, mc.player);
            mc.playerController.updateController();
        }
        returnSlot = -1;
    }

    @SubscribeEvent
    public void onUpdateEvent(PlayerUpdateEvent event) {
        if (nullCheck()) return;
        if (mc.currentScreen != null) return;
        if (stage != 0 && ItemSwitcher.Switch(new SpecItemSwitchItem(Items.BOW), ItemSwitcher.switchType.Mainhand) == null) {
            this.disable();
            Spark.sendInfo("No bow found");
            return;
        }
        if (stage == 0) {
            if (!mapArrows()) {
                this.disable();
                return;
            }
            stage++;
        } else if (stage == 1) {
            this.stage++;
            timer++;
            return;
        } else if (stage == 2) {
            mc.gameSettings.keyBindUseItem.pressed = true;
            timer = 0;
            this.stage++;
        } else if (stage == 3) {
            if (timer > 4) {
                this.stage++;
            }
        } else if (stage == 4) {
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, -90, mc.player.onGround));
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
            mc.player.resetActiveHand();
            mc.gameSettings.keyBindUseItem.pressed = false;
            timer = 0;
            this.stage++;
        } else if (stage == 5) {
            if (timer < 12) {
                timer++;
                return;
            }
            this.stage = 0;
            timer = 0;
        }
        timer++;
    }

    private boolean mapArrows() {
        for (int a = 9; a < 45; a++) {
            if (mc.player.inventoryContainer.getInventory().get(a).getItem() instanceof ItemTippedArrow) {
                final ItemStack arrow = mc.player.inventoryContainer.getInventory().get(a);
                final ItemStack currentArrow = mc.player.inventoryContainer.getInventory().get(9);
                if (PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRENGTH) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRONG_STRENGTH) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.LONG_STRENGTH)) {
                    if (!mc.player.isPotionActive(MobEffects.STRENGTH) && !PotionUtils.getPotionFromItem(currentArrow).equals(PotionTypes.STRENGTH) && !PotionUtils.getPotionFromItem(currentArrow).equals(PotionTypes.STRONG_STRENGTH) && !PotionUtils.getPotionFromItem(currentArrow).equals(PotionTypes.LONG_STRENGTH)) {
                        switchTo(a);
                        return true;
                    }
                }
                if (PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.SWIFTNESS) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.LONG_SWIFTNESS) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRONG_SWIFTNESS)) {
                    if (!mc.player.isPotionActive(MobEffects.SPEED) && !PotionUtils.getPotionFromItem(currentArrow).equals(PotionTypes.SWIFTNESS) && !PotionUtils.getPotionFromItem(currentArrow).equals(PotionTypes.STRONG_SWIFTNESS) && !PotionUtils.getPotionFromItem(currentArrow).equals(PotionTypes.LONG_SWIFTNESS)) {
                        switchTo(a);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void switchTo(int from) {
        if (from == 9) return;
        if (returnSlot == -1)
            returnSlot = from;
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, from, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, from, 0, ClickType.PICKUP, mc.player);
        mc.playerController.updateController();
    }
}