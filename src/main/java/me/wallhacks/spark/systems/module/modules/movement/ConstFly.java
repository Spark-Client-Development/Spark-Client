package me.wallhacks.spark.systems.module.modules.movement;

import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "ConstFly", description = "Fly horizontally on constantiam")
public class ConstFly extends Module {
    int c;
    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        mc.player.onGround = true;
        mc.player.motionY = 0;
        if (c > 40) {
            mc.player.posY -= 0.032;
            c = 0;
        } else c ++;
        if (mc.player.ticksExisted % 3 != 0)
            mc.player.setPosition(mc.player.posX, mc.player.posY += 1.0e-9, mc.player.posZ);
    }
    @Override
    public void onEnable() {
        c = 0;
    }
}
