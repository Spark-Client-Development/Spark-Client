package me.wallhacks.spark.util.render;

import me.wallhacks.spark.manager.MapManager;
import me.wallhacks.spark.systems.clientsetting.clientsettings.HudSettings;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.maps.SparkMap;
import me.wallhacks.spark.util.objects.Vec2i;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapDecoration;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class MapRender implements MC {



    public static void RenderWholeMap(int ImageStartX,int ImageStartY,int ImageScaleX,int ImageScaleY,int ImageScale,double TargetX,double TargetZ, double offsetX,double offsetY){
        GL11.glPushMatrix();
        GuiUtil.glScissor(ImageStartX,ImageStartY,ImageScaleX,ImageScaleY);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        //background
        Gui.drawRect(ImageStartX, ImageStartY, ImageStartX+ImageScaleX, ImageStartY+ImageScaleY, HudSettings.getInstance().getGuiHudListBackgroundColor().getRGB());
        GlStateManager.color(1,1,1,1);


        float centerX = (float) (SparkMap.get2dMapPosFromWorldPos(TargetX,ImageScale) - ImageScaleX * 0.5 - (offsetX));
        float centerY = (float) (SparkMap.get2dMapPosFromWorldPos(TargetZ,ImageScale) - ImageScaleY * 0.5 - (offsetY));

        Vec2i WholeMapStartPos = SparkMap.getMapPosFrom2dMapPos(centerX, centerY, ImageScale);
        Vec2i WholeMapEndPos = SparkMap.getMapPosFrom2dMapPos(centerX+ImageScaleX, centerY+ImageScaleY, ImageScale);


        for (int x = WholeMapStartPos.x; x <= WholeMapEndPos.x; x++) {
            for (int y = WholeMapStartPos.y; y <= WholeMapEndPos.y; y++) {

                SparkMap map = MapManager.instance.getMap(new Vec2i(x, y));

                float x_ = map.getStartPos().x*(ImageScale/SparkMap.getWidthAndHeight())-centerX;
                float y_ = map.getStartPos().y*(ImageScale/SparkMap.getWidthAndHeight())-centerY;


                RenderMap(map,ImageStartX+x_,ImageStartY+y_,ImageScale);
            }
        }

        if(true){
            float OffsetXtoPlayer = (float) (ImageStartX+ImageScaleX * 0.5+offsetX+SparkMap.get2dMapPosFromWorldPos(mc.player.posX-TargetX,ImageScale));
            float OffsetYtoPlayer = (float) (ImageStartY+ImageScaleY * 0.5+offsetY+SparkMap.get2dMapPosFromWorldPos(mc.player.posZ-TargetZ,ImageScale));

            GuiUtil.drawCompleteImageRotated(OffsetXtoPlayer,OffsetYtoPlayer,4,4,(int)mc.player.rotationYaw+90,ARROW_ICON, Color.WHITE);
        }


        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        GL11.glPopMatrix();
    }

    private static final ResourceLocation ARROW_ICON = new ResourceLocation("textures/icons/arrowicon.png");

    public static void RenderMap(SparkMap m,float ImageStartX,float ImageStartY,int ImageScale){



        m.resourceLocation = mc.getTextureManager().getDynamicTextureLocation("temp", m.mapTexture);
        mc.getTextureManager().bindTexture(m.resourceLocation);

        GuiUtil.drawCompleteImage(ImageStartX, ImageStartY, ImageScale, ImageScale);
    }




}
