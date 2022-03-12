package me.wallhacks.spark.manager;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.event.player.UpdateWalkingPlayerEvent;
import me.wallhacks.spark.event.render.RenderEntityEvent;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.player.RaytraceUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RotationManager implements MC {
    public RotationManager () {
        Spark.eventBus.register(this);
    }

    boolean cancelNextWalkingUpdate = false;
    int DoFakeRotationForTicks = 0;

    Float FakeRotationYaw = null;
    Float FakeRotationPitch = null;

    boolean isRaytraceBypassDone = false;

    public void setCancelNextWalkingUpdate(){
        cancelNextWalkingUpdate = true;
    }
    public boolean sendPosPacketAndCancelNextUpdatePacket(Vec3d pos)
    {
        mc.player.connection.sendPacket(new CPacketPlayer.Position(pos.x,pos.y,pos.z,mc.player.onGround));

        setCancelNextWalkingUpdate();


        return true;
    }

    public void setDoFakeRotationForTicks(int doFakeRotationForTicks) {
        DoFakeRotationForTicks = doFakeRotationForTicks;
    }

    //just call this method for rotation
    //it return true if rotation was reached
    //yaw step is how fast it rotates and stay ticks is how many ticks after rotation we should keep that rotation
    //allow sendMultiplePackets if for doing multiple rotation things in one tick(bad idea for ca)

    public boolean rotate(float[] rotation, boolean instant) {
        return rotate(rotation,instant,false);
    }

    public boolean rotate(float[] rotation, boolean instant,boolean multiSpoof) {
        if(!cancelNextWalkingUpdate) {
            if(FakeRotationYaw == null)
                FakeRotationYaw = mc.player.lastReportedYaw;

            if(FakeRotationYaw < rotation[0])
                FakeRotationYaw = (float) Math.min(FakeRotationYaw+AntiCheatConfig.getInstance().rotStep.getValue(), rotation[0]);
            else if(FakeRotationYaw > rotation[0])
                FakeRotationYaw = (float) Math.max(FakeRotationYaw-AntiCheatConfig.getInstance().rotStep.getValue(), rotation[0]);

            FakeRotationPitch = rotation[1];
            DoFakeRotationForTicks = 1+Math.max(0,AntiCheatConfig.getInstance().stayTicks.getValue());

            if(instant)
            {
                mc.player.onUpdateWalkingPlayer();
                cancelNextWalkingUpdate = true;
            }

            //see if we have reached rotation we tolerate offset of 2 degrees
            return (Math.abs(rotation[0] - mc.player.lastReportedYaw) <= 2 && Math.abs(rotation[1] - mc.player.lastReportedPitch) <= 2);

        }
        else{
            if(multiSpoof){
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotation[0],rotation[1],mc.player.onGround));
                return true;
            }
        }

        return false;
    }


    public boolean setFakePitch(float Pitch,int stayTicks) {
        if(!cancelNextWalkingUpdate) {
            FakeRotationPitch = Pitch;
            DoFakeRotationForTicks = 1+Math.max(0,stayTicks);

            return true;
        }
        return false;
    }

    float realRotationYaw;
    float realRotationPitch;





    @SubscribeEvent
    void OnUpdateWalkingEvent(UpdateWalkingPlayerEvent.Pre event) {
        realRotationYaw = mc.player.rotationYaw;
        realRotationPitch = mc.player.rotationPitch;

        if (cancelNextWalkingUpdate)
        {
            event.setCanceled(true);
            cancelNextWalkingUpdate = false;
        }
        else {
            if (FakeRotationYaw != null || FakeRotationPitch != null) {

                if(FakeRotationYaw != null)
                    mc.player.rotationYaw = FakeRotationYaw;

                if(FakeRotationPitch != null)
                    mc.player.rotationPitch = FakeRotationPitch;



                //stay ticks keeps fake rotation for a few update loops
                if (DoFakeRotationForTicks <= 0)
                {
                    FakeRotationPitch = null;
                    FakeRotationYaw = null;
                }
                else{
                    DoFakeRotationForTicks--;
                }

            }

        }
    }

    @SubscribeEvent
    void OnUpdateWalkingEvent(UpdateWalkingPlayerEvent.Post event){
        EntityPlayerSP player = Minecraft.getMinecraft().player;

        player.rotationYaw = realRotationYaw;
        player.rotationPitch = realRotationPitch;
    }


    public float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = new Vec3d(mc.player.posX,mc.player.posY+mc.player.getEyeHeight(),mc.player.posZ);

        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        //return new float[]{ me.rotationYaw + MathHelper.wrapDegrees(yaw - me.rotationYaw), me.rotationPitch + MathHelper.wrapDegrees(pitch - me.rotationPitch) };

        float[] myRot = new float[]{mc.player.rotationYaw,mc.player.rotationPitch};
        if(FakeRotationYaw != null)
            myRot[0] = FakeRotationYaw;
        if(FakeRotationPitch != null)
            myRot[1] = FakeRotationPitch;

        return new float[] {myRot[0] + MathHelper.wrapDegrees(yaw-myRot[0]), myRot[1]+MathHelper.wrapDegrees(pitch-myRot[1]) };
    }

    public boolean isRaytraceBypassDone() {
        return isRaytraceBypassDone;
    }

    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        isRaytraceBypassDone = RaytraceUtil.isRotationGoodForRaytrace(mc.player.lastReportedYaw,mc.player.lastReportedPitch);
    }

    public Float getFakeRotationPitch() {
        return FakeRotationPitch;
    }

    public Float getFakeRotationYaw() {
        return FakeRotationYaw;
    }

    //render rotation
    private static float headyaw = 0;
    private static float pitch = 0;
    private static float lastheadyaw = 0;
    private static float lastpitch = 0;

    @SubscribeEvent
    public void preRender(RenderEntityEvent.Pre event){
        if(event.getEntity() == mc.player) {
            headyaw = mc.player.rotationYawHead;
            pitch = mc.player.rotationPitch;
            lastpitch = mc.player.prevRotationPitch;
            lastheadyaw = mc.player.prevRotationYawHead;

            if(FakeRotationPitch != null)
            {
                mc.player.prevRotationPitch = FakeRotationPitch;
            }
            if(FakeRotationYaw != null)
            {
                mc.player.rotationYawHead = FakeRotationYaw;
                mc.player.rotationPitch = FakeRotationPitch;
                mc.player.prevRotationYawHead = FakeRotationYaw;
            }
        }
    }
    @SubscribeEvent
    public void preRender(RenderEntityEvent.Post event){
        if(event.getEntity() == mc.player) {
            mc.player.rotationYawHead = headyaw;
            mc.player.rotationPitch = pitch;
            mc.player.prevRotationYawHead = lastheadyaw;
            mc.player.prevRotationPitch = lastpitch;
        }
    }
}
