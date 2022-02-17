package me.wallhacks.spark.event.player;

import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class PacketSendEvent extends Event {

    public PacketSendEvent(Packet<?> p){
        this.p = p;
    }

    final Packet<?> p;
    
    @SuppressWarnings("unchecked")
	public <T extends Packet<?>> T getPacket() {
        return (T) p;
    }

    public static class Post extends Event {
        public Post(Packet<?> p){
            this.p = p;
        }

        final Packet<?> p;

        @SuppressWarnings("unchecked")
        public <T extends Packet<?>> T getPacket() {
            return (T) p;
        }
    }
}
