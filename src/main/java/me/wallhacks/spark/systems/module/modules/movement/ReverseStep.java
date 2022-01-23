package me.wallhacks.spark.systems.module.modules.movement;

import me.wallhacks.spark.event.player.PlayerLivingTickEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "ReverseStep", description = "Fast fall")
public class ReverseStep extends Module {



    @SubscribeEvent
    public void onUpdateEvent(PlayerLivingTickEvent event) {

        if (!MC.mc.player.onGround || MC.mc.player.isOnLadder() || MC.mc.player.isInWater() || MC.mc.player.isInLava() || MC.mc.player.movementInput.jump || MC.mc.player.noClip) return;
        if (MC.mc.player.moveForward == 0 && MC.mc.player.moveStrafing == 0) return;

        if(MC.mc.player.motionY <= 0)
            MC.mc.player.motionY = -1;
    }

}
