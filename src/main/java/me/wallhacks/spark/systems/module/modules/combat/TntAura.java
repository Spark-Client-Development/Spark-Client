package me.wallhacks.spark.systems.module.modules.combat;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.util.WorldUtils;
import me.wallhacks.spark.util.combat.AttackUtil;
import me.wallhacks.spark.util.objects.FadePos;
import me.wallhacks.spark.util.player.BlockInteractUtil;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.player.RaytraceUtil;
import me.wallhacks.spark.util.player.itemswitcher.ItemSwitcher;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.BlockSwitchItem;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.HardSolidBlockSwitchItem;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.SpecBlockSwitchItem;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.SpecItemSwitchItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Module.Registration(name = "TntAura", description = "uses tnt to blow up your enemy")
public class TntAura extends Module {
    public static TntAura INSTANCE;

    BlockPos targetFloorPos;

    BooleanSetting closeTop = new BooleanSetting("CloseTop", this, false);
    BooleanSetting extraHeight = new BooleanSetting("ExtraHeight", this, false);

    BooleanSetting pause = new BooleanSetting("Pause", this, true);

    IntSetting delay = new IntSetting("Delay", this, 4, 0, 10);


    BooleanSetting render = new BooleanSetting("Render", this, true, "Render");
    ColorSetting fill = new ColorSetting("Color", this, new Color(0x38DC865E, true), "Render");
    int cooldown = 0;


    public TntAura() {
        INSTANCE = this;
    }

    int getH() {
        return extraHeight.isOn() ? 3 : 2;
    }

    @SubscribeEvent
    void OnUpdate(PlayerUpdateEvent event) {

        BlockPos newPos = GetTntBlock();

        if (newPos == null || (targetFloorPos != null && !targetFloorPos.equals(newPos))) {

            if (targetFloorPos == null)
                Spark.sendInfo("TntAura has no target!");
            else
                Spark.sendInfo("TntAura has lost target!");
            targetFloorPos = null;

            disable();
            return;
        }
        targetFloorPos = newPos;

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        if (pause.isOn()) {
            //pause if tnt is exploding
            List<Entity> l = mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(targetFloorPos));
            for (Entity e : l) {
                if (e instanceof EntityTNTPrimed) {
                    EntityTNTPrimed tnt = (EntityTNTPrimed) e;

                    if (tnt.ticksExisted > 60)
                        return;
                }
            }
        }


        BlockPos targetPos = targetFloorPos.add(0, getH(), 0);

        ArrayList<BlockPos> toPlace = new ArrayList<BlockPos>();
        for (BlockPos s : WorldUtils.getSurroundBlocks(targetPos)) {
            toPlace.add(s.add(0, 0, 0));
            toPlace.add(s.add(0, -1, 0));
            toPlace.add(s.add(0, -2, 0));
        }

        //sort by distance
        Collections.sort(toPlace, new Comparator<BlockPos>() {
            @Override
            public int compare(BlockPos fruit2, BlockPos fruit1) {
                return (int) ((PlayerUtil.getDistance(fruit1) - PlayerUtil.getDistance(fruit2)) * 5);
            }
        });

        for (BlockPos pos : toPlace) {
            if (mc.world.getBlockState(pos).getBlock().material.isReplaceable()) {
                BlockInteractUtil.BlockPlaceResult res = place(pos, new HardSolidBlockSwitchItem());
                if (res != BlockInteractUtil.BlockPlaceResult.FAILED) {
                    return;
                }
            }

        }


        if (mc.world.getBlockState(targetPos).getBlock() == Blocks.TNT) {
            if (closeTop.isOn()) {
                if (mc.world.getBlockState(targetPos.add(0, 1, 0)).getBlock().material.isReplaceable()) {
                    place(targetPos.add(0, 1, 0), new HardSolidBlockSwitchItem());
                    return;
                }
            }


            Vec3d pos = PlayerUtil.getClosestPoint(RaytraceUtil.getPointToLookAtBlock(targetPos));
            EnumFacing facing = EnumFacing.UP;

            if (pos == null)
                pos = new Vec3d(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5);
            else
                facing = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), pos, false).sideHit;


            if (AntiCheatConfig.getInstance().placeRotate.getValue())
                if (!Spark.rotationManager.rotate(Spark.rotationManager.getLegitRotations(pos), true))
                    return;

            EnumHand hand = Spark.switchManager.Switch(new SpecItemSwitchItem(Items.FLINT_AND_STEEL), ItemSwitcher.usedHand.Both);

            if (hand == null)
                return;

            BlockInteractUtil.processRightClickBlock(targetPos, facing, true, hand, pos);

            cooldown = delay.getValue();
        } else
            place(targetPos, new SpecBlockSwitchItem(Blocks.TNT));


    }


    BlockInteractUtil.BlockPlaceResult place(BlockPos p, BlockSwitchItem item) {

        BlockInteractUtil.BlockPlaceResult res = (BlockInteractUtil.tryPlaceBlock(p, item, true));
        if (res == BlockInteractUtil.BlockPlaceResult.PLACED)
            if (render.getValue())
                new FadePos(p, fill, true);
        return res;
    }


    BlockPos GetTntBlock() {
        BlockPos NewPos = null;
        double bestDistance = Double.MAX_VALUE;

        entityLoop:
        for (Entity entity : mc.world.loadedEntityList) {

            if (entity instanceof EntityPlayer) {
                EntityPlayer e = (EntityPlayer) entity;
                // && e.onGround
                if (AttackUtil.canAttackPlayer(e, 10)) {

                    BlockPos floored = PlayerUtil.getPlayerPosFloored(e);

                    if (!mc.world.getBlockState(floored).getBlock().material.isSolid()) {

                        for (int i = 0; i < getH(); i++) {
                            if (mc.world.getBlockState(floored.add(0, 1, 0)).getBlock().material.isSolid()) {
                                continue entityLoop;
                            }
                        }

                        BlockPos p = PlayerUtil.getPlayerPosFloored(e);

                        if (p.equals(targetFloorPos))
                            return p;

                        float dis = PlayerUtil.getDistance(p);
                        if (dis < bestDistance) {
                            bestDistance = dis;
                            NewPos = p;
                        }

                    }
                }
            }

        }
        return NewPos;
    }

    public boolean blockNeedsToBeEmpty(BlockPos p) {
        if (isEnabled() && targetFloorPos != null) {
            for (int i = 0; i < getH(); i++) {
                if (p.equals(targetFloorPos.add(0, i, 0)))
                    return true;
            }

        }
        return false;
    }

    public boolean isInAttackZone(EntityPlayer player) {
        if (isEnabled() && targetFloorPos != null) {
            BlockPos floored = PlayerUtil.getPlayerPosFloored(player);
            //second check if for if they are jumping
            if (floored.equals(targetFloorPos) || floored.add(0, -1, 0).equals(targetFloorPos))
                return true;
        }
        return false;
    }
}