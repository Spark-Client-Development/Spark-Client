package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.util.MC;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ EntityPlayer.class })
public abstract class MixinEntityPlayer implements MC {


    @Shadow public abstract boolean isWearing(EnumPlayerModelParts part);

    @Inject(method = "onUpdate", at = @At("RETURN"))
    public void onUpdate(final CallbackInfo info) {
        if((Object)this instanceof EntityPlayerSP) {
            PlayerUpdateEvent event = new PlayerUpdateEvent();
            Spark.eventBus.post(event);
            Spark.switchManager.OnLateUpdate();
        }

    }

    @Inject(method = "isWearing", at = @At("RETURN"), cancellable = true)
    public void isWearing(EnumPlayerModelParts part, CallbackInfoReturnable<Boolean> cir) {
        if (mc.world == null)
            cir.setReturnValue(true);
    }
}