package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.render.RenderEntityEvent;
import me.wallhacks.spark.util.render.RenderUtil;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderGlobal.class})
public abstract class MixinRenderGlobal {

    @Inject(method = "renderEntities", at = @At("HEAD"))
    public void renderEntitiesHead(CallbackInfo ci) {
        RenderUtil.isRenderLoop = true;
    }

    @Inject(method = "renderEntities", at = @At("RETURN"))
    public void renderEntitiesReturn(CallbackInfo ci) {
        RenderUtil.isRenderLoop = false;
    }

}
