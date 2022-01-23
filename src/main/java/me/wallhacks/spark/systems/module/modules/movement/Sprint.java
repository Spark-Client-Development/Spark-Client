package me.wallhacks.spark.systems.module.modules.movement;

import me.wallhacks.spark.event.player.PlayerLivingTickEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "Sprint", description = "Sprint when we can")
public class Sprint extends Module {


    @SubscribeEvent
    public void onUpdate(PlayerLivingTickEvent event){

        if(MC.mc.player.movementInput.moveForward >= 0.8F)
            MC.mc.player.setSprinting(true);

    }

}
