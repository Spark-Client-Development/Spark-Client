package me.wallhacks.spark.util.player;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.objects.Pair;
import me.wallhacks.spark.util.player.itemswitcher.ItemSwitcher;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.BlockSwitchItem;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSkull;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockInteractUtil implements MC {
    public static BlockPlaceResult tryPlaceBlock(BlockPos pos, BlockSwitchItem switcher, boolean clientSided, boolean checkEntities) {
        return tryPlaceBlock(pos, switcher, AntiCheatConfig.getInstance().getBlockPlaceSwitchType(), clientSided, checkEntities);
    }


    public static BlockPlaceResult tryPlaceBlock(BlockPos pos, BlockSwitchItem switcher, ItemSwitcher.switchType switchType, boolean clientSided, boolean checkEntities) {

        if (!mc.world.getBlockState(pos).getMaterial().isReplaceable())
            return BlockPlaceResult.FAILED;

        Item willuse = null;
        if (switcher != null) {
            willuse = Spark.switchManager.predictItem(switcher, ItemSwitcher.usedHand.Both, switchType);
            if (!(willuse instanceof ItemBlock) && !(willuse instanceof ItemSkull) && willuse != Items.SKULL && willuse != Items.WATER_BUCKET && willuse != Items.LAVA_BUCKET)
                return BlockPlaceResult.FAILED;
        }


        if (checkEntities && !blockCollisionCheck(pos, willuse == null || !(willuse instanceof ItemBlock) ? null : ((ItemBlock) willuse).getBlock()))
            return BlockPlaceResult.FAILED;

        EnumFacing face = getDirForPlacingBlockAtPos(pos);

        if (face == null)
            return BlockPlaceResult.FAILED;

        BlockPos placeOn = pos.offset(face, -1);
        return getBlockPlaceResult(face, switcher, switchType, clientSided, placeOn);
    }

    public static BlockPlaceResult tryPlaceBlockOnBlock(BlockPos pos, EnumFacing face, BlockSwitchItem switcher, boolean clientSided, boolean checkEntities) {
        return tryPlaceBlockOnBlock(pos, face, switcher, ItemSwitcher.switchType.Normal, clientSided, checkEntities);
    }

    public static BlockPlaceResult tryPlaceBlockOnBlock(BlockPos pos, EnumFacing face, BlockSwitchItem switcher, ItemSwitcher.switchType switchType, boolean clientSided, boolean checkEntities) {

        if (!mc.world.getBlockState(pos).getMaterial().isReplaceable())
            return BlockPlaceResult.FAILED;

        Item willuse = null;
        if (switcher != null) {
            willuse = Spark.switchManager.predictItem(switcher, ItemSwitcher.usedHand.Both, switchType);
            if (!(willuse instanceof ItemBlock) && !(willuse instanceof ItemSkull) && willuse != Items.SKULL && willuse != Items.WATER_BUCKET && willuse != Items.LAVA_BUCKET)
                return BlockPlaceResult.FAILED;
        }

        if (checkEntities && !blockCollisionCheck(pos, ((ItemBlock) willuse).getBlock()))
            return BlockPlaceResult.FAILED;

        if (face == null)
            return BlockPlaceResult.FAILED;

        BlockPos placeOn = pos.offset(face, -1);

        if (mc.world.getBlockState(placeOn).getBlock().isReplaceable(mc.world, placeOn))
            return BlockPlaceResult.FAILED;

        return getBlockPlaceResult(face, switcher, switchType, clientSided, placeOn);
    }

    @NotNull
    private static BlockInteractUtil.BlockPlaceResult getBlockPlaceResult(EnumFacing face, BlockSwitchItem switcher, ItemSwitcher.switchType switchType, boolean clientSided, BlockPos placeOn) {
        Pair<Vec3d, Boolean> p = getPointOnBlockFace(placeOn, face);
        Vec3d hitVec = p.getKey();

        if (hitVec == null)
            return BlockPlaceResult.FAILED;

        EnumHand hand = Spark.switchManager.Switch(switcher, ItemSwitcher.usedHand.Both, switchType);
        if (hand == null)
            return BlockPlaceResult.FAILED;

        if (AntiCheatConfig.getInstance().placeRotate.getValue()) {
            float rot[] = Spark.rotationManager.getLegitRotations(hitVec);
            boolean ready = true;
            if (p.getValue()) {
                if (!Spark.rotationManager.isRaytraceBypassDone() && RaytraceUtil.getRotationForBypass(rot[0]) != null) {
                    ready = false;
                    rot = RaytraceUtil.getRotationForBypass(rot[0]);
                }
            }
            if (!Spark.rotationManager.rotate(rot, true) || !ready)
                return BlockPlaceResult.WAIT;
        }


        mc.playerController.syncCurrentPlayItem();

        return processRightClickBlockForPlace(placeOn, face, clientSided, hand, hitVec) ? BlockPlaceResult.PLACED : BlockPlaceResult.FAILED;
    }

    public static boolean canPlaceBlockAtPos(BlockPos p, boolean checkEntities) {

        if (!mc.world.getBlockState(p).getMaterial().isReplaceable())
            return false;
        if (checkEntities && !blockCollisionCheck(p, null))
            return false;
        if (getDirForPlacingBlockAtPos(p) == null)
            return false;

        return true;
    }

    public static boolean blockCollisionCheck(BlockPos pos, Block placeThis) {

        AxisAlignedBB box = new AxisAlignedBB(pos);

        if (placeThis != null)
            box = placeThis.getDefaultState().getSelectedBoundingBox(mc.world, pos);

        List<Entity> l = mc.world.getEntitiesWithinAABBExcludingEntity(null, box);
        for (Entity e : l) {
            if (!(e instanceof EntityItem) && !(e instanceof EntityExpBottle) && !e.isDead)
                return false;
        }
        return true;
    }

    public static boolean blockCollisionCheck(BlockPos pos, boolean ignoreCrystal) {

        AxisAlignedBB box = new AxisAlignedBB(pos);
        List<Entity> l = mc.world.getEntitiesWithinAABBExcludingEntity(null, box);
        for (Entity e : l) {
            if (!(e instanceof EntityItem) && !(e instanceof EntityExpBottle) && !e.isDead && (!(e instanceof EntityEnderCrystal) || !ignoreCrystal))
                return false;
        }
        return true;
    }


    public static EnumFacing getDirForPlacingBlockAtPos(BlockPos pos) {
        for (EnumFacing enumFacing : EnumFacing.values()) {
            if (mc.world.getBlockState(pos.offset(enumFacing, -1)).getBlock().material.isSolid()) {
                if (getPointOnBlockFace(pos.offset(enumFacing, -1), enumFacing).getKey() != null)
                    return enumFacing;
            }
        }
        return null;
    }

    public static Pair<Vec3d, Boolean> getPointOnBlockFace(BlockPos pos, EnumFacing facing) {
        Vec3d vec = RaytraceUtil.getPointOnBlockFace(pos, facing, AntiCheatConfig.getInstance().placeStrict.getValue());
        if (vec != null)
            return new Pair<>(PlayerUtil.getDistance(vec) < AntiCheatConfig.getInstance().placeRange.getValue() ? vec : null, false);
        else {
            //can't see
            vec = new Vec3d(pos.getX() + 0.5 + facing.getDirectionVec().getX() * 0.5, pos.getY() + 0.5 + facing.getDirectionVec().getY() * 0.5, pos.getZ() + 0.5 + facing.getDirectionVec().getZ() * 0.5);
            if (PlayerUtil.getDistance(vec) < AntiCheatConfig.getInstance().placeWallRange.getValue())
                return new Pair<>(vec, false);
            if (PlayerUtil.getDistance(vec) < AntiCheatConfig.getInstance().placeRange.getValue() && AntiCheatConfig.getInstance().raytrace.getValue() && RaytraceUtil.getRotationForBypass(Spark.rotationManager.getLegitRotations(vec)[0]) != null)
                return new Pair<>(vec, true);
        }
        return new Pair<>(null, null);
    }

    @Nullable
    public static Pair<Vec3d, EnumFacing> getInteractPoint(BlockPos pos) {
        double best = 0;
        boolean wall = false;
        Vec3d v = null;
        EnumFacing f = null;
        for (EnumFacing facing : EnumFacing.VALUES) {
            Vec3d vec = RaytraceUtil.getPointOnBlockFace(pos, facing, AntiCheatConfig.getInstance().placeStrict.getValue());
            if (vec != null)
                if (PlayerUtil.getDistance(vec) < AntiCheatConfig.getInstance().placeRange.getValue()) {
                    if (v == null || mc.player.getDistance(v.x, v.y, v.z) < best) {
                        v = vec;
                        f = facing;
                        wall = true;
                    }
                } else if (!wall) {
                    //can't see
                    vec = new Vec3d(pos.getX() + 0.5 + facing.getDirectionVec().getX() * 0.5, pos.getY() + 0.5 + facing.getDirectionVec().getY() * 0.5, pos.getZ() + 0.5 + facing.getDirectionVec().getZ() * 0.5);
                    if (PlayerUtil.getDistance(vec) < AntiCheatConfig.getInstance().placeWallRange.getValue()) {
                        if (v == null || mc.player.getDistance(v.x, v.y, v.z) < best) {
                            v = vec;
                            f = facing;
                        }
                    }
                }
        }
        if (v != null) return new Pair<>(v, f);
        return null;
    }


    public static boolean processRightClickBlockForPlace(BlockPos pos, EnumFacing side, boolean clientSided, EnumHand hand, Vec3d hit) {
        final boolean activated = !mc.player.isSneaking() && mc.world.getBlockState(pos).getBlock().onBlockActivated(mc.world, pos, mc.world.getBlockState(pos), mc.player, hand, side, 0, 0, 0);
        if (activated)
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        boolean placed = processRightClickBlock(pos, side, clientSided, hand, hit);
        if (activated)
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        return placed;

    }

    public static boolean processRightClickBlock(BlockPos pos, EnumFacing side, boolean clientSided, EnumHand hand, Vec3d hit) {
        switch (AntiCheatConfig.getInstance().placeSwing.getValue()) {
            case "Normal":
                mc.player.swingArm(hand);
                break;
            case "Packet":
                mc.player.connection.sendPacket(new CPacketAnimation(hand));
                break;
        }

        if (!clientSided)
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, side, hand, (float) hit.x, (float) hit.y, (float) hit.z));
        else {
            if (mc.playerController.processRightClickBlock(mc.player, mc.world, pos, side, hit, hand) != EnumActionResult.SUCCESS)
                return false;
        }

        if (mc.player.getHeldItemMainhand().getItem() == Items.WATER_BUCKET || mc.player.getHeldItemMainhand().getItem() == Items.BUCKET || mc.player.getHeldItemMainhand().getItem() == Items.LAVA_BUCKET) {
            mc.playerController.processRightClick(mc.player, mc.world, hand);
        }
        return true;
    }

    public enum BlockPlaceResult {
        FAILED, WAIT, PLACED
    }

}
