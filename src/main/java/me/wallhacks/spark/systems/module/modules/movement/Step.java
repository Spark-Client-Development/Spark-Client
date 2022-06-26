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
    public ModeSetting mode = new ModeSetting("Mode",this,"Vanilla", Arrays.asList("Vanilla", "Packet", "AAC"));
    public IntSetting height = new IntSetting("StepHeight",this,2,1,4);
    private IntSetting cooldown = new IntSetting("Cooldown",this,0,0,20);

    int ticksSinceLastStep = 0;

    @SubscribeEvent
    public void onUpdateEvent(PlayerUpdateEvent event) {
        ticksSinceLastStep++;

        if (!mc.player.collidedHorizontally) return;
        if (mc.player.isOnLadder() || mc.player.isInWater() || mc.player.isInLava() || mc.player.movementInput.jump || mc.player.noClip) return;
        if (mc.player.moveForward == 0 && mc.player.moveStrafing == 0) return;

        if(cooldown.getValue() > ticksSinceLastStep) return;


        if(mode.is("Packet")) {
            final double n = get_n();

            if (n < 0 || n > height.getValue()) return;
            if(!mc.player.onGround)
                return;
            if (n > 1.5) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.42, mc.player.posZ, mc.player.onGround));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.78, mc.player.posZ, mc.player.onGround));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.63, mc.player.posZ, mc.player.onGround));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.51, mc.player.posZ, mc.player.onGround));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.9, mc.player.posZ, mc.player.onGround));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.21, mc.player.posZ, mc.player.onGround));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.45, mc.player.posZ, mc.player.onGround));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.43, mc.player.posZ, mc.player.onGround));
                mc.player.setPosition(mc.player.posX, mc.player.posY + n, mc.player.posZ);
            }
            else if (n > 1) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698, mc.player.posZ, mc.player.onGround));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805212, mc.player.posZ, mc.player.onGround));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00133597911214, mc.player.posZ, mc.player.onGround));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16610926093821, mc.player.posZ, mc.player.onGround));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.24918707874468, mc.player.posZ, mc.player.onGround));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.1707870772188, mc.player.posZ, mc.player.onGround));
                mc.player.setPosition(mc.player.posX, mc.player.posY + n, mc.player.posZ);
            }
            else if (n > 0.5) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698, mc.player.posZ, mc.player.onGround));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805212, mc.player.posZ, mc.player.onGround));
                mc.player.setPosition(mc.player.posX, mc.player.posY + n, mc.player.posZ);
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
            mc.player.stepHeight = mc.player.onGround && mc.player.collidedHorizontally && cancelStage == 0 && mc.player.posY % 1 == 0 ? 1.1F : 0.5F;
            if (cancelStage == -1) {
                cancelStage = 0;
                return;
            }

            double yDist = mc.player.posY - previousY;
            double hDistSq = (mc.player.posX - previousX) * (mc.player.posX - previousX) + (mc.player.posZ - previousZ) * (mc.player.posZ - previousZ);

            if (yDist > 0.5 && yDist < 1.05 && hDistSq < 1 && cancelStage == 0)
            {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(previousX, previousY + 0.42, previousZ, false));
                offsetX = previousX - mc.player.posX;
                offsetY = 0.755 - yDist;
                offsetZ = previousZ - mc.player.posZ;

                frozenX = previousX;
                frozenZ = previousZ;
                mc.player.stepHeight = 1.05F;
                cancelStage = 1;
            }


            switch (cancelStage)
            {
                case 1:
                    cancelStage = 2;
                    mc.player.setEntityBoundingBox((mc.player.getEntityBoundingBox().offset(frozenX - mc.player.posX, 0, frozenZ - mc.player.posZ)));
                    break;
                case 2:
                    event.setCanceled(true);
                    cancelStage = -1;
                    break;
            }

            previousX = mc.player.posX;
            previousY = mc.player.posY;
            previousZ = mc.player.posZ;

            if (offsetX != 0 || offsetY != 0 || offsetZ != 0) {
                mc.player.posX += offsetX;
                mc.player.setEntityBoundingBox((mc.player.getEntityBoundingBox().offset(0, offsetY, 0)));
                mc.player.posZ += offsetZ;
            }
        }
    }



    public double get_n() {
        double max_y = -1;

        final AxisAlignedBB grow = mc.player.getEntityBoundingBox().offset(0, 0.05, 0).grow(0.05);

        if (!mc.world.getCollisionBoxes(mc.player, grow.offset(0, 2, 0)).isEmpty()) return 100;

        for (final AxisAlignedBB aabb : mc.world.getCollisionBoxes(mc.player, grow)) {

            if (aabb.maxY > max_y) {
                max_y = aabb.maxY;
            }

        }

        return max_y - mc.player.posY;

    }
}
