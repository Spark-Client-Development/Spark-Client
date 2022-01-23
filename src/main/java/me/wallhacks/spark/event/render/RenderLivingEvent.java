package me.wallhacks.spark.event.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class RenderLivingEvent extends Event {
    Entity entityLivingBase;
    float limbSwing;
    float limbSwingAmount;
    float ageInTicks;
    float netHeadYaw;
    float headPitch;
    float scaleFactor;
    ModelBase modelBase;

    public RenderLivingEvent(Entity entityLivingBase, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, ModelBase model) {
        this.entityLivingBase = entityLivingBase;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
        this.scaleFactor = scaleFactor;
        this.modelBase = model;
    }

    public float getAgeInTicks() {
        return ageInTicks;
    }

    public float getHeadPitch() {
        return headPitch;
    }

    public float getLimbSwing() {
        return limbSwing;
    }

    public Entity getEntity() {
        return entityLivingBase;
    }

    public float getLimbSwingAmount() {
        return limbSwingAmount;
    }

    public float getNetHeadYaw() {
        return netHeadYaw;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public ModelBase getModelBase() {
        return modelBase;
    }
}
