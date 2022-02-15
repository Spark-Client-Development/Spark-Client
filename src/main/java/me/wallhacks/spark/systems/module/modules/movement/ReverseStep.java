package me.wallhacks.spark.systems.module.modules.movement;

import me.wallhacks.spark.event.player.PlayerLivingTickEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "ReverseStep", description = "Fast fall")
public class ReverseStep extends Module {



    @SubscribeEvent
    public void onUpdateEvent(PlayerLivingTickEvent event) {

        if (!mc.player.onGround || mc.player.isOnLadder() || mc.player.isInWater() || mc.player.isInLava() || mc.player.movementInput.jump || mc.player.noClip) return;
        if (mc.player.moveForward == 0 && mc.player.moveStrafing == 0) return;

        if(mc.player.motionY <= 0)
            mc.player.motionY = -1;
    }

}
