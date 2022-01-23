package me.wallhacks.spark.mixin.mixins;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerIsPotionActiveEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={EntityLivingBase.class})
public abstract class MixinEntityLivingBase {


    @Inject(method = "isPotionActive", at = @At("HEAD"), cancellable = true)
    public void isPotionActive(Potion potionIn, final CallbackInfoReturnable<Boolean> info)
    {
        PlayerIsPotionActiveEvent event = new PlayerIsPotionActiveEvent(potionIn);
        Spark.eventBus.post(event);

        if (event.isCanceled())
            info.setReturnValue(false);
    }

}
