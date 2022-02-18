package me.wallhacks.spark.mixin.mixins.spark;

import io.netty.channel.ChannelHandlerContext;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.event.player.PacketSendEvent;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ NetworkManager.class })
public class MixinNetworkManager {

    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    public void sendPacket(Packet<?> packetIn, final CallbackInfo callbackInfo) {
        PacketSendEvent event = new PacketSendEvent(packetIn);

        Spark.eventBus.post(event);

        if(event.isCanceled())
        {
            callbackInfo.cancel();
        }
    }


    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    public void channelRead0(ChannelHandlerContext p_channelRead0_1_, Packet<?> p_channelRead0_2_, final CallbackInfo callbackInfo) {
            PacketReceiveEvent event = new PacketReceiveEvent(p_channelRead0_2_);

            Spark.eventBus.post(event);

            if(event.isCanceled()) {
                callbackInfo.cancel();
            }
    }
}