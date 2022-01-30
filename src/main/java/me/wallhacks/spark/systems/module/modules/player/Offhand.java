package me.wallhacks.spark.systems.module.modules.player;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.manager.SystemManager;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.module.modules.combat.CrystalAura;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.combat.CrystalUtil;
import me.wallhacks.spark.util.player.InventoryUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;

import java.util.Arrays;

@Module.Registration(name = "Offhand", description = "puts things in ur offhand")
public class Offhand extends Module {
    ModeSetting mode = new ModeSetting("Mode", this, "Totem", Arrays.asList("Totem", "Crystal", "Gapple"));
    ModeSetting fallbackMode = new ModeSetting("FallbackMode", this, "Gapple", Arrays.asList("Totem", "Crystal", "Gapple"));



    ModeSetting gapSwap = new ModeSetting("GapSwap", this, "Off", Arrays.asList("Off", "Sword", "Pick", "Both", "Always"));
    BooleanSetting CrystalSwap = new BooleanSetting("CrystalSwap", this,false);





    IntSetting TotemHp = new IntSetting("TotemHP", this,16, 0, 36,"Totem");
    BooleanSetting CrystalCheck = new BooleanSetting("CrystalCheck", this,false,"Totem");
    BooleanSetting bowCheck = new BooleanSetting("BowCheck", this,false,"Totem");


    BooleanSetting closeContainer = new BooleanSetting("CloseContainer", this,false,"Swapping");
    BooleanSetting hotbar = new BooleanSetting("Hotbar", this,false,"Swapping");
    IntSetting cooldown = new IntSetting("Cooldown", this, 0, 0, 20 ,"Swapping");



    private int timer = 0;


    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        if(nullCheck())return;
        timer++;


        //if is not creative
        if (MC.mc.playerController.currentGameType.isSurvivalOrAdventure()) {
            float hp = MC.mc.player.getHealth() + MC.mc.player.getAbsorptionAmount();


            if ((hp > TotemHp.getValue() && lethalToLocalCheck() && MC.mc.player.fallDistance < 10 && bowCheck()) || (getItemSlot(Items.TOTEM_OF_UNDYING)) == -1 && MC.mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING) {
                //if we are not in danger of dying

                if (shouldGapSwap()) {
                    swapItems((Items.GOLDEN_APPLE));

                    if (MC.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) {
                        MC.mc.playerController.isHittingBlock = true;
                    }

                } else if (shouldCrystalSwap()) {
                    swapItems((Items.END_CRYSTAL));
                } else
                    useMode(mode.getValue());

                //if we failed to find item
                if (MC.mc.player.getHeldItemOffhand().getItem() == Items.AIR) {
                    swapItems((Items.TOTEM_OF_UNDYING));
                }
            } else {
                //else we will use a totem
                swapItems((Items.TOTEM_OF_UNDYING));
            }

            //if no totems left we use fallback item
            if (MC.mc.player.getHeldItemOffhand().getItem() == Items.AIR) {
                useMode(fallbackMode.getValue());
            }
        }
    }

    private void useMode(String mode)
    {
        if (mode.equalsIgnoreCase("crystal")) {
            swapItems((Items.END_CRYSTAL));
        } else if (mode.equalsIgnoreCase("totem")) {
            swapItems((Items.TOTEM_OF_UNDYING));
        } else if (mode.equalsIgnoreCase("gapple")) {
            swapItems((Items.GOLDEN_APPLE));
        }
    }

    private boolean shouldCrystalSwap(){

        return CrystalSwap.isOn() && SystemManager.getModule(CrystalAura.class).isEnabled();
    }

    private boolean shouldGapSwap() {
        if (MC.mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE || gapSwap.getValue().equals("Off") || !MC.mc.gameSettings.keyBindUseItem.isKeyDown()) return false;
        switch (gapSwap.getValue()) {
            case "Sword":
                return hasSword();
            case "Pickaxe":
                return hasPick();
            case "Both":
                return (hasSword() || hasPick());
            case "Always":
                return true;
        }
        return false;
    }

    private boolean hasSword() {
        return MC.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword;
    }

    private boolean hasPick() {
        return MC.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe;
    }

    private boolean lethalToLocalCheck() {
        if (!CrystalCheck.getValue()) return true;
        for (Entity entity : MC.mc.world.loadedEntityList) {
            if (entity instanceof EntityEnderCrystal && MC.mc.player.getDistance(entity) < 8) {
                if (CrystalUtil.calculateDamageCrystal((EntityEnderCrystal)entity, MC.mc.player, false)*2 >= (MC.mc.player.getHealth() - 2)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean bowCheck() {
        if (!bowCheck.getValue()) return true;
        for (Entity entity : MC.mc.world.loadedEntityList) {
            if (entity instanceof EntityArrow && entity.getDistance(MC.mc.player) < 15) {
                if (!((EntityArrow) entity).inGround) {
                    return false;
                }
            }
            if (entity instanceof EntityPlayer) {
                EntityPlayer e = (EntityPlayer) entity;
                if (e.getHeldItemMainhand().getItem() instanceof ItemBow && MC.mc.world.rayTraceBlocks(MC.mc.player.getPositionEyes(MC.mc.getRenderPartialTicks()), entity.getPositionEyes(MC.mc.getRenderPartialTicks())) == null && MC.mc.world.rayTraceBlocks(MC.mc.player.getPositionVector(), entity.getPositionEyes(MC.mc.getRenderPartialTicks())) == null) {
                    return false;
                }
            }
        }
        return true;
    }

    public void swapItems(Item input) {
        if(MC.mc.player.getHeldItemOffhand().getItem() == input)
            return;

        int slot = getItemSlot(input);

        if (slot == -1) return;


        //gui is container we can't swap item
        if (!(MC.mc.currentScreen instanceof GuiInventory) && MC.mc.currentScreen != null && MC.mc.currentScreen instanceof GuiContainer)
        {
            //thats why we close screen or just return
            if(closeContainer.isOn())
                MC.mc.player.closeScreen();
            else
                return;
        }

        if (timer < cooldown.getValue() && MC.mc.player.inventory.getStackInSlot(slot).getItem() != Items.TOTEM_OF_UNDYING) return;
            timer = 0;

        InventoryUtil.moveItem(slot,45);
    }


    private int getItemSlot(Item input) {
        return InventoryUtil.FindItemInInventory(input,hotbar.isOn() || input == Items.TOTEM_OF_UNDYING,false);
    }
}
