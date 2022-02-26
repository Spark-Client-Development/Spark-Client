package me.wallhacks.spark.manager;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PacketSendEvent;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PositionManager {
    public int teleportId;

    public PositionManager() {
        Spark.eventBus.register(this);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void packetSend(PacketSendEvent event) {
        if (event.getPacket() instanceof CPacketConfirmTeleport)
            teleportId = ((CPacketConfirmTeleport) event.getPacket()).getTeleportId();
    }
}
