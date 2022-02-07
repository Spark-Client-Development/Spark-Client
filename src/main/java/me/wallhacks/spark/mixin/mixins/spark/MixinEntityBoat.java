package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.entity.BoatControlEvent;
import net.minecraft.entity.item.EntityBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityBoat.class)
public class MixinEntityBoat {
    @Inject(method = {"controlBoat"}, at = {@At("HEAD")}, cancellable = true)
    public void controlBoat(final CallbackInfo info) {
        final BoatControlEvent event = new BoatControlEvent();
        Spark.eventBus.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }
}
