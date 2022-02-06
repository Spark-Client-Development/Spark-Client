package me.wallhacks.spark.systems.module.modules.player;

import me.wallhacks.spark.event.player.PacketSendEvent;
import me.wallhacks.spark.systems.module.Module;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "AntiHunger", description = "Prevent hunger loss by cancelling sprint packets")
public class AntiHunger extends Module {

    @SubscribeEvent
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacket() instanceof CPacketEntityAction) {
           if (((CPacketEntityAction) event.getPacket()).getAction() == CPacketEntityAction.Action.START_SPRINTING) {
                event.setCanceled(true);
            }
        }
    }
}
