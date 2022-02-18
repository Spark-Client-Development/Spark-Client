package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateMoveStateEvent;
import me.wallhacks.spark.event.player.SneakEvent;
import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MovementInputFromOptions.class)
public class MixinMovementInputFromOptions {

    @Inject(method = "updatePlayerMoveState", at = @At("RETURN"))
    public void updatePlayerMoveStateReturn(CallbackInfo ci) {
        Spark.eventBus.post(new PlayerUpdateMoveStateEvent());
    }

    @Redirect(method = "updatePlayerMoveState", at = @At(value = "FIELD", target = "Lnet/minecraft/util/MovementInputFromOptions;sneak:Z", opcode = Opcodes.GETFIELD))
    public boolean sneak(MovementInputFromOptions instance) {
        if (instance.sneak) {
            SneakEvent event = new SneakEvent();
            Spark.eventBus.post(event);
            return !event.isCanceled();
        } return false;
    }
}
