package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.manager.CapeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public class MixinAbstractClientPlayer {
    @Shadow public NetworkPlayerInfo playerInfo;

    @Inject(method = {"isSpectator"}, at={@At(value="HEAD")}, cancellable=true)
    public void isSpectator(CallbackInfoReturnable<Boolean> info) {
        if(Minecraft.getMinecraft().getConnection() == null)
            info.setReturnValue(false);
    }

    @Inject(method = "getLocationCape", at = @At("RETURN"), cancellable = true)
    public void getCape(CallbackInfoReturnable<ResourceLocation> info) {
        ResourceLocation r = Spark.capeManager.getCapeForUser(this.playerInfo.getGameProfile().getId().toString().replaceAll("-", ""));
        if (r != null) {
            info.cancel();
            info.setReturnValue(r);
        }
    }
}
