package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.render.RenderEntityEvent;
import me.wallhacks.spark.event.render.SkyEvent;
import me.wallhacks.spark.systems.module.modules.exploit.Zelensky;
import me.wallhacks.spark.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
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


    @Inject(method = "Lnet/minecraft/client/renderer/RenderGlobal;renderSky(FI)V", at = @At(value="HEAD"), cancellable = true)
    public void renderSkyHead(float partialTicks, int pass,final CallbackInfo info) {

        if(Minecraft.getMinecraft().player == null)
            return;

        SkyEvent skyEvent = new SkyEvent(partialTicks);


        Spark.eventBus.post(skyEvent);
        if(skyEvent.isCanceled())
        {
            info.cancel();
        }


    }

}
