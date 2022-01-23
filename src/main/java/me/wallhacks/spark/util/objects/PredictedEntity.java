package me.wallhacks.spark.util.objects;

import me.wallhacks.spark.util.combat.PredictionUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;

public class PredictedEntity {

    public PredictedEntity(EntityLivingBase in, int prediction){
        entity = in;

        predictedBB = in instanceof EntityPlayer ? PredictionUtil.PredictedTarget(entity,prediction) : in.boundingBox;
    }
    public final EntityLivingBase entity;

    public final AxisAlignedBB predictedBB;

}
