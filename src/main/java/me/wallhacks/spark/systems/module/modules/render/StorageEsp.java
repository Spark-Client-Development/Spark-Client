package me.wallhacks.spark.systems.module.modules.render;

import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.render.EspUtil;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;

import java.awt.*;

@Module.Registration(name = "StorageEsp", description = "Render esp for entities")
public class StorageEsp extends Module {

    ColorSetting ChestColor = new ColorSetting("Chest", this, new Color(40,40,220));
    ColorSetting EnderChestColor = new ColorSetting("EnderChest", this, new Color(66,30,220));
    ColorSetting UtilsColor = new ColorSetting("Utils", this, new Color(40,40,40));
    ColorSetting ShulkerColor = new ColorSetting("Shulker", this, new Color(160,60,130));


    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        GL11.glPushMatrix();

        for(TileEntity o : mc.world.loadedTileEntityList){

            float Alpha = (float) Math.max(0.2f,Math.min(0.6, 0.02f* mc.player.getDistance(o.getPos().getX(),o.getPos().getY(),o.getPos().getZ())));




            if(o instanceof TileEntityChest){
                RenderBlock(o, ChestColor.getColor(), Alpha);
            }
            else if(o instanceof TileEntityEnderChest){
                RenderBlock(o, EnderChestColor.getColor(), Alpha);
            }
            else if(o instanceof TileEntityFurnace){
                RenderBlock(o, UtilsColor.getColor(), Alpha);
            }
            else if(o instanceof TileEntityShulkerBox){
                RenderBlock(o, ShulkerColor.getColor(), Alpha);
            }
            else if(o instanceof TileEntityHopper){
                RenderBlock(o, UtilsColor.getColor(), Alpha);
            }
            else if(o instanceof TileEntityDispenser){
                RenderBlock(o, UtilsColor.getColor(), Alpha);
            }
            else if(o instanceof TileEntityDropper){
                RenderBlock(o, UtilsColor.getColor(), Alpha);
            }


        }
        GL11.glPopMatrix();
    }

    void RenderBlock(TileEntity p,Color c, float a)
    {
        try{


            AxisAlignedBB B = mc.world.getBlockState(p.getPos()).getSelectedBoundingBox(mc.world, p.getPos());

            if(p instanceof TileEntityChest){
                TileEntityChest t = (TileEntityChest)p;


                    if(t.adjacentChestXNeg == null && t.adjacentChestZNeg == null){
                        if(t.adjacentChestZPos != null || t.adjacentChestXPos != null)
                            return;
                    }
                    else{
                        if(t.adjacentChestZNeg != null)
                            B = new AxisAlignedBB(B.minX,B.minY,B.minZ-(B.maxZ-B.minZ),B.maxX,B.maxY,B.maxZ);
                        else
                            B = new AxisAlignedBB(B.minX-(B.maxX-B.minX),B.minY,B.minZ,B.maxX,B.maxY,B.maxZ);
                    }

            }

            EspUtil.boundingESPBox(B,new Color(c.getRed(),c.getGreen(),c.getBlue(),(int)((120+c.getAlpha()/2f)*a)), 2.0f);
            a *= 0.8f;
            EspUtil.boundingESPBoxFilled(B,new Color(c.getRed(),c.getGreen(),c.getBlue(),(int)((120+c.getAlpha()/2f)*a)));

        }
        catch(Exception e){

        }
    }

}
