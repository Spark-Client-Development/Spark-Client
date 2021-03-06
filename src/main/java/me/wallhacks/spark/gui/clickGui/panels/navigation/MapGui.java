package me.wallhacks.spark.gui.clickGui.panels.navigation;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelBase;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelButton;
import me.wallhacks.spark.systems.clientsetting.clientsettings.MapConfig;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.MathUtil;
import me.wallhacks.spark.util.maps.SparkMap;
import me.wallhacks.spark.util.objects.MCStructures;
import me.wallhacks.spark.util.objects.Pair;
import me.wallhacks.spark.util.objects.Vec2d;
import me.wallhacks.spark.util.objects.Vec2i;
import me.wallhacks.spark.util.render.MapRender;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class MapGui extends GuiPanelBase {




    void resetValues() {
        dim = mc.player.dimension;

        zoom = 256.5;;
        offsetX = 0;
        offsetY = 0;
        screenInfoCoords = null;

        showBiomes = false;
    }

    int dim = 0;
    double zoom = 256.5;
    double offsetX = 0;
    double offsetY = 0;

    boolean showBiomes = false;

    MapGuiSubMenu mapGuiSubMenu = new MapGuiSubMenu(this);

    Vec2i screenInfoCoords;

    final ResourceLocation layersIcon = new ResourceLocation("textures/icons/layersicon.png");
    final ResourceLocation biomeIcon = new ResourceLocation("textures/icons/biomeicon.png");
    GuiPanelButton dimButton = new GuiPanelButton(() -> {


        if(dim != 1)
        {
            if(dim == 0)
            {
                offsetX/=8;
                offsetY/=8;
                dim = -1;
            }
            else
            {
                offsetX*=8;
                offsetY*=8;
                dim = 0;
            }
        }
    },"");

    GuiPanelButton biomeButton = new GuiPanelButton(() -> {
        showBiomes = !showBiomes;
    },"");

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);



        double mWheel = zoom*deltaTime*(Mouse.getDWheel())*0.00004;


        if(mWheel != 0)
        {

            Vec2d wp = new Vec2d(
                    SparkMap.getWorldPosFrom2dMapPos(offsetX,zoom),
                    SparkMap.getWorldPosFrom2dMapPos(offsetY,zoom)
            );


            zoom = MathHelper.clamp(zoom+mWheel,0.2,1800);
            if(screenInfoCoords != null)
                screenInfoCoords = null;

            offsetX = SparkMap.get2dMapPosFromWorldPos(wp.x,zoom);
            offsetY = SparkMap.get2dMapPosFromWorldPos(wp.y,zoom);
        }





        Vec2d pos = MapRender.ConvertPos(new Vec2d(mc.player.posX,mc.player.posZ),mc.player.dimension,dim);
        MapRender.RenderWholeMap(posX,posY,width,height,(float) zoom,0,pos.x,pos.y,offsetX,offsetY,dim, MouseX, MouseY, true,true,showBiomes);





        int layerButton = 18;


        dimButton.setPositionAndSize(posX+3,posY+height-3-layerButton,layerButton,layerButton);

        dimButton.drawBackGround(guiSettings.getGuiScreenBackgroundColor().getRGB());
        dimButton.drawBackGround(guiSettings.getGuiMainPanelBackgroundColor().getRGB());
        GuiUtil.drawCompleteImage(posX+3+2,posY+height-3-layerButton+2,layerButton-4,layerButton-4,layersIcon, Color.WHITE);
        dimButton.renderContent(MouseX, MouseY, deltaTime);



        if(Spark.mapManager.canShowBiomes(dim))
        {
            biomeButton.setPositionAndSize(posX+3,posY+height-3-layerButton-3-layerButton,layerButton,layerButton);

            biomeButton.drawBackGround(guiSettings.getGuiScreenBackgroundColor().getRGB());
            biomeButton.drawBackGround(guiSettings.getGuiMainPanelBackgroundColor().getRGB());
            GuiUtil.drawCompleteImage(posX+3+2,posY+height-3-layerButton-3-layerButton+2,layerButton-4,layerButton-4,biomeIcon, Color.WHITE);
            biomeButton.renderContent(MouseX, MouseY, deltaTime);
        }





        Vec2i mousePosOnMap = SparkMap.getWorldPosFromScreenPosOnMap(zoom,pos,MouseX-offsetX,MouseY-offsetY,posX+width/2,posY+height/2);

        mousePosOnMap = new Vec2i((int) MathUtil.round(mousePosOnMap.x,(int)(2000/zoom)), (int) MathUtil.round(mousePosOnMap.y,(int)(2000/zoom)));


        String coords = "("+mousePosOnMap.x +"," + mousePosOnMap.y+")";
        int coordsLen = fontManager.getTextWidth(coords)+4;
        int coordsHeight = fontManager.getTextHeight()+4;
        drawQuad(posX+width-coordsLen-3,posY+height-coordsHeight-3,coordsLen,coordsHeight,guiSettings.getGuiScreenBackgroundColor().getRGB());
        drawQuad(posX+width-coordsLen-3,posY+height-coordsHeight-3,coordsLen,coordsHeight,guiSettings.getGuiMainPanelBackgroundColor().getRGB());
        fontManager.drawString(coords,posX+width-coordsLen-3+2,posY+height-coordsHeight-3+2,guiSettings.getContrastColor().getRGB());


        if(screenInfoCoords != null)
        {
            mapGuiSubMenu.posX = screenInfoCoords.x;
            mapGuiSubMenu.posY = screenInfoCoords.y;
            mapGuiSubMenu.renderContent(MouseX, MouseY, deltaTime);


        }

    }

    double lMouseX;
    double lMouseY;

    @Override
    public void onClickDown(int MouseButton, int MouseX, int MouseY) {
        super.onClickDown(MouseButton, MouseX, MouseY);

        lMouseX = MouseX;
        lMouseY = MouseY;

        if(MouseButton == 1)
        {
            screenInfoCoords = new Vec2i(MouseX,MouseY);
            mapGuiSubMenu.inputField.setText("");

            Vec2i pos = SparkMap.getWorldPosFromScreenPosOnMap(zoom, new Vec2d(mc.player.posX,mc.player.posZ),MouseX-offsetX,MouseY-offsetY,posX+width/2,posY+height/2);

            Vec2i mp = SparkMap.getMapPosFromWorldPos(pos.x,pos.y);
            loop:
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    SparkMap map = Spark.mapManager.getMap(mp,dim);
                    for (Pair<Vec2i, MCStructures> i : map.structures) {
                        float x_start = (float) (posX+ (i.getKey().x*16)*zoom - offsetX);
                        float y_start = (float) (posY+ (i.getKey().y*16)*zoom - offsetY);

                        if(MathUtil.getDistanceFromTo(new Vec2d(MouseX,MouseY),new Vec2d(x_start,y_start)) < 10 && MapConfig.getInstance().StructureList.contains(i.getValue()))
                        {
                            mapGuiSubMenu.inputField.setText(i.getValue().name());
                            break loop;
                        }
                    }
                }
            }


        }
        else
            screenInfoCoords = null;
    }



    @Override
    public void onClick(int MouseButton, int MouseX, int MouseY) {
        super.onClick(MouseButton, MouseX, MouseY);

        if(MouseButton == 0)
        {
            offsetX += MouseX-lMouseX;
            offsetY += MouseY-lMouseY;
        }



        lMouseX = MouseX;
        lMouseY = MouseY;
    }
}
