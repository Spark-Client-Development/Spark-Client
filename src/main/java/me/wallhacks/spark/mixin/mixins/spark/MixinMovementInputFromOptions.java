package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateMoveStateEvent;
import me.wallhacks.spark.event.player.SneakEvent;
import me.wallhacks.spark.util.MC;
import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MovementInputFromOptions.class)
public class MixinMovementInputFromOptions implements MC {

    @Inject(method = "updatePlayerMoveState", at = @At("RETURN"))
    public void updatePlayerMoveStateReturn(CallbackInfo ci) {



        if(mc.player.movementInput.sneak)
        {
            SneakEvent event = new SneakEvent();
            Spark.eventBus.post(event);
            if(event.isCanceled())
            {
                mc.player.movementInput.moveStrafe = (float)((double)mc.player.movementInput.moveStrafe / 0.3D);
                mc.player.movementInput.moveForward = (float)((double)mc.player.movementInput.moveForward / 0.3D);
                mc.player.movementInput.sneak = false;
            }
        }

        Spark.eventBus.post(new PlayerUpdateMoveStateEvent());
    }





}
