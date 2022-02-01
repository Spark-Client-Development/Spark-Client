package me.wallhacks.spark.util.player;


import me.wallhacks.spark.util.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationUtil implements MC {

    public static  float get_rotation_yaw() {

        float rotation_yaw = mc.player.rotationYaw;
        if (mc.player.moveForward < 0.0f) {
            rotation_yaw += 180.0f;
        }
        float n = 1.0f;
        if (mc.player.moveForward < 0.0f) {
            n = -0.5f;
        }
        else if (mc.player.moveForward > 0.0f) {

            n = 0.5f;
        }
        if (mc.player.moveStrafing > 0.0f) {
            rotation_yaw -= 90.0f * n;

        }
        if (mc.player.moveStrafing < 0.0f) {
            rotation_yaw += 90.0f * n;
        }
        return rotation_yaw * 0.017453292f;
    }
    public static double[] directionSpeed(double speed)
    {

        float forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw
                + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();

        if (forward != 0)
        {
            if (side > 0)
            {
                yaw += (forward > 0 ? -45 : 45);
            }
            else if (side < 0)
            {
                yaw += (forward > 0 ? 45 : -45);
            }
            side = 0;

            // forward = clamp(forward, 0, 1);
            if (forward > 0)
            {
                forward = 1;
            }
            else if (forward < 0)
            {
                forward = -1;
            }
        }

        final double sin = Math.sin(Math.toRadians(yaw + 90));
        final double cos = Math.cos(Math.toRadians(yaw + 90));
        final double posX = (forward * speed * cos + side * speed * sin);
        final double posZ = (forward * speed * sin - side * speed * cos);
        return new double[]
                { posX, posZ };
    }


    public static float[] getViewRotations(Vec3d vec, EntityPlayer me)
    {
        Vec3d eyesPos = me.getPositionEyes(Minecraft.getMinecraft().getRenderPartialTicks());

        return getViewRotations(vec,eyesPos,me);
    }
    public static float[] getViewRotations(Vec3d vec, Vec3d eyesPos, EntityPlayer me)
    {
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        //return new float[]{ me.rotationYaw + MathHelper.wrapDegrees(yaw - me.rotationYaw), me.rotationPitch + MathHelper.wrapDegrees(pitch - me.rotationPitch) };

        float[] myRot = new float[]{me.rotationYaw,me.rotationPitch};

        return new float[] {myRot[0] + MathHelper.wrapDegrees(yaw-myRot[0]), myRot[1]+ MathHelper.wrapDegrees(pitch-myRot[1]) };

    }


}
