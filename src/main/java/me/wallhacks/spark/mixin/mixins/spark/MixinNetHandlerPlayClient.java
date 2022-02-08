package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.ChunkLoadEvent;
import me.wallhacks.spark.util.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient implements MC {
    @Inject(method = "handleChunkData", at = @At("RETURN"))
    public void handleChunkData(SPacketChunkData packetIn, final CallbackInfo callbackInfo) {
        ChunkLoadEvent.Load e = new ChunkLoadEvent.Load(Minecraft.getMinecraft().world.getChunkProvider().provideChunk(packetIn.getChunkX(), packetIn.getChunkZ()));
        Spark.eventBus.post(e);
    }
}

