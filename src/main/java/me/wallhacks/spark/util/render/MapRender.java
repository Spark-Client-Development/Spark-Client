package me.wallhacks.spark.util.render;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.manager.FontManager;
import me.wallhacks.spark.manager.MapManager;
import me.wallhacks.spark.manager.WaypointManager;
import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;
import me.wallhacks.spark.systems.clientsetting.clientsettings.MapConfig;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.MathUtil;
import me.wallhacks.spark.util.maps.SparkMap;
import me.wallhacks.spark.util.objects.*;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import scala.reflect.internal.AnnotationInfos;

import java.awt.*;
import java.util.ArrayList;

public class MapRender implements MC {


    private static final ResourceLocation ARROW_ICON = new ResourceLocation("textures/icons/arrowicon.png");


    public enum MapGrid {
        Chunks(16, new Vec2i(200,Integer.MAX_VALUE), new Color(0xFF4D4D57, true)),
        Regions(16*32, new Vec2i(40,450), new Color(0xFF9F9FBD, true)),
        Sector(4096, new Vec2i(3,80), new Color(0xFF474750, true)),
        LargeSector(16384*2, new Vec2i(1,6), new Color(0xFFA1A6A6, true));



        final int size;
        final float scaledSize;

        final Vec2i range;

        final Color color;



        MapGrid(int sizeInBlocks, Vec2i range, Color color) {
            this.size = sizeInBlocks;
            this.scaledSize = sizeInBlocks/SparkMap.scale;
            this.range = range;

            this.color = color;

        }
    }

    public enum Highway {
        EastHighway(0,0,Integer.MAX_VALUE,0,new Color(0x9E3434A6, true)),
        SouthHighway(0,0,0,Integer.MAX_VALUE,new Color(0x9E3434A6, true)),
        NorthHighway(0,0,0,Integer.MIN_VALUE,new Color(0x9E3434A6, true)),
        WestHighway(0,0,Integer.MIN_VALUE,0,new Color(0x9E3434A6, true)),

        SouthEastHighway(0,0,Integer.MAX_VALUE,Integer.MAX_VALUE,new Color(0x9D34A67A, true)),
        SouthWestHighway(0,0,Integer.MIN_VALUE,Integer.MAX_VALUE,new Color(0x9D34A67A, true)),
        NorthWestHighway(0,0,Integer.MIN_VALUE,Integer.MIN_VALUE,new Color(0x9D34A67A, true)),
        NorthEastHighway(0,0,Integer.MAX_VALUE,Integer.MIN_VALUE,new Color(0x9D34A67A, true));


        final int startX;
        final int startZ;


        final int endX;
        final int endZ;


        final Color color;

        Highway(int startX, int startZ, int endX, int endZ, Color color) {
            this.startX = startX;
            this.startZ = startZ;
            this.endX = endX;
            this.endZ = endZ;
            this.color = color;
        }

    }




    public static void RenderWholeMap(int ImageStartX, int ImageStartY, int ImageScaleX, int ImageScaleY, double ImageScale,float rotation, double TargetX, double TargetZ, double offsetX, double offsetY, int dim, double mouseX, double mouseY, boolean hover,boolean drawGrid, boolean showBiomes) {
        GL11.glPushMatrix();
        GuiUtil.glScissor(ImageStartX, ImageStartY, ImageScaleX, ImageScaleY);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);


