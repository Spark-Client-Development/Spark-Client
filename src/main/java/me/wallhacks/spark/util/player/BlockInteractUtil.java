package me.wallhacks.spark.util.player;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.SpecBlockSwitchItem;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;
import me.wallhacks.spark.util.player.itemswitcher.ItemSwitcher;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.BlockSwitchItem;

import java.util.List;

public class BlockInteractUtil implements MC {

    public static BlockPlaceResult tryPlaceBlock(BlockPos pos, BlockSwitchItem switcher, boolean clientSided, boolean checkEntities, int rotationStayTicks) {
        return tryPlaceBlock(pos, switcher, clientSided, checkEntities, rotationStayTicks, false);
    }

    public static BlockPlaceResult tryPlaceBlock(BlockPos pos, BlockSwitchItem switcher, boolean clientSided, boolean checkEntities, int rotationStayTicks, boolean allowSendMultipleRotPacket){

        if(!mc.world.getBlockState(pos).getMaterial().isReplaceable())
            return BlockPlaceResult.FAILED;

        Item willuse = null;
        if(switcher != null)
        {
            willuse = ItemSwitcher.predictItem(switcher, ItemSwitcher.switchType.Both);
            if(!(willuse instanceof ItemBlock) && willuse != Items.WATER_BUCKET && willuse != Items.LAVA_BUCKET)
                return BlockPlaceResult.FAILED;
        }


        if(checkEntities && !blockCollisionCheck(pos,willuse == null ? null : ((ItemBlock)willuse).getBlock()))
            return BlockPlaceResult.FAILED;

        EnumFacing face = getDirForPlacingBlockAtPos(pos);

        if(face == null)
            return BlockPlaceResult.FAILED;

        BlockPos placeOn = pos.offset(face, -1);
        Vec3d hitVec = getPointOnBlockFace(placeOn,face);


        EnumHand hand = ItemSwitcher.Switch(switcher,ItemSwitcher.switchType.Both);
        if(hand == null)
            return BlockPlaceResult.FAILED;

        if(AntiCheatConfig.getInstance().getBlockRotate())
        if(!Spark.rotationManager.rotate(Spark.rotationManager.getLegitRotations(hitVec), AntiCheatConfig.getInstance().getBlockRotStep(), rotationStayTicks,allowSendMultipleRotPacket))
            return BlockPlaceResult.WAIT;



        mc.playerController.syncCurrentPlayItem();

        return processRightClickBlockForPlace(placeOn,face,clientSided,hand,hitVec) ? BlockPlaceResult.PLACED : BlockPlaceResult.FAILED;
    }

    public static BlockPlaceResult tryPlaceBlockOnBlock(BlockPos pos, EnumFacing face, BlockSwitchItem switcher, boolean clientSided, boolean checkEntities, int rotationStayTicks, boolean allowSendMultipleRotPacket){

        if(!mc.world.getBlockState(pos).getMaterial().isReplaceable())
            return BlockPlaceResult.FAILED;

        Item willuse = ItemSwitcher.predictItem(switcher, ItemSwitcher.switchType.Both);
        if(!(willuse instanceof ItemBlock) && willuse != Items.WATER_BUCKET && willuse != Items.LAVA_BUCKET)
            return BlockPlaceResult.FAILED;

        if(checkEntities && !blockCollisionCheck(pos, ((ItemBlock)willuse).getBlock()))
            return BlockPlaceResult.FAILED;

        if(face == null)
            return BlockPlaceResult.FAILED;

        BlockPos placeOn = pos.offset(face, -1);

        if (mc.world.getBlockState(placeOn).getBlock().isReplaceable(mc.world, placeOn))
            return BlockPlaceResult.FAILED;

        Vec3d hitVec = getPointOnBlockFace(placeOn,face);


        EnumHand hand = ItemSwitcher.Switch(switcher,ItemSwitcher.switchType.Both);
        if(hand == null)
            return BlockPlaceResult.FAILED;

        if(AntiCheatConfig.getInstance().getBlockRotate())
            if(!Spark.rotationManager.rotate(Spark.rotationManager.getLegitRotations(hitVec), AntiCheatConfig.getInstance().getBlockRotStep(), rotationStayTicks,allowSendMultipleRotPacket))
                return BlockPlaceResult.WAIT;



        mc.playerController.syncCurrentPlayItem();

        return processRightClickBlockForPlace(placeOn,face,clientSided,hand,hitVec) ? BlockPlaceResult.PLACED : BlockPlaceResult.FAILED;
    }


