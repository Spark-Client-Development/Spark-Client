package me.wallhacks.spark.util.player;

import me.wallhacks.spark.util.MC;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;

import java.util.ArrayList;
import java.util.List;

public class RaytraceUtil implements MC {


    public static Vec3d getPointToLookAtEntity(Entity entity){
        //get list of all visible points
        List<Vec3d> list = (getVisiblePointsForEntity(entity));

        //get closest to entity center
        Vec3d closest = null;
        double closestDis = Float.MAX_VALUE;

        for (Vec3d pos : list) {
            double dis = Math.abs(pos.y-entity.boundingBox.getCenter().y);
            if(dis < closestDis){closest = pos;closestDis = dis;}
        }
        return closest;
    }
    public static List<Vec3d> getVisiblePointsForEntity(Entity entity){
        return getVisiblePointsForBox(entity.boundingBox.shrink(0.1));
    }
    public static List<Vec3d> getVisiblePointsForBox(AxisAlignedBB box)
    {
        Vec3d from = new Vec3d(mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ);

        ArrayList<Vec3d> validHits = new ArrayList<>();


        double d0 = 1.0D / ((box.maxX - box.minX) + 1.0D);
        double d1 = 1.0D / ((box.maxY - box.minY) + 1.0D);
        double d2 = 1.0D / ((box.maxZ - box.minZ) + 1.0D);
        double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
        double d4 = (1.0D - Math.floor(1.0D / d1) * d1) / 2.0D;
        double d5 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;


        if (d0 >= 0.0D && d1 >= 0.0D && d2 >= 0.0D)
        {
            for (float f = 1.0F; f >= 0.0F; f -= d0)
            {
                for (float f1 = 1.0F; f1 >= 0.0F; f1 -= d1)
                {
                    for (float f2 = 1.0F; f2 >= 0.0F; f2 -= d2)
                    {
                        double d6 = box.minX + (box.maxX - box.minX) * (double)f;
                        double d7 = box.minY + (box.maxY - box.minY) * (double)f1;
                        double d8 = box.minZ + (box.maxZ - box.minZ) * (double)f2;

                        RayTraceResult res = mc.world.rayTraceBlocks(from,new Vec3d(d6 + d3, d7 + d4, d8 + d5),false);

                        //we did not hit shit thats not our target
                        if (res == null)
                            validHits.add( new Vec3d(d6 + d3, d7 + d4, d8 + d5));

                    }
                }
            }
        }
        return validHits;

    }



    public static Vec3d getPointOnBlockFace(BlockPos pos, EnumFacing facing, boolean needsToSeeFace){

        ArrayList<Vec3d> validHits = new ArrayList<>();
        Vec3d from = new Vec3d(mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ);

        Vec3i facingVec = facing.getDirectionVec();
        for (double x = 0.1; x <= 0.9; x +=0.4)
        {
            double x1 = (facingVec.getX() == 0) ? x : 0.5+0.5*facingVec.getX();

            for (double y = 0.1; y <= 0.9; y +=0.4)
            {
                double y1 = (facingVec.getY() == 0) ? y : 0.5+0.5*facingVec.getY();

                for (double z = 0.1; z <= 0.9; z +=0.4)
                {
                    double z1 = (facingVec.getZ() == 0) ? z : 0.5+0.5*facingVec.getZ();

                    Vec3d vec = new Vec3d(pos.getX()+x1
                            ,pos.getY()+y1,pos.getZ()+z1);



                    RayTraceResult res = mc.world.rayTraceBlocks(from,vec,false);

                    //we did not hit shit thats not our target
                    if (res == null || (pos.equals(res.getBlockPos()) && (!needsToSeeFace || facing.equals(res.sideHit))))
                        validHits.add(vec);


                }

            }
        }

        return PlayerUtil.getClosestPoint(validHits);
    }

    public static ArrayList<Vec3d> getPointToLookAtBlock(BlockPos pos){

        ArrayList<Vec3d> validHits = new ArrayList<>();
        Vec3d from = new Vec3d(mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ);

        for (double x = 0.0; x <= 1; x +=0.25) {
                for (double z = 0.0; z <= 1; z +=0.25) {
                    for (double y = 1.0; y >= 0; y -=0.25) {
                    if (x == 1 || x == 0 || y == 1 || y == 0 || z == 1 || z == 0) {
                        Vec3d vec = new Vec3d(pos.getX() + x
                                , pos.getY() + y, pos.getZ() + z);

                        RayTraceResult res = mc.world.rayTraceBlocks(from, vec, false);

                        //we did not hit shit thats not our target
                        if (res == null || (pos.equals(res.getBlockPos())))
                            validHits.add(vec);

                    }
                }


            }
        }

        return validHits;
    }



    public static Vec3d getPointToBreakPlaceCrystal(BlockPos pos){

        ArrayList<Vec3d> validHits = new ArrayList<>();
        Vec3d from = new Vec3d(mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ);

        for (double x = 0.05; x <= 0.95; x +=0.45)
        {
            for (double z = 0.05; z <= 0.95; z +=0.45)
            {
                Vec3d vec = new Vec3d(pos.getX()+x,pos.getY()+1,pos.getZ()+z);
                RayTraceResult res = mc.world.rayTraceBlocks(from,vec,false);

                //we did not hit shit thats not our target
                if (res == null || (pos.equals(res.getBlockPos())))
                    validHits.add(vec);


            }
        }

        //get closest to entity center
        Vec3d closest = null;
        double closestDis = Float.MAX_VALUE;

        for (Vec3d hit : validHits) {
            double dis = Math.abs(pos.getX()+0.5-hit.x) + Math.abs(pos.getZ()+0.5-hit.z);
            if(dis < closestDis){ closest = hit; closestDis = dis;}
        }
        return closest;
    }

    public static float[] getRotationForBypass(float limit) {
        float rotation[] = null;
        float best = Float.MAX_VALUE;
        for (float yaw = limit - 5; yaw <= limit + 5; yaw += 1) {
            for (float pitch = -90; pitch <= -40; pitch += 1) {
                float difference = Math.abs(limit - yaw);
                if ((rotation == null || difference < best) && isRotationGood(yaw, pitch)) {
                    best = difference;
                    rotation = new float[]{yaw, pitch};
                }
            }
        }
        return rotation;
    }

    public static boolean isRotationGood(float yaw, float pitch) {
        Vec3d eyes = new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.eyeHeight, mc.player.posZ);
        Vec3d look = getVectorForRotation(pitch, yaw);
        look = eyes.add(look.x * 100, look.y * 100, look.z * 100);
        return mc.world.rayTraceBlocks(eyes, look, false, true, false) == null;
    }

    public static final Vec3d getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3d((double) (f1 * f2), (double) f3, (double) (f * f2));
    }
}
