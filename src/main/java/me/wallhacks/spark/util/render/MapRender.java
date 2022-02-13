package me.wallhacks.spark.util.render;

import com.github.lunatrius.core.util.vector.Vector2d;
import com.sun.javafx.geom.Vec2d;
import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.manager.MapManager;
import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;
import me.wallhacks.spark.systems.clientsetting.clientsettings.HudSettings;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.maps.SparkMap;
import me.wallhacks.spark.util.objects.Vec2i;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Vector3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapDecoration;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class MapRender implements MC {



    public static void RenderWholeMap(int ImageStartX,int ImageStartY,int ImageScaleX,int ImageScaleY,int ImageScale,double TargetX,double TargetZ, double offsetX,double offsetY,int dim){
        GL11.glPushMatrix();
        GuiUtil.glScissor(ImageStartX,ImageStartY,ImageScaleX,ImageScaleY);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);


        //background
        Gui.drawRect(ImageStartX, ImageStartY, ImageStartX+ImageScaleX, ImageStartY+ImageScaleY, new Color(68, 68, 68, 165).getRGB());
        GlStateManager.color(1,1,1,1);


        float centerX = (float) (SparkMap.get2dMapPosFromWorldPos(TargetX,ImageScale) - ImageScaleX * 0.5 - (offsetX));
        float centerY = (float) (SparkMap.get2dMapPosFromWorldPos(TargetZ,ImageScale) - ImageScaleY * 0.5 - (offsetY));

        Vec2i WholeMapStartPos = SparkMap.getMapPosFrom2dMapPos(centerX, centerY, ImageScale);
        Vec2i WholeMapEndPos = SparkMap.getMapPosFrom2dMapPos(centerX+ImageScaleX, centerY+ImageScaleY, ImageScale);


        for (int x = WholeMapStartPos.x; x <= WholeMapEndPos.x; x++) {
            for (int y = WholeMapStartPos.y; y <= WholeMapEndPos.y; y++) {



                SparkMap map = MapManager.instance.getMap(new Vec2i(x, y),dim);

                float x_ = map.getStartPos().x*(ImageScale/SparkMap.getWidthAndHeight())-centerX;
                float y_ = map.getStartPos().y*(ImageScale/SparkMap.getWidthAndHeight())-centerY;


                RenderMap(map,ImageStartX+x_,ImageStartY+y_,ImageScale);
            }
        }

        if(dim == mc.player.dimension || (dim != 1 && mc.player.dimension != 1)){

            Vector2d pos = ConvertPos(new Vector2d(mc.player.posX,mc.player.posZ),mc.player.dimension,dim);
            float OffsetXtoPlayer = (float) (ImageStartX+ImageScaleX * 0.5+offsetX+SparkMap.get2dMapPosFromWorldPos(pos.x-TargetX,ImageScale));
            float OffsetYtoPlayer = (float) (ImageStartY+ImageScaleY * 0.5+offsetY+SparkMap.get2dMapPosFromWorldPos(pos.y-TargetZ,ImageScale));

            GuiUtil.drawCompleteImageRotated(OffsetXtoPlayer-2,OffsetYtoPlayer-2,4,4,(int)mc.player.rotationYaw+90,ARROW_ICON, Color.WHITE);
        }

        if(mc.player.dimension == dim)
        {
            for (Entity e : mc.world.loadedEntityList) {
                if(e instanceof EntityPlayer)
                {
                    EntityPlayer player = (EntityPlayer) e;

                    if(player != mc.player)
                    {
                        double x = ImageStartX+ImageScaleX * 0.5+offsetX+SparkMap.get2dMapPosFromWorldPos(player.posX-TargetX,ImageScale);
                        double y = ImageStartY+ImageScaleY * 0.5+offsetY+SparkMap.get2dMapPosFromWorldPos(player.posZ-TargetZ,ImageScale);

                        GL11.glPushMatrix();
                        GlStateManager.translate(x,y,0);
                        GlStateManager.scale(0.3,0.3,0.3);
                        GuiPanelBase.drawRect(-12,-12,12,12, ClientConfig.getInstance().getMainColor().getRGB());
                        NetworkPlayerInfo info = mc.player.connection.getPlayerInfo(player.getName());
                        if(info != null)
                            GuiUtil.renderPlayerHead(info,-10,-10,20);
                        GL11.glPopMatrix();
                    }

                }
            }
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


    public static Vector2d ConvertPos(Vector2d pos, int fromDim, int toDim)
    {
        if(toDim != fromDim)
        {
            if(fromDim == 1 || toDim == 1)
            {
                pos.x = 0;
                pos.y = 0;
            }
            else {
                if(fromDim == -1)
                {
                    pos.x *= 8;
                    pos.y *= 8;
                }
                else{
                    pos.x /= 8;
                    pos.y /= 8;
                }
            }
        }
        return pos;
    }


}
