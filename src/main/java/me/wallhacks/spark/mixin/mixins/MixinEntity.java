package me.wallhacks.spark.mixin.mixins;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import me.wallhacks.spark.systems.module.modules.movement.Velocity;

@Mixin(Entity.class)
public class MixinEntity {
    @Redirect(method={"applyEntityCollision"}, at=@At(value="INVOKE", target="Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    public void addVelocityHook(Entity entity, double x, double y, double z) {
        if (!Velocity.INSTANCE.isEnabled() || !Velocity.INSTANCE.NoPush.getValue()) {
            entity.motionX += x;
            entity.motionY += y;
            entity.motionZ += z;
        }
    }
}
