package me.wallhacks.spark.systems.module.modules.movement;

import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.event.player.UpdateWalkingPlayerEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;

import java.util.Arrays;

@Module.Registration(name = "Step", description = "Makes your feet long!")
public class Step extends Module {
    public static Step INSTANCE;
    public Step() {
        INSTANCE = this;
    }
    public ModeSetting mode = new ModeSetting("Mode",this,"Vanilla", Arrays.asList("Vanilla", "Packet", "AAC"),"General");
    public IntSetting height = new IntSetting("StepHeight",this,2,1,4,"General");
    private IntSetting cooldown = new IntSetting("Cooldown",this,0,0,20,"General");

    int ticksSinceLastStep = 0;

    @SubscribeEvent
    public void onUpdateEvent(PlayerUpdateEvent event) {
        ticksSinceLastStep++;

        if (!MC.mc.player.collidedHorizontally) return;
        if (MC.mc.player.isOnLadder() || MC.mc.player.isInWater() || MC.mc.player.isInLava() || MC.mc.player.movementInput.jump || MC.mc.player.noClip) return;
        if (MC.mc.player.moveForward == 0 && MC.mc.player.moveStrafing == 0) return;

        if(cooldown.getValue() > ticksSinceLastStep) return;


        if(mode.is("Packet")) {
            final double n = get_n();

            if (n < 0 || n > height.getValue()) return;
            if(!MC.mc.player.onGround)
                return;
            if (n > 1.5) {
                MC.mc.player.connection.sendPacket(new CPacketPlayer.Position(MC.mc.player.posX, MC.mc.player.posY + 0.42, MC.mc.player.posZ, MC.mc.player.onGround));
                MC.mc.player.connection.sendPacket(new CPacketPlayer.Position(MC.mc.player.posX, MC.mc.player.posY + 0.78, MC.mc.player.posZ, MC.mc.player.onGround));
                MC.mc.player.connection.sendPacket(new CPacketPlayer.Position(MC.mc.player.posX, MC.mc.player.posY + 0.63, MC.mc.player.posZ, MC.mc.player.onGround));
                MC.mc.player.connection.sendPacket(new CPacketPlayer.Position(MC.mc.player.posX, MC.mc.player.posY + 0.51, MC.mc.player.posZ, MC.mc.player.onGround));
                MC.mc.player.connection.sendPacket(new CPacketPlayer.Position(MC.mc.player.posX, MC.mc.player.posY + 0.9, MC.mc.player.posZ, MC.mc.player.onGround));
                MC.mc.player.connection.sendPacket(new CPacketPlayer.Position(MC.mc.player.posX, MC.mc.player.posY + 1.21, MC.mc.player.posZ, MC.mc.player.onGround));
                MC.mc.player.connection.sendPacket(new CPacketPlayer.Position(MC.mc.player.posX, MC.mc.player.posY + 1.45, MC.mc.player.posZ, MC.mc.player.onGround));
                MC.mc.player.connection.sendPacket(new CPacketPlayer.Position(MC.mc.player.posX, MC.mc.player.posY + 1.43, MC.mc.player.posZ, MC.mc.player.onGround));
                MC.mc.player.setPosition(MC.mc.player.posX, MC.mc.player.posY + n, MC.mc.player.posZ);
            }
            else if (n > 1) {
                MC.mc.player.connection.sendPacket(new CPacketPlayer.Position(MC.mc.player.posX, MC.mc.player.posY + 0.41999998688698, MC.mc.player.posZ, MC.mc.player.onGround));
                MC.mc.player.connection.sendPacket(new CPacketPlayer.Position(MC.mc.player.posX, MC.mc.player.posY + 0.7531999805212, MC.mc.player.posZ, MC.mc.player.onGround));
                MC.mc.player.connection.sendPacket(new CPacketPlayer.Position(MC.mc.player.posX, MC.mc.player.posY + 1.00133597911214, MC.mc.player.posZ, MC.mc.player.onGround));
                MC.mc.player.connection.sendPacket(new CPacketPlayer.Position(MC.mc.player.posX, MC.mc.player.posY + 1.16610926093821, MC.mc.player.posZ, MC.mc.player.onGround));
                MC.mc.player.connection.sendPacket(new CPacketPlayer.Position(MC.mc.player.posX, MC.mc.player.posY + 1.24918707874468, MC.mc.player.posZ, MC.mc.player.onGround));
                MC.mc.player.connection.sendPacket(new CPacketPlayer.Position(MC.mc.player.posX, MC.mc.player.posY + 1.1707870772188, MC.mc.player.posZ, MC.mc.player.onGround));
                MC.mc.player.setPosition(MC.mc.player.posX, MC.mc.player.posY + n, MC.mc.player.posZ);
            }
            else if (n > 0.5) {
                MC.mc.player.connection.sendPacket(new CPacketPlayer.Position(MC.mc.player.posX, MC.mc.player.posY + 0.41999998688698, MC.mc.player.posZ, MC.mc.player.onGround));
                MC.mc.player.connection.sendPacket(new CPacketPlayer.Position(MC.mc.player.posX, MC.mc.player.posY + 0.7531999805212, MC.mc.player.posZ, MC.mc.player.onGround));
                MC.mc.player.setPosition(MC.mc.player.posX, MC.mc.player.posY + n, MC.mc.player.posZ);
            }
            ticksSinceLastStep = 0;
        }
    }


