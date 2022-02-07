package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.render.RenderEntityEvent;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={RenderManager.class})
public abstract class MixinRenderManager {

    @Inject(method = "renderEntity", at = @At("HEAD"), cancellable = true)
    public void renderEntityHead(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean p_188391_10_, CallbackInfo callbackInfo) {
        RenderEntityEvent.Pre renderEntityHeadEvent = new RenderEntityEvent.Pre(entityIn);

        Spark.eventBus.post(renderEntityHeadEvent);



        if (renderEntityHeadEvent.isCanceled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "renderEntity", at = @At("RETURN"), cancellable = true)
    public void renderEntityReturn(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean p_188391_10_, CallbackInfo callbackInfo) {
        RenderEntityEvent.Post renderEntityReturnEvent = new RenderEntityEvent.Post(entityIn);

        Spark.eventBus.post(renderEntityReturnEvent);




        if (renderEntityReturnEvent.isCanceled()) {
            callbackInfo.cancel();
        }
    }

}
