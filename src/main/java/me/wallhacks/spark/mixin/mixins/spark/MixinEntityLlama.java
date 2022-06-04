package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.entity.BoatControlEvent;
import me.wallhacks.spark.event.player.PlayerIsPotionActiveEvent;
import me.wallhacks.spark.systems.module.modules.exploit.EntityControl;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLlama.class)
public class MixinEntityLlama {
    @Inject(method = "canBeSteered", at = @At("HEAD"), cancellable = true)
    public void canBeSteered(final CallbackInfoReturnable<Boolean> info) {

        if (EntityControl.instance.isEnabled())
            info.setReturnValue(true);
    }


}
