package me.wallhacks.spark.util.combat;

import me.wallhacks.spark.util.MC;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class PredictionUtil implements MC {

    public static AxisAlignedBB PredictedTarget(EntityLivingBase entity, int predict) {
        AxisAlignedBB[] bb = PredictedTargetBoxes(entity, predict);
        return bb[bb.length-1];
    }

    //gets list of predicted bounding boxes
    public static AxisAlignedBB[] PredictedTargetBoxes(EntityLivingBase entity, int predict) {


        AxisAlignedBB box = entity.boundingBox;

        if(predict <= 0)
            return new AxisAlignedBB[]{box};


        double motionX = entity.posX - entity.lastTickPosX;
        double motionY = entity.posY - entity.lastTickPosY;
        double motionZ = entity.posZ - entity.lastTickPosZ;

        //check if motion worth predicting
        double motion = Math.sqrt(Math.pow(motionX, 2) + Math.pow(motionZ, 2) + Math.pow(motionY, 2));
        if (motion < 0.1 && onGround(entity, box)) {
            return new AxisAlignedBB[]{box};

        }
        //if phasing we don't predict
        if(!mc.world.getCollisionBoxes(entity, box.shrink(0.06)).isEmpty())
            return new AxisAlignedBB[]{box};

        //do simple prediction if not player
        if(!(entity instanceof EntityPlayer))
        {
            return new AxisAlignedBB[]{box,new AxisAlignedBB(box.minX+motionX*predict,box.minY+motionY*predict,box.minZ+motionZ*predict,box.maxX+motionX*predict,box.maxY+motionY*predict,box.maxZ+motionZ*predict)};

        }

        AxisAlignedBB[] boxes = new AxisAlignedBB[predict+1];
        boxes[0] = box;



        //check if we should predict player strafing (jumping)
        boolean predictStrafe = (motion > 0.31);


        //predict target moving over
        for (int i = 0; i < predict; i++) {

            //if player in not on ground we need to add gravity(falling) else we can add jumping when player is strafing
            if (onGround(entity, box)) {
                //jump
                motionY = predictStrafe ? 0.4 : 0;
            }else {
                //gravity
                motionY -= 0.08;

            }
            //drag added
            motionX *= 0.9800000190734863D;
            motionY *= 0.9800000190734863D;
            motionZ *= 0.9800000190734863D;

            //handle col - try move collision
            box = moveHitbox(motionX,motionY,motionZ,entity,box);



            boxes[i+1] = box;
        }
        return boxes;

    }
    //is entitx on ground with bb
    public static boolean onGround(Entity entity , AxisAlignedBB bb){
        return !mc.world.getCollisionBoxes(entity, bb.expand(0,-0.06,0)).isEmpty();
    }

    public static AxisAlignedBB moveHitbox(double x, double y, double z,Entity entity ,AxisAlignedBB bb) {
        List<AxisAlignedBB> list1 = mc.world.getCollisionBoxes(entity, bb.expand(x, y, z));

        if (y != 0.0D) {
            int k = 0;
            for (int l = list1.size(); k < l; ++k) {
                y = (list1.get(k)).calculateXOffset(bb, y);
            }
            if (y != 0.0D) {
                bb = (bb.offset(0.0D, y, 0.0D));
            }
        }

        if (x != 0.0D) {
            int k = 0;
            for (int l = list1.size(); k < l; ++k) {
                x = (list1.get(k)).calculateYOffset(bb, y);
            }
            if (x != 0.0D) {
                bb = (bb.offset(x, 0.0D, 0.0D));
            }
        }

        if (z != 0.0D) {
            int k = 0;
            for (int l = list1.size(); k < l; ++k) {
                z = (list1.get(k)).calculateZOffset(bb, y);
            }
            if (z != 0.0D) {
                bb = (bb.offset(0.0D, 0.0D, z));
            }
        }
        return bb;
    }


}
