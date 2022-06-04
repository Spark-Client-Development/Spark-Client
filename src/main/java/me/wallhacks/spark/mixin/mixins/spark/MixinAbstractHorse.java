package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.systems.module.modules.exploit.EntityControl;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityLlama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public class MixinAbstractHorse {
    @Inject(method = "isHorseSaddled", at = @At("HEAD"), cancellable = true)
    public void isHorseSaddled(final CallbackInfoReturnable<Boolean> info) {

        if (EntityControl.instance.isEnabled() && Minecraft.getMinecraft().player != null && (Object)Minecraft.getMinecraft().player.ridingEntity == this)
            info.setReturnValue(true);
    }


}
