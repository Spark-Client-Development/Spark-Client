package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.manager.SystemManager;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.wallhacks.spark.systems.module.modules.render.NoRender;

@Mixin(value={LayerArmorBase.class})
public abstract class MixinLayerArmorBase {

    @Inject(method = "renderArmorLayer", at = @At(value = "HEAD"), cancellable = true)
    public void renderArmorLayer(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn, CallbackInfo info) {
        NoRender noRender = SystemManager.getModule(NoRender.class);

        if (noRender.isEnabled() && noRender.armor.isOn())
            info.cancel();


    }


}