    //aac mode - not skided from salhack
    //geza3D: ANGERY
    private double previousX, previousY, previousZ;
    private double offsetX, offsetY, offsetZ;
    private double frozenX, frozenZ;
    private byte cancelStage;

    @SubscribeEvent
    public void onUpdateEvent(UpdateWalkingPlayerEvent.Pre event) {
        if(mode.is("AAC")){
            offsetX = 0;
            offsetY = 0;
            offsetZ = 0;
            MC.mc.player.stepHeight = MC.mc.player.onGround && MC.mc.player.collidedHorizontally && cancelStage == 0 && MC.mc.player.posY % 1 == 0 ? 1.1F : 0.5F;
            if (cancelStage == -1) {
                cancelStage = 0;
                return;
            }

            double yDist = MC.mc.player.posY - previousY;
            double hDistSq = (MC.mc.player.posX - previousX) * (MC.mc.player.posX - previousX) + (MC.mc.player.posZ - previousZ) * (MC.mc.player.posZ - previousZ);

            if (yDist > 0.5 && yDist < 1.05 && hDistSq < 1 && cancelStage == 0)
            {
                MC.mc.player.connection.sendPacket(new CPacketPlayer.Position(previousX, previousY + 0.42, previousZ, false));
                offsetX = previousX - MC.mc.player.posX;
                offsetY = 0.755 - yDist;
                offsetZ = previousZ - MC.mc.player.posZ;

                frozenX = previousX;
                frozenZ = previousZ;
                MC.mc.player.stepHeight = 1.05F;
                cancelStage = 1;
            }


            switch (cancelStage)
            {
                case 1:
                    cancelStage = 2;
                    MC.mc.player.setEntityBoundingBox((MC.mc.player.getEntityBoundingBox().offset(frozenX - MC.mc.player.posX, 0, frozenZ - MC.mc.player.posZ)));
                    break;
                case 2:
                    event.setCanceled(true);
                    cancelStage = -1;
                    break;
            }

            previousX = MC.mc.player.posX;
            previousY = MC.mc.player.posY;
            previousZ = MC.mc.player.posZ;

            if (offsetX != 0 || offsetY != 0 || offsetZ != 0) {
                MC.mc.player.posX += offsetX;
                MC.mc.player.setEntityBoundingBox((MC.mc.player.getEntityBoundingBox().offset(0, offsetY, 0)));
                MC.mc.player.posZ += offsetZ;
            }
        }
    }



    public double get_n() {
        double max_y = -1;

        final AxisAlignedBB grow = MC.mc.player.getEntityBoundingBox().offset(0, 0.05, 0).grow(0.05);

        if (!MC.mc.world.getCollisionBoxes(MC.mc.player, grow.offset(0, 2, 0)).isEmpty()) return 100;

        for (final AxisAlignedBB aabb : MC.mc.world.getCollisionBoxes(MC.mc.player, grow)) {

            if (aabb.maxY > max_y) {
                max_y = aabb.maxY;
            }

        }

        return max_y - MC.mc.player.posY;

    }
}
