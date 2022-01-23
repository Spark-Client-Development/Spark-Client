package me.wallhacks.spark.systems.module.modules.render;

import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.render.EspUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;

import java.awt.*;
import java.util.ArrayList;

@Module.Registration(name = "Trajectories", description = "Render esp for entities")
public class Trajectories extends Module {

    BooleanSetting RenderForAll = new BooleanSetting("RenderForAll", this, false);
    ColorSetting LineColor = new ColorSetting("LineColor",this,new Color(40,190,40,220));


    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        ArrayList<Vec3d> path = new ArrayList<>();

        if(RenderForAll.isOn())
            for(Object o : MC.mc.world.loadedEntityList.toArray()){
                Entity entity = (Entity)o;
                if(entity instanceof EntityPlayer)
                    renderPath(MC.mc.getRenderPartialTicks(),(EntityPlayer)entity);
            }
        else
            renderPath(MC.mc.getRenderPartialTicks(), MC.mc.player);
    }

    private void renderPath(float partialTicks, EntityPlayer player) {

        ArrayList<net.minecraft.util.math.Vec3d> path = new ArrayList<>();

        ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
        Item item = stack.getItem();

        // check if item is throwable
        if(stack.isEmpty() || !isThrowable(item))
        {
            stack = player.getHeldItem(EnumHand.OFF_HAND);
            item = stack.getItem();
            if(stack.isEmpty() || !isThrowable(item))
                return;
        }

        double yaw = Math.toRadians(player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks);
        double pitch = Math.toRadians(player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks);


        // calculate starting position
        double arrowPosX = player.lastTickPosX
                + (player.posX - player.lastTickPosX) * partialTicks
                - Math.cos(yaw) * 0.16;

        double arrowPosY = player.lastTickPosY
                + (player.posY - player.lastTickPosY) * partialTicks
                + player.getEyeHeight() - 0.1;

        double arrowPosZ = player.lastTickPosZ
                + (player.posZ - player.lastTickPosZ) * partialTicks
                - Math.sin(yaw) * 0.16;

        // Motion factor. Arrows go faster than snowballs and all that...
        double arrowMotionFactor = item == Items.BOW ? 1.0 : 0.4;


        // calculate starting motion
        double arrowMotionX =
                -Math.sin(yaw) * Math.cos(pitch) * arrowMotionFactor;
        double arrowMotionY = -Math.sin(pitch) * arrowMotionFactor;
        double arrowMotionZ =
                Math.cos(yaw) * Math.cos(pitch) * arrowMotionFactor;

        // 3D Pythagorean theorem. Returns the length of the arrowMotion vector.
        double arrowMotion = Math.sqrt(arrowMotionX * arrowMotionX
                + arrowMotionY * arrowMotionY + arrowMotionZ * arrowMotionZ);

        arrowMotionX /= arrowMotion;
        arrowMotionY /= arrowMotion;
        arrowMotionZ /= arrowMotion;


        // apply bow charge
        if(item == Items.BOW)
        {
            float bowPower = (72000 - player.getItemInUseCount()) / 20.0f;
            bowPower = (bowPower * bowPower + bowPower * 2.0f) / 3.0f;

            if(bowPower > 1 || bowPower <= 0.1F)
                bowPower = 1;

            bowPower *= 3F;
            arrowMotionX *= bowPower;
            arrowMotionY *= bowPower;
            arrowMotionZ *= bowPower;

        }else
        {
            arrowMotionX *= 1.5;
            arrowMotionY *= 1.5;
            arrowMotionZ *= 1.5;
        }

        double gravity = getProjectileGravity(item);
        Vec3d eyesPos = getEyesPos(player);

        loop:
        for(int i = 0; i < 200; i++)
        {
            // add to path
            Vec3d arrowPos = new Vec3d(arrowPosX, arrowPosY, arrowPosZ);


            // apply motion
            arrowPosX += arrowMotionX;
            arrowPosY += arrowMotionY;
            arrowPosZ += arrowMotionZ;


            // apply air friction
            arrowMotionX *= 0.99004444;
            arrowMotionY *= 0.99004444;
            arrowMotionZ *= 0.99004444;

            // apply gravity
            arrowMotionY -= gravity;

            // check for collision

            RayTraceResult res = MC.mc.world.rayTraceBlocks(eyesPos, arrowPos, false, true, false);
            if(res != null)
            {

                TrajectoriesTarget(res.hitVec, res.sideHit, LineColor.getColor());
                path.add(res.hitVec);


                break;

            }

            path.add(arrowPos);
            eyesPos = arrowPos;
        }



        EspUtil.renderLineList(path, LineColor.getColor());

    }

    private double getProjectileGravity(Item item)
    {
        if(item == Items.BOW)
            return 0.05;

        if(item == Items.SPLASH_POTION || item == Items.LINGERING_POTION)
            return 0.4;


        if(item == Items.FISHING_ROD)
            return 0.2;


        return 0.03;
    }

    private boolean isThrowable(Item item)
    {
        return item == Items.BOW
                || item == Items.SNOWBALL || item == Items.EGG
                || item == Items.ENDER_PEARL
                || item == Items.SPLASH_POTION
                || item == Items.LINGERING_POTION
                || item == Items.FISHING_ROD;
    }

    public void TrajectoriesTarget(net.minecraft.util.math.Vec3d pos, EnumFacing facing, Color c)
    {

        double x = pos.x;
        double y = pos.y;
        double z = pos.z;

        double xDir = (facing.getXOffset() == 0 ? 0.3 : 0);
        double yDir = (facing.getYOffset() == 0 ? 0.3 : 0);
        double zDir = (facing.getZOffset() == 0 ? 0.3 : 0);



        EspUtil.boundingESPBoxFilled(new AxisAlignedBB(x-xDir,y-yDir,z-zDir,x+xDir,y+yDir,z+zDir),c);




    }
    Vec3d getEyesPos(EntityPlayer player)
    {


        return new Vec3d(player.posX,
                player.posY + player.getEyeHeight(),
                player.posZ);
    }


}
