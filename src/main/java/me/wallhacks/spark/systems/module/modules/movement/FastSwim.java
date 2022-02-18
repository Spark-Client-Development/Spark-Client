package me.wallhacks.spark.systems.module.modules.movement;

import me.wallhacks.spark.event.player.PlayerMoveEvent;
import me.wallhacks.spark.event.player.SneakEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "FastSwim", description = "Swim faster")
public class FastSwim extends Module {
    public static FastSwim INSTANCE;
    public DoubleSetting waterSpeed = new DoubleSetting("WaterSpeed", this, 1.35, 1, 3);
    public DoubleSetting lavaSpeed = new DoubleSetting("LavaSpeed", this, 2, 1, 3);

    public FastSwim() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onMove(PlayerMoveEvent event) {
        if ((mc.player.isInWater() || mc.player.isInWater()) && !mc.player.onGround) {
            boolean jump = mc.player.movementInput.jump;
            boolean sneak = mc.player.movementInput.sneak;
            mc.player.setSprinting(true);
            if (jump && !sneak) {
                event.setY(0.11);
            } else if (!jump && sneak) {
                mc.player.setSneaking(false);
                event.setY(-0.11);
            } else {
                if (mc.player.ticksExisted % 2 == 0) event.setY(-0.005);
                else event.setY(0.005);
            }
            if (mc.player.isInWater()) {
                event.setX(event.getX() * waterSpeed.getFloatValue());
                event.setZ(event.getZ() * waterSpeed.getFloatValue());
            } else {
                event.setX(event.getX() * lavaSpeed.getFloatValue());
                event.setZ(event.getZ() * lavaSpeed.getFloatValue());
            }
        }
    }

    @SubscribeEvent
    public void onSneak(SneakEvent event) {
        if ((mc.player.isInWater() || mc.player.isInWater()) && !mc.player.onGround) event.setCanceled(true);
    }
}
