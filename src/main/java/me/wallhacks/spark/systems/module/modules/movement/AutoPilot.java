package me.wallhacks.spark.systems.module.modules.movement;

import java.util.ArrayList;

import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.event.player.PlayerUpdateMoveStateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


@Module.Registration(name = "AutoPilot", description = "Keeps elytraflying at the same altitude")
public class AutoPilot extends Module {
	BooleanSetting autoTakeoff = new BooleanSetting("AutoTakeoff", this, true);
    DoubleSetting Ylevel = new DoubleSetting("Ylevel", this, 121.3, 0, 255);
    double p = .08; // Proportional: determines how quickly the desired altitude is reached
    double i = .04; // Integral: helps correct a final offset
    double d = 4; // Differential: provides dampening to prevent overshoot
    double speedP = 10; // Proportional control for pitch angle

    volatile double Vspeed = 0;
    @SubscribeEvent
    public void onWalkEvent(PlayerUpdateMoveStateEvent event) {
        if (autoTakeoff.getValue()) {
            if(mc.player.onGround)
                mc.player.movementInput.jump = true;

            if (Vspeed < -.01)
                mc.player.movementInput.jump = true;
        }
    }

    ArrayList<Double> errorIntegralFifo = new ArrayList<Double>();
    int flyTicks = 0;
    float pitch = 0;
    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        Vspeed = Minecraft.getMinecraft().player.posY - Minecraft.getMinecraft().player.prevPosY;

        if (Minecraft.getMinecraft().player.isElytraFlying()) {
            if (flyTicks < 20) {
                // Prevents hitting the ground on takeoff (helps with new anticheat)
                if (mc.player.posY < Ylevel.getValue())
                    mc.player.motionY = .1;
            }
            flyTicks++;

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

            double desiredVspeed = 0;
            desiredVspeed = (Ylevel.getValue() - Minecraft.getMinecraft().player.posY)*(p/10) + Vspeed*(d/10) - errorIntegral*i;

            pitch += (Vspeed - desiredVspeed) * speedP;
            if (pitch > maxPitch) pitch = (float) maxPitch;
            if (pitch < minPitch) pitch = (float) minPitch;

            Minecraft.getMinecraft().player.rotationPitch = pitch;
        }
        else {
            pitch = 0;
            flyTicks = 0;
            errorIntegralFifo.clear();
        }
    }
}