        if(rotation % 90 != 0)
        {
            GlStateManager.translate(ImageStartX+ImageScaleX/2,ImageStartY+ImageScaleY/2,0);
            GlStateManager.rotate(rotation, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(-ImageStartX-ImageScaleX/2,-ImageStartY-ImageScaleY/2,0);


            if(hover)
                new Exception("hover needs to be false when rotate is not 0").printStackTrace();
            ImageStartX -= ImageScaleX/2;
            ImageStartY -= ImageScaleY/2;
            ImageScaleX*=2;
            ImageScaleY*=2;
        }

        //background
        Gui.drawRect(ImageStartX, ImageStartY, ImageStartX + ImageScaleX, ImageStartY + ImageScaleY, new Color(68, 68, 68, 165).getRGB());
        GlStateManager.color(1, 1, 1, 1);


        float centerX = (float) (SparkMap.get2dMapPosFromWorldPos(TargetX, ImageScale) - ImageScaleX * 0.5 - (offsetX));
        float centerY = (float) (SparkMap.get2dMapPosFromWorldPos(TargetZ, ImageScale) - ImageScaleY * 0.5 - (offsetY));

        Vec2i WholeMapStartPos = SparkMap.getMapPosFrom2dMapPos(centerX, centerY, ImageScale);
        Vec2i WholeMapEndPos = SparkMap.getMapPosFrom2dMapPos(centerX + ImageScaleX, centerY + ImageScaleY, ImageScale);



        float mapAlpha = (float) MathHelper.clamp((ImageScale-10)/10f,0,1);

        float thick = 0.4f;

        if(drawGrid)
        for (int i = 0; i < MapGrid.values().length; i++) {
            MapGrid grid = MapGrid.values()[i];
            if(grid.range.x < ImageScale && ImageScale < grid.range.y)
            {
                double scale = grid.scaledSize/SparkMap.size*ImageScale;

                float conv = grid.size/SparkMap.getWidthAndHeight();

                //this code can be made better in so many ways :(
                float x_start = (float) (ImageStartX+ (Math.floor(WholeMapStartPos.x/conv)*conv)*ImageScale - centerX);
                float y_start = (float) (ImageStartY+ (Math.floor(WholeMapStartPos.y/conv)*conv)*ImageScale - centerY);


                GuiUtil.linePre(grid.color,thick);

                thick*=1.4f;

                for (float xr = x_start; xr < ImageStartX+ImageScaleX+scale; xr+=scale) {
                    GL11.glBegin(2);
                    GL11.glVertex2d(xr, ImageStartY);
                    GL11.glVertex2d(xr, ImageStartY+ImageScaleY);
                    GL11.glEnd();
                }

                for (float yr = y_start; yr < ImageStartY+ImageScaleY+scale; yr+=scale) {
                    GL11.glBegin(2);
                    GL11.glVertex2d(ImageStartX,yr);
                    GL11.glVertex2d(ImageStartX+ImageScaleX,yr);
                    GL11.glEnd();
                }


                GuiUtil.linePost();

            }

        }




        ArrayList<Pair<Vec2i,MCStructures>> structuresHashMap = new ArrayList<Pair<Vec2i, MCStructures>>();


        if(showBiomes && !Spark.mapManager.canShowBiomes(dim))
            showBiomes = false;

        //render map

        if(mapAlpha > 0)
        for (int x = WholeMapStartPos.x; x <= WholeMapEndPos.x; x++) {
            for (int y = WholeMapStartPos.y; y <= WholeMapEndPos.y; y++) {


                SparkMap map = MapManager.instance.getMap(new Vec2i(x, y), dim);

                double x_ = map.getStartPos().x * (ImageScale / SparkMap.getWidthAndHeight()) - centerX;
                double y_ = map.getStartPos().y * (ImageScale / SparkMap.getWidthAndHeight()) - centerY;

                if(!map.structures.isEmpty())
                    structuresHashMap.addAll(new ArrayList<>(map.structures));



                if(showBiomes){
                    if(!map.isBiomeMapEmpty())
                    {


                        ResourceLocation location = map.getBiomeResourceLocation();

                        if(location != null)
                        {


                            GuiUtil.drawCompleteImage(ImageStartX + x_, ImageStartY + y_, ImageScale, ImageScale,location,new Color(1f,1f,1f,mapAlpha));
                        }
                    }
                    else
                        Spark.mapManager.addToGenerateBiomeMap(map);
                } else if(!map.isEmpty())
                {


                    ResourceLocation location = map.getResourceLocation();

                    if(location != null)
                    {


                        GuiUtil.drawCompleteImage(ImageStartX + x_, ImageStartY + y_, ImageScale, ImageScale,location,new Color(1f,1f,1f,mapAlpha));
                    }
                }



            }
        }


        FontManager fontManager = Spark.fontManager;

        //highways
        if(drawGrid)
        {
            for (Highway highway : Highway.values()) {
                double xs = ImageStartX + ImageScaleX * 0.5 + offsetX + SparkMap.get2dMapPosFromWorldPos(highway.startX - TargetX, ImageScale);
                double ys = ImageStartY + ImageScaleY * 0.5 + offsetY + SparkMap.get2dMapPosFromWorldPos(highway.startZ - TargetZ, ImageScale);

                double xe = ImageStartX + ImageScaleX * 0.5 + offsetX + SparkMap.get2dMapPosFromWorldPos(highway.endX - TargetX, ImageScale);
                double ye = ImageStartY + ImageScaleY * 0.5 + offsetY + SparkMap.get2dMapPosFromWorldPos(highway.endZ - TargetZ, ImageScale);

                Vec2d cs = MathUtil.clamp(new Vec2d(xs,ys),new Vec2d(ImageStartX-5,ImageStartY-5),new Vec2d(ImageStartX+ImageScaleX+5,ImageStartY+ImageScaleY+5),new Vec2d(xe,ye));
                Vec2d ce = MathUtil.clamp(new Vec2d(xe,ye),new Vec2d(ImageStartX-5,ImageStartY-5),new Vec2d(ImageStartX+ImageScaleX+5,ImageStartY+ImageScaleY+5),new Vec2d(xs,ys));

                Vec2d mouse = new Vec2d(mouseX,mouseY);
                boolean isOn = hover && MathUtil.inLine(cs,ce,mouse,5);

                GuiUtil.linePre(highway.color,isOn ? 3f : 1.5f);


                GL11.glBegin(2);
                GL11.glVertex2d(cs.x,cs.y);
                GL11.glVertex2d(ce.x,ce.y);
                GL11.glEnd();


                GuiUtil.linePost();

                if(isOn)
                {
                    GL11.glPushMatrix();
                    GlStateManager.translate(mouseX, mouseY, 0);

                    String name = highway.name();
                    GuiUtil.drawQuad(0, 0, fontManager.getTextWidth(name + 2), fontManager.getTextHeight() + 3, new Color(56, 53, 53, 245).getRGB());
                    fontManager.drawString(name, 2, 2, new Color(239, 224, 224).getRGB());


                    GL11.glPopMatrix();

                }

            }
        }



        if(MapConfig.getInstance().Structures.isOn())
        for (Pair<Vec2i, MCStructures> structuresPair : structuresHashMap) {

            if(!MapConfig.getInstance().StructureList.contains(structuresPair.getValue()))
                continue;

            Vec2d pos = new Vec2d(structuresPair.getKey().x*16,structuresPair.getKey().y*16);
            double x = ImageStartX + ImageScaleX * 0.5 + offsetX + SparkMap.get2dMapPosFromWorldPos(pos.x - TargetX, ImageScale);
            double y = ImageStartY + ImageScaleY * 0.5 + offsetY + SparkMap.get2dMapPosFromWorldPos(pos.y - TargetZ, ImageScale);

            boolean hovered = false;
            if (hover) {
                Double distance = Math.sqrt((y - mouseY) * (y - mouseY) + (x - mouseX) * (x - mouseX));
                if (distance < 3) hovered = true;
            }

            MCStructures structures = structuresPair.getValue();


            double s = Math.min(structures.getSize()*(ImageScale / SparkMap.getWidthAndHeight()),0.6);

            double hideSize = 0.1;

            if(s < hideSize)
                continue;

            GL11.glPushMatrix();
            GlStateManager.translate(x, y, 0);

            float alpha = (float) Math.min(1,Math.abs(s-hideSize)/0.1);


            if(hovered)
                s*=1.3;

            GlStateManager.scale(s,s,s);
            GuiUtil.drawCompleteImageRotated(-6,-6,6*2,6*2,0,structures.getResourceLocation(),new Color(1f,1f,1f,alpha));

            GL11.glPopMatrix();

        }







        for (WaypointManager.Waypoint point : Spark.waypointManager.getWayPoints()) {

            if (point.getDim() == dim || (point.getDim() != 1 && dim != 1)) {

                Vec2d pos = point.getLocation2d(point.getDim(), dim);
                double x = (ImageStartX + ImageScaleX * 0.5 + offsetX + SparkMap.get2dMapPosFromWorldPos(pos.x - TargetX, ImageScale));
                double y = (ImageStartY + ImageScaleY * 0.5 + offsetY + SparkMap.get2dMapPosFromWorldPos(pos.y - TargetZ, ImageScale));
                boolean hovered = false;
                if (hover) {
                    Double distance = Math.sqrt((y - mouseY) * (y - mouseY) + (x - mouseX) * (x - mouseX));
                    if (distance < 3) hovered = true;
                }
                GL11.glPushMatrix();
                GlStateManager.translate(x, y, 0);

                RenderUtil.drawFilledCircle(0, 0, hovered ? 4 : 3, new Color(56, 53, 53, 245).getRGB());
                RenderUtil.drawFilledCircle(0, 0, hovered ? 3 : 2, point.getColor().getRGB());
                if (hovered) {

                    GuiUtil.drawQuad(0, 0, fontManager.getTextWidth(point.getName() + 2), fontManager.getTextHeight() + 3, new Color(56, 53, 53, 245).getRGB());
                    fontManager.drawString(point.getName(), 2, 2, new Color(239, 224, 224).getRGB());
                }

                GL11.glPopMatrix();


            }


        }



        if (mc.player.dimension == dim) {
            for (Entity e : mc.world.loadedEntityList) {
                if (e instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) e;

                    if (player != mc.player) {
                        double x = ImageStartX + ImageScaleX * 0.5 + offsetX + SparkMap.get2dMapPosFromWorldPos(player.posX - TargetX, ImageScale);
                        double y = ImageStartY + ImageScaleY * 0.5 + offsetY + SparkMap.get2dMapPosFromWorldPos(player.posZ - TargetZ, ImageScale);

                        GL11.glPushMatrix();
                        GlStateManager.translate(x,y,0);
                        GlStateManager.scale(0.3,0.3,0.3);
                        Gui.drawRect(-12,-12,12,12, ClientConfig.getInstance().getMainColor().getRGB());

                        NetworkPlayerInfo info = mc.player.connection.getPlayerInfo(player.getName());
                        if (info != null)
                            GuiUtil.renderPlayerHead(info, -10, -10, 20);
                        GL11.glPopMatrix();
                    }

                }
            }
        }





