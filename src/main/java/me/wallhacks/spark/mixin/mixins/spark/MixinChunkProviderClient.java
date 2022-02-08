package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.ChunkLoadEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ ChunkProviderClient.class })
public class MixinChunkProviderClient {


    @Inject(method = "unloadChunk", at = @At("HEAD"))
    public void unloadChunk(int x, int z, final CallbackInfo callbackInfo) {
        ChunkLoadEvent.Unload e = new ChunkLoadEvent.Unload(Minecraft.getMinecraft().world.getChunkProvider().provideChunk(x, z));
        Spark.eventBus.post(e);
    }
}
