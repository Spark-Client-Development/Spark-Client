package me.wallhacks.spark.util.player;

import me.wallhacks.spark.util.MC;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.*;

import java.util.List;

public class PlayerUtil implements MC {

    public static Block isColliding(double posX, double posY, double posZ) {
        Block block = null;
        if (mc.player != null) {
            final AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(posX, posY, posZ) : mc.player.getEntityBoundingBox().contract(0.0d, 0.0d, 0.0d).offset(posX, posY, posZ);
            int y = (int) bb.minY;
            for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; x++) {
                for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; z++) {
                    block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                }
            }
        }
        return block;
    }

    public static Vec3d interpolateEntity(final Entity entity, final float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time);
    }

    public static boolean isInBlocks(Entity entity, AxisAlignedBB entityBoundingBox) {
        return !mc.world.getCollisionBoxes(entity, entityBoundingBox).isEmpty();
    }
    public static Vec3d getVelocity() {
        return new Vec3d(mc.player.motionX, mc.player.motionY, mc.player.motionZ);
    }

    public static boolean isInBlocks(Entity entity) {
        return isInBlocks(entity, entity.getEntityBoundingBox());
    }

    public static BlockPos getPlayerPosFloored(final Entity p_Player)
    {
        return new BlockPos(Math.floor(p_Player.posX), Math.floor(p_Player.posY), Math.floor(p_Player.posZ));
    }
    public static BlockPos getPlayerPosFloored(final Vec3d pos, double h)
    {
        return new BlockPos(Math.floor(pos.x), Math.floor(pos.y+h), Math.floor(pos.z));
    }
    public static BlockPos getPlayerPosFloored(final double x, final double y, final double z)
    {
        return new BlockPos(Math.floor(x), Math.floor(y), Math.floor(z));
    }







    public static BlockPos GetPlayerPosHighFloored(final Entity p_Player)
    {
        return getPlayerPosFloored(p_Player,0.2);
    }
    public static BlockPos getPlayerPosFloored(final Entity p_Player, double y)
    {
        return new BlockPos(Math.floor(p_Player.posX), Math.floor(p_Player.posY+y), Math.floor(p_Player.posZ));
    }

    public static boolean MoveCenter(BlockPos pos,boolean onlyNeedsToBeInBlock){

        AxisAlignedBB bb = mc.player.boundingBox;
        boolean isInCenter = (bb.maxX < pos.getX()+1 && bb.maxZ < pos.getZ()+1 && bb.minX > pos.getX() && bb.minZ > pos.getZ());
        if(onlyNeedsToBeInBlock && isInCenter)
            return true;

        Vec3d Center = new Vec3d(pos.getX()+0.5,pos.getY(),pos.getZ()+0.5);
        double l_XDiff = Math.abs(Center.x - mc.player.posX);
        double l_ZDiff = Math.abs(Center.z - mc.player.posZ);

        if (l_XDiff <= 0.1 && l_ZDiff <= 0.1)
        {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
            return true;
        }
        else
        {
            double l_MotionX = Center.x-mc.player.posX;
            double l_MotionZ = Center.z-mc.player.posZ;

            mc.player.motionX = (l_MotionX/3);
            mc.player.motionZ = (l_MotionZ/3);
            return false;
        }

    }



    public static boolean CanInteractVanillaCheck(Vec3d pos, double eyeHeight){
        //vanilla ray trace check
        return (getDistanceSq(pos) < (canEntityBeSeen(pos,eyeHeight) ? 36 : 9));

    }
    public static boolean CanInteractVanillaCheck(Entity entity){
        //vanilla ray trace check
        return (mc.player.getDistanceSq(entity) < (mc.player.canEntityBeSeen(entity) ? 36 : 9));

    }

    public static double getDistanceSq(Vec3d pos) {
        double d0 = mc.player.posX - pos.x;
        double d1 = mc.player.posY - pos.y;
        double d2 = mc.player.posZ - pos.z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }
    public static float getDistanceToHead (Vec3d pos){
        float f = (float)(mc.player.posX - pos.x);
        float f1 = (float)(mc.player.posY + mc.player.getEyeHeight() - pos.y);
        float f2 = (float)(mc.player.posZ - pos.z);
        return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
    }
    public static float getDistance (Vec3d pos){
        float f = (float)(mc.player.posX - pos.x);
        float f1 = (float)(mc.player.posY - pos.y);
        float f2 = (float)(mc.player.posZ - pos.z);
        return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
    }
    public static float getDistance (ChunkPos pos){
        float f = (float)(mc.player.posX - (pos.getXStart()+8));
        float f2 = (float)(mc.player.posZ - (pos.getZStart()+8));
        return MathHelper.sqrt(f * f + f2 * f2);
    }

    public static Vec3d getEyePos (){

        return new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
    }


    public static float getDistance (BlockPos block){
        Vec3d pos = new Vec3d(block.getX()+0.5,block.getY()+0.5,block.getZ()+0.5);
        float f = (float)(mc.player.posX - pos.x);
        float f1 = (float)(mc.player.posY - pos.y);
        float f2 = (float)(mc.player.posZ - pos.z);
        return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
    }





    public static boolean canEntityBeSeen(Vec3d pos, double eyeHeight) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.x, pos.y + eyeHeight, pos.z), false, true, false) == null;
    }

    public static Vec3d getClosestPoint(List<Vec3d> list){
        if(list.size() == 0)
            return null;

        Vec3d closest = null;
        float closestDis = Float.MAX_VALUE;

        for (Vec3d pos : list) {
            float dis = getDistanceToHead(pos);
            if(dis < closestDis)
            {
                closest = pos;
                closestDis = dis;
            }
        }
        return closest;
    }

    public static boolean isOnLiquid()
    {


        if (mc.player != null)
        {
            final AxisAlignedBB bb = mc.player.getRidingEntity() != null
                    ? mc.player.getRidingEntity().boundingBox.contract(0.0d, 0.0d, 0.0d)
                    : mc.player.boundingBox.contract(0.0d, 0.0d, 0.0d);
            boolean onLiquid = false;
            int y = (int) (bb.minY-0.1f);
            for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1.0D); x++)
            {
                for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1.0D); z++)
                {
                    final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (block != Blocks.AIR)
                    {
                        if (!(block instanceof BlockLiquid))
                        {
                            if (!(mc.world.getBlockState(new BlockPos(x, y+1, z)).getBlock() instanceof BlockLiquid))
                            {
                                return false;

                            }
                        }

                        onLiquid = true;
                    }

                }
            }

            return onLiquid;
        }

        return false;
    }

}
