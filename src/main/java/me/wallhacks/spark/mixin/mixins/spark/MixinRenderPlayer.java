package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.systems.module.modules.render.Chams;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.wallhacks.spark.systems.module.modules.render.NameTags;

@Mixin(value={RenderPlayer.class})
public class MixinRenderPlayer {
    @Inject(method = "renderEntityName", at = @At("HEAD"), cancellable = true)
    public void renderLivingLabel(AbstractClientPlayer entityIn, double x, double y, double z, String name, double distanceSq, CallbackInfo info) {
        if (NameTags.INSTANCE.isEnabled() || entityIn instanceof Chams.PopCham)
            info.cancel();
    }
}

