package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.render.RenderLivingEvent;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={RenderEnderCrystal.class})
public class MixinRenderEnderCrystal {

    @Shadow
    public ModelBase modelEnderCrystal;

    @Shadow
    public ModelBase modelEnderCrystalNoBase;


    @Redirect(method = {"doRender"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void renderModelBaseHook(ModelBase model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        RenderLivingEvent event = new RenderLivingEvent(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, model);
        Spark.eventBus.post(event);
        if (!event.isCanceled()) {
            model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }
}
