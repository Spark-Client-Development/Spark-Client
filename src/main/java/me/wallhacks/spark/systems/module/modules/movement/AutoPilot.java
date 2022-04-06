package me.wallhacks.spark.systems.module.modules.player;

import net.minecraft.network.play.client.CPacketPlayer.PositionRotation;

import java.util.ArrayList;

import javax.vecmath.Vector2d;

import org.lwjgl.BufferUtils;

import com.sun.tools.javac.util.List;

import akka.japi.Pair;
import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.event.player.PacketSendEvent;
import me.wallhacks.spark.event.player.PlayerUpdateMoveStateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;


@Module.Registration(name = "AutoPilot", description = "Cancels server rotations")
public class AutoPilot extends Module {
    DoubleSetting Ylevel = new DoubleSetting("Ylevel", this, 121.4, 0, 255);
    DoubleSetting speedp = new DoubleSetting("speedp", this, 10, 0, 100);
    DoubleSetting p = new DoubleSetting("p", this, .08, -1, 1);
    DoubleSetting i = new DoubleSetting("i", this, .04, -1, 1);
    DoubleSetting d = new DoubleSetting("d", this, 4, -1, 1);
	BooleanSetting autostart = new BooleanSetting("AutoStart", this, false);
    DoubleSetting jumpTime = new DoubleSetting("JumpTime", this, -.01, -1, 0);
    IntSetting initialTime = new IntSetting("initialTime", this, 0, 0, 100);
    DoubleSetting upSpeed = new DoubleSetting("upSpeed", this, 1, 0, 10);

    ArrayList<Double> errorIntegralFifo = new ArrayList<Double>();


    float pitch = 0;
    double prevVspeed = 0;
    
    float snapAngle(float step, float angle) {
        while (angle < 0) angle += 360;
        float snappedAngle = angle + step/2;
        snappedAngle -= snappedAngle % step;
        if (snappedAngle == 360)
            snappedAngle = 0;
        return snappedAngle;
    }
    

    int tick = 0;
    boolean gofly = false;
    volatile double Vspeed;

    @SubscribeEvent
    public void tick(ClientTickEvent event) {
        try {
            Vspeed = Minecraft.getMinecraft().player.posY - Minecraft.getMinecraft().player.prevPosY;
        } catch (Exception e) {
        }
    }

    @SubscribeEvent
    public void onWalkEvent(PlayerUpdateMoveStateEvent event) {
        tick++;
        if (tick % 20 == 0)
            mc.player.movementInput.jump = true;

        System.out.println(String.valueOf(Vspeed));
        System.out.println("test");
        if (Vspeed < jumpTime.getValue())
            mc.player.movementInput.jump = true;
    }

    int flyTicks = 0;
    @SubscribeEvent
    public void clientTick(ClientTickEvent event) {
        try {

            if (Minecraft.getMinecraft().player.isElytraFlying()) {
                if (flyTicks < initialTime.getValue()) {
                    if (mc.player.posY < Ylevel.getValue())
                        mc.player.motionY = upSpeed.getValue();
                }
                flyTicks++;
                double desiredVspeed = 0;
                double maxPitch = 45;
                double minPitch = -45;
                double error = Minecraft.getMinecraft().player.posY - Ylevel.getValue();

                int integralSize = 10;
                errorIntegralFifo.add(error);
                if (errorIntegralFifo.size() > integralSize) {
                    errorIntegralFifo.remove(0);
                }

                double errorIntegral = 0;
                for (int l = 0; l < errorIntegralFifo.size(); l++) {
                    errorIntegral += errorIntegralFifo.get(l);
                }
                errorIntegral /= errorIntegralFifo.size();
                

                desiredVspeed = (Ylevel.getValue() - Minecraft.getMinecraft().player.posY)*(p.getValue()/10) + Vspeed*(d.getValue()/10) - errorIntegral*i.getValue();


                pitch += (Vspeed - desiredVspeed) * speedp.getValue();
                if (pitch > maxPitch) pitch = (float) maxPitch;
                if (pitch < minPitch) pitch = (float) minPitch;



                Minecraft.getMinecraft().player.rotationPitch = pitch;

                //YawLock
                float snappedYaw = snapAngle(45, Minecraft.getMinecraft().player.rotationYaw);
                Minecraft.getMinecraft().player.rotationYaw = (float) snappedYaw;
            }
            else {
                pitch = 0;
                flyTicks = 0;
                errorIntegralFifo.clear();
            }
        } catch (Exception e) {
        }

    }
    
}
