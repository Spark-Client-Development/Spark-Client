package me.wallhacks.spark.systems.module.modules.player;

import me.wallhacks.spark.systems.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

@Module.Registration(name = "YawLock", description = "Simple YawLock that works well with AutoPilot (snaps to 45 degree angles) (can be overpowered)")
public class YawLock extends Module {
    float snapAngle(float step, float angle) {
        while (angle < 0) angle += 360;
        float snappedAngle = angle + step/2;
        snappedAngle -= snappedAngle % step;
        if (snappedAngle == 360)
            snappedAngle = 0;
        return snappedAngle;
    }

    @SubscribeEvent
    public void clientTick(ClientTickEvent event) {
        float snappedYaw = snapAngle(45, mc.player.rotationYaw);
        mc.player.rotationYaw = (float) snappedYaw;
    }
}
