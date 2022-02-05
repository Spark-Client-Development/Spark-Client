package me.wallhacks.spark.mixin.mixins;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.render.SpawnParticleEvent;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ ParticleManager.class })
public class MixinParticleManager {



    @Inject(method = "addEffect", at = @At(value = "HEAD"),cancellable = true)
    public void addEffect(Particle effect,CallbackInfo info) {
        SpawnParticleEvent event = new SpawnParticleEvent(effect);

        Spark.eventBus.post(event);

        if(event.isCanceled())
            info.cancel();
    }
}
