package me.wallhacks.spark.mixin.mixins.spark;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public class MixinAbstractClientPlayer {
    @Inject(method = {"isSpectator"}, at={@At(value="HEAD")}, cancellable=true)
    public void isSpectator(CallbackInfoReturnable<Boolean> info) {
        if(Minecraft.getMinecraft().getConnection() == null)
            info.setReturnValue(false);
    }
}
