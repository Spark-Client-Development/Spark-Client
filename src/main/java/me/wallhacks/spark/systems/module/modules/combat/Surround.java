package me.wallhacks.spark.systems.module.modules.combat;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.event.player.SafeWalkEvent;
import me.wallhacks.spark.event.player.SneakEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.module.modules.exploit.PacketMine;
import me.wallhacks.spark.systems.setting.SettingGroup;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.WorldUtils;
import me.wallhacks.spark.util.objects.FadePos;
import me.wallhacks.spark.util.player.BlockInteractUtil;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.HardSolidBlockSwitchItem;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.SpecBlockSwitchItem;
import net.java.games.input.Keyboard;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Module.Registration(name = "Surround", description = "Steals from chests")
public class Surround extends Module {
    public static Surround instance;
    IntSetting blocksPerTick = new IntSetting("BlocksPerTick", this, 4, 1, 8);
    BooleanSetting bottomFill = new BooleanSetting("BottomFill", this, true);
    ModeSetting SnapToCenter = new ModeSetting("Center", this, "Always", Arrays.asList("Always", "Off", "ForPlace"));
    ModeSetting disable = new ModeSetting("Disable", this, "Off", Arrays.asList("Off", "Done", "OffGround"));
    BooleanSetting IgnoreCrystals = new BooleanSetting("IgnoreCrystals", this, true);
    BooleanSetting BreakCrystals = new BooleanSetting("BreakCrystals", this, false);
    BooleanSetting allowNonObi = new BooleanSetting("AllowNonObi", this, true);
    ModeSetting switchingMode = new ModeSetting("Switch", this, "Silent", Arrays.asList("Normal", "Silent", "Const"));
    SettingGroup renderG = new SettingGroup("Render", this);
    BooleanSetting render = new BooleanSetting("Render", renderG, true);
    ColorSetting fill = new ColorSetting("Fill", renderG, new Color(0x3846C372, true));
    boolean isPlacing = true;


    @SubscribeEvent
    public void onSneakEvent(SafeWalkEvent event) {
        if (isEnabled() && getBind() == mc.gameSettings.keyBindSneak.keyCode) {
            event.setCanceled(true);
        }
    }

    public Surround() {
        instance = this;
    }

    public boolean isPlacing() {
        return isEnabled() && isPlacing;
    }

    @SubscribeEvent
    void OnUpdate(PlayerUpdateEvent event) {

        isPlacing = false;

        //PlayerUtil.isInBlocks(mc.player) checks if we are in blocks
        if (disable.is("OffGround") && !PlayerUtil.isInBlocks(mc.player) && !mc.player.onGround) {
            disable();
            return;
        }
        BlockPos blockUnderPlayer = PlayerUtil.getPlayerPosFloored(mc.player, 0.2);
        if (!SnapToCenter.isValueName("Off"))
            if (!PlayerUtil.MoveCenter(blockUnderPlayer, SnapToCenter.isValueName("OnPlace")))
                return;


        ArrayList<BlockPos> aroundPlayer = new ArrayList<BlockPos>();


        List<BlockPos> occupiedByPlayer = WorldUtils.getBlocksOccupiedByBox(mc.player.boundingBox);


        for (BlockPos floored : occupiedByPlayer) {
            BlockPos[] poses = new BlockPos[]{floored.add(1, 0, 0), floored.add(0, 0, 1), floored.add(-1, 0, 0), floored.add(0, 0, -1)};
            for (BlockPos p : poses)
                if (!occupiedByPlayer.contains(p)) {
                    aroundPlayer.add(p);

                }
            for (BlockPos p : poses)
                if (occupiedByPlayer.contains(p))
                    aroundPlayer.add(p.add(0, -1, 0));


        }


        ArrayList<BlockPos> poses = new ArrayList<BlockPos>();

        if (bottomFill.isOn() && mc.player.isJumping) {
            if (!poses.contains(blockUnderPlayer.add(0, -1, 0)))
                poses.add(blockUnderPlayer.add(0, -1, 0));
        }


        for (BlockPos p : aroundPlayer) {
            if (!poses.contains(p))
                poses.add(p);
        }

        if (bottomFill.isOn() && !mc.player.isJumping) {
            if (!poses.contains(blockUnderPlayer.add(0, -1, 0)))
                poses.add(blockUnderPlayer.add(0, -1, 0));
        }

        int placed = 0;
        boolean done = true;
        for (BlockPos x : poses) {
            if (mc.world.getBlockState(x).getBlock().material.isReplaceable()) {
                BlockPos p = getBlockPosToPlaceAtBlock(x);
                if (p != null) {

                    done = false;
                    BlockInteractUtil.BlockPlaceResult res = Place(p);
                    if (p.equals(PacketMine.instance.pos)) PacketMine.instance.pos = null;

                    if (res != BlockInteractUtil.BlockPlaceResult.FAILED)
                        isPlacing = true;

                    if (res == BlockInteractUtil.BlockPlaceResult.PLACED) {
                        if (render.getValue())
                            new FadePos(p, fill, true);
                        placed++;
                    } else if (res == BlockInteractUtil.BlockPlaceResult.WAIT)
                        return;

                    if (placed >= blocksPerTick.getValue())
                        return;
                }
            }
        }

        if (done && disable.is("Done")) this.disable();
    }


    BlockInteractUtil.BlockPlaceResult Place(BlockPos x) {

        ArrayList<EntityEnderCrystal> crystals = new ArrayList<EntityEnderCrystal>();
        boolean isblocked = false;
        List<Entity> l = mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(x));
        for (Entity e : l) {
            if (!(e instanceof EntityItem) && !(e instanceof EntityXPOrb) && !e.isDead) {
                isblocked = true;
                if (e instanceof EntityEnderCrystal && (IgnoreCrystals.isOn() || BreakCrystals.isOn())) {
                    crystals.add((EntityEnderCrystal) e);
                } else
                    return BlockInteractUtil.BlockPlaceResult.FAILED;
            }
        }

        if (BreakCrystals.isOn())
            for (EntityEnderCrystal entity : crystals) {
                mc.player.connection.sendPacket(new CPacketUseEntity(entity));
                mc.player.swingArm(EnumHand.MAIN_HAND);

            }


        BlockInteractUtil.BlockPlaceResult res = (BlockInteractUtil.tryPlaceBlockNoEntityCheck(x, allowNonObi.isOn() ? new HardSolidBlockSwitchItem() : new SpecBlockSwitchItem(Blocks.OBSIDIAN), Spark.switchManager.getModeFromString(switchingMode.getValue())));


        return res;


    }


    BlockPos getBlockPosToPlaceAtBlock(BlockPos pos) {
        if (canPlace(pos))
            return pos;
        for (BlockPos x : new BlockPos[]{pos.add(0, -1, 0), pos.add(0, 0, 1), pos.add(0, 0, -1), pos.add(1, 0, 0), pos.add(-1, 0, 0)}) {
            if (canPlace(x))
                return x;
        }
        return null;
    }

    boolean canPlace(BlockPos p) {
        List<Entity> l = mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(p));
        for (Entity e : l) {
            if (!(e instanceof EntityItem) && !(e instanceof EntityXPOrb) && !e.isDead) {
                if (e instanceof EntityEnderCrystal && (IgnoreCrystals.isOn() || BreakCrystals.isOn())) {

                } else
                    return false;
            }
        }
        return BlockInteractUtil.canPlaceBlockAtPos(p, false);

    }

}