        //arrow
        if (dim == mc.player.dimension || (dim != 1 && mc.player.dimension != 1)) {

            Vec2d pos = ConvertPos(new Vec2d(mc.player.posX, mc.player.posZ), mc.player.dimension, dim);
            float OffsetXtoPlayer = (float) (ImageStartX + ImageScaleX * 0.5 + offsetX + SparkMap.get2dMapPosFromWorldPos(pos.x - TargetX, ImageScale));
            float OffsetYtoPlayer = (float) (ImageStartY + ImageScaleY * 0.5 + offsetY + SparkMap.get2dMapPosFromWorldPos(pos.y - TargetZ, ImageScale));

            GuiUtil.drawCompleteImageRotated(OffsetXtoPlayer - 2, OffsetYtoPlayer - 2, 4, 4, mc.player.rotationYaw + 90, ARROW_ICON, dim == -1 ? Color.WHITE : Color.RED);
        }


        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        GL11.glPopMatrix();

        GlStateManager.enableBlend();

    }



    public static Vec2d ConvertPos(Vec2d pos, int fromDim, int toDim) {
        if (toDim != fromDim) {
            if (fromDim == 1 || toDim == 1) {
                pos.x = 0;
                pos.y = 0;
            } else {
                if (fromDim == -1) {
                    pos.x *= 8;
                    pos.y *= 8;
                } else {
                    pos.x /= 8;
                    pos.y /= 8;
                }
            }
        }
        return pos;
    }


}