    public static BlockPos getBlockPosToPlaceAtBlock(BlockPos pos,boolean checkEntities) {
        if(canPlaceBlockAtPos(pos,checkEntities))
            return pos;
        for (BlockPos x : new BlockPos[]{pos.add(0,-1,0),pos.add(0,0,1),pos.add(0,0,-1),pos.add(1,0,0),pos.add(-1,0,0)}) {
            if(canPlaceBlockAtPos(x,checkEntities))
                return x;
        }
        return null;
    }

    public static boolean canPlaceBlockAtPos(BlockPos p,boolean checkEntities) {

        if(!mc.world.getBlockState(p).getMaterial().isReplaceable())
            return false;
        if(checkEntities && !blockCollisionCheck(p,null))
            return false;
        if(getDirForPlacingBlockAtPos(p) == null)
            return false;

        return true;
    }

    public static boolean blockCollisionCheck(BlockPos pos,Block placeThis){

        AxisAlignedBB box = new AxisAlignedBB(pos);

        if(placeThis != null)
            box = placeThis.getDefaultState().getSelectedBoundingBox(mc.world,pos);

        List<Entity> l = mc.world.getEntitiesWithinAABBExcludingEntity(null, box);
        for(Entity e : l){
            if(!(e instanceof EntityItem) && !(e instanceof EntityExpBottle) && !e.isDead)
                return false;
        }
        return true;
    }


    public static EnumFacing getDirForPlacingBlockAtPos(BlockPos pos){

        for (EnumFacing enumFacing : EnumFacing.values()) {
            if (mc.world.getBlockState(pos.offset(enumFacing, -1)).getBlock().material.isSolid())
            {

                if(null != getPointOnBlockFace(pos.offset(enumFacing, -1),enumFacing))
                    return enumFacing;


            }
        }


        return null;

    }

    public static Vec3d getPointOnBlockFace(BlockPos pos, EnumFacing facing){
        Vec3d vec = RaytraceUtil.getPointOnBlockFace(pos,facing,AntiCheatConfig.getInstance().getBlockStrictRayTrace());
        if(vec != null)
            return PlayerUtil.getDistance(vec) < AntiCheatConfig.getInstance().getBlockPlaceRange() ? vec : null;
        else{
            //can't see
            vec = new Vec3d(pos.getX()+0.5+facing.getDirectionVec().getX()*0.5,pos.getY()+0.5+facing.getDirectionVec().getY()*0.5,pos.getZ()+0.5+facing.getDirectionVec().getZ()*0.5);
            return PlayerUtil.getDistance(vec) < AntiCheatConfig.getInstance().getBlockPlaceWallRange() ? vec : null;
        }


    }


    public static boolean processRightClickBlockForPlace(BlockPos pos, EnumFacing side, boolean clientSided, EnumHand hand, Vec3d hit)
    {
        final boolean activated = !mc.player.isSneaking() && mc.world.getBlockState(pos).getBlock().onBlockActivated(mc.world, pos, mc.world.getBlockState(pos), mc.player, hand, side, 0, 0, 0);
        if (activated)
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        boolean placed = processRightClickBlock(pos,side,clientSided,hand,hit);
        if (activated)
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        return placed;

    }

    public static boolean processRightClickBlock(BlockPos pos, EnumFacing side, boolean clientSided, EnumHand hand, Vec3d hit) {
        switch (AntiCheatConfig.getInstance().PlaceSwing.getValue()) {
            case "Normal":
                mc.player.swingArm(hand);
                break;
            case "Packet":
                mc.player.connection.sendPacket(new CPacketAnimation(hand));
                break;
        }

        if(!clientSided)
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, side, hand, (float)hit.x,(float)hit.y,(float)hit.z));
        else{
            if(mc.playerController.processRightClickBlock(mc.player, mc.world, pos, side, hit, hand) != EnumActionResult.SUCCESS)
                return false;
        }

        if(mc.player.getHeldItemMainhand().getItem() == Items.WATER_BUCKET || mc.player.getHeldItemMainhand().getItem() == Items.BUCKET  || mc.player.getHeldItemMainhand().getItem() == Items.LAVA_BUCKET) {
            mc.playerController.processRightClick(mc.player, mc.world, hand);
        }
        return true;
    }

    public enum BlockPlaceResult {
        FAILED,WAIT,PLACED
    }

}
