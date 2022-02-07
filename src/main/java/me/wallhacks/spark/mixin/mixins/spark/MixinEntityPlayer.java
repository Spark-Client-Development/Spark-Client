package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ EntityPlayer.class })
public class MixinEntityPlayer {



    @Inject(method = "onUpdate", at = @At("RETURN"))
    public void onUpdate(final CallbackInfo info) {
        if((Object)this instanceof EntityPlayerSP) {
            PlayerUpdateEvent event = new PlayerUpdateEvent();
            Spark.eventBus.post(event);
        }
    }
}