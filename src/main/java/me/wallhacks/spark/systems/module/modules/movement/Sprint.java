package me.wallhacks.spark.systems.module.modules.movement;

import me.wallhacks.spark.event.player.PlayerLivingTickEvent;
import me.wallhacks.spark.event.player.PlayerMoveEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.module.modules.player.Freecam;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.player.PlayerUtil;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;

@Module.Registration(name = "Sprint", description = "Sprint when we can")
public class Sprint extends Module {
    ModeSetting mode = new ModeSetting("Sprint", this, "Vanilla", Arrays.asList("Instant", "Vanilla"));

    @SubscribeEvent
    public void onUpdate(PlayerLivingTickEvent event) {
        if(mc.player.movementInput.moveForward >= 0.8F && mode.is("Vanilla"))
            mc.player.setSprinting(true);
    }

    //set priority to high so speed will override sprint module
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onMove(PlayerMoveEvent event) {
        if (event.isCanceled()) return;
        if (Freecam.INSTANCE.isEnabled()) return;
        if (mode.is("Instant") && !mc.player.isInLava() && !mc.player.isInWater()) {
            float forward = mc.player.movementInput.moveForward;
            float strafe = mc.player.movementInput.moveStrafe;
            double speed = getBaseMotionSpeed();
            if (forward == 0 && strafe == 0) {
                event.setX(0D);
                event.setZ(0D);
            } else if (forward != 0.0D && strafe != 0.0D) {
                forward *= Math.sin(0.7853981633974483D);
                strafe *= Math.cos(0.7853981633974483D);
            }
            event.setCanceled(true);
            float yaw = mc.player.rotationYaw; // BaritoneAPI.getProvider().getPrimaryBaritone().getLookBehavior().getYaw();
            event.setX(forward * speed * -Math.sin(Math.toRadians(yaw)) + strafe * speed * Math.cos(Math.toRadians(yaw)));
            event.setZ(forward * speed * Math.cos(Math.toRadians(yaw)) - strafe * speed * -Math.sin(Math.toRadians(yaw)));
        }
    }


    private double getBaseMotionSpeed() {
        double baseSpeed = 0.2873D;
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            int amplifier = mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier();
            baseSpeed *= 1.0D + 0.2D * ((double) amplifier + 1);
        }
        return baseSpeed;
    }

}
