package me.wallhacks.spark.mixin.mixins.spark;

import com.google.common.base.Predicate;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.render.FovModifierEvent;
import me.wallhacks.spark.event.render.RenderHurtCameraEffectEvent;
import me.wallhacks.spark.manager.SystemManager;
import me.wallhacks.spark.systems.module.modules.mics.Putin;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import me.wallhacks.spark.systems.module.modules.player.NoEntityTrace;

import java.util.ArrayList;
import java.util.List;

@Mixin({ EntityRenderer.class })
public class MixinEntityRenderer {

    @Inject(method = "getFOVModifier", at = @At("HEAD"), cancellable = true)
    public void getFOVModifier(float partialTicks, boolean useFOVSetting, final CallbackInfoReturnable<Float> callbackInfo)
    {
        FovModifierEvent event = new FovModifierEvent(useFOVSetting);

        Spark.eventBus.post(event);

        if(event.isCanceled())
        {
            callbackInfo.setReturnValue(event.getFov());
            callbackInfo.cancel();
        }
    }

    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    public void hurtCameraEffect(float ticks, CallbackInfo info)
    {
        RenderHurtCameraEffectEvent event = new RenderHurtCameraEffectEvent();

        Spark.eventBus.post(event);

        if (event.isCanceled())
            info.cancel();
    }




    @Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<Entity> getEntitiesInAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate<? super Entity> predicate) {
        NoEntityTrace noEntityTrace = SystemManager.getModule(NoEntityTrace.class);
        if (noEntityTrace.isEnabled() && noEntityTrace.noTrace()){
            return new ArrayList<>();
        } else {
            return worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
        }
    }

    @Inject(method = "orientCamera", at = @At("HEAD"))
    public void orientCamera(float partialTicks,final CallbackInfo callbackInfo)
    {
        if(Putin.instance.isEnabled())
            GlStateManager.scale(2, 2 * 0.4, 2);


    }

}