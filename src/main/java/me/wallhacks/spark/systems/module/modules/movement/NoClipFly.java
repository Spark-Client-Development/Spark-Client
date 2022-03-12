package me.wallhacks.spark.systems.module.modules.movement;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.event.player.PlayerMoveEvent;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.event.player.PlayerUpdateMoveStateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.util.objects.Timer;
import me.wallhacks.spark.util.player.RotationUtil;
import me.wallhacks.spark.util.render.EspUtil;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

@Module.Registration(name = "NoClipFly", description = "wallhacks_ coded this all by himself")
public class NoClipFly extends Module {

    DoubleSetting flyspeed = new DoubleSetting("FlySpeed",this,1,0.2,10);
    DoubleSetting Upspeed = new DoubleSetting("UPSpeed",this,0.1,0.01,1);
    ColorSetting colr = new ColorSetting("Shitter",this,new Color(27, 190, 103, 207));
    IntSetting packets = new IntSetting("PAckets",this,15,2,50);


    Timer updateTimer = new Timer();

    @SubscribeEvent
    public void onUpdateMove(PlayerMoveEvent event) {

        mc.player.noClip = true;
    }

    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        mc.player.capabilities.isFlying = true;

        if(updateTimer.passedMs(2550)) {
            updateTimer.reset();
            return;
        }
        if(updateTimer.passedMs(2500)) {
            return;
        }
        double[] dir = RotationUtil.directionSpeed(flyspeed.getValue());
        mc.player.capabilities.setFlySpeed(Upspeed.getValue().floatValue());
        mc.player.motionZ = dir[1];
        mc.player.motionX = dir[0];
        for(int i = 0; i < packets.getValue(); i++) {
            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY, mc.player.posZ,mc.player.rotationYaw,mc.player.rotationPitch, false));
            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.prevPosX, mc.player.prevPosY + 0.05, mc.player.prevPosZ,mc.player.rotationYaw,mc.player.rotationPitch, true));
            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY, mc.player.posZ,mc.player.rotationYaw,mc.player.rotationPitch, false));
        }
    }

    public void onDisable() {
        mc.player.capabilities.isFlying = false;
        mc.player.noClip = false;
    }
    double realx;
    double realy;
    double realz;

    @SubscribeEvent
    public void getPack(PacketReceiveEvent event) {
        if(event.getPacket() instanceof SPacketPlayerPosLook) {
            if(updateTimer.passedMs(2500)) {
                return;
            }
            realx = ((SPacketPlayerPosLook) event.getPacket()).x;
            realz =((SPacketPlayerPosLook) event.getPacket()).z;
            realy = ((SPacketPlayerPosLook) event.getPacket()).y;
            if(true) {
                ((SPacketPlayerPosLook) event.getPacket()).y = mc.player.posY;
                ((SPacketPlayerPosLook) event.getPacket()).x = mc.player.posX;
                ((SPacketPlayerPosLook) event.getPacket()).z = mc.player.posZ;
                ((SPacketPlayerPosLook) event.getPacket()).yaw = mc.player.rotationYaw;
                ((SPacketPlayerPosLook) event.getPacket()).pitch = mc.player.rotationPitch;
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.prevPosX, mc.player.prevPosY + 0.05, mc.player.prevPosZ, true));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if(!nullCheck()) {
            if(realx != 0) {

                double sx = mc.player.boundingBox.maxX-mc.player.boundingBox.minX;
                double sy = mc.player.boundingBox.maxY-mc.player.boundingBox.minY;
                double sz = mc.player.boundingBox.maxZ-mc.player.boundingBox.minZ;

                EspUtil.boundingESPBox(new AxisAlignedBB(realx-sx/2,realy,realz-sz/2,realx+sx/2,realy+sy,realz+sz/2), colr.getColor().brighter(), 2.0f);
            }
        }
    }


}
