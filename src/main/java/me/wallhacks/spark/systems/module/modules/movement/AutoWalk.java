package me.wallhacks.spark.systems.module.modules.movement;

import me.wallhacks.spark.event.player.PlayerUpdateMoveStateEvent;
import me.wallhacks.spark.systems.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "AutoWalk", description = "Holds down w without holding it down")
public class AutoWalk extends Module {
    @SubscribeEvent
    public void onWalkEvent(PlayerUpdateMoveStateEvent event) {
        mc.player.movementInput.moveForward++;
    }
}
