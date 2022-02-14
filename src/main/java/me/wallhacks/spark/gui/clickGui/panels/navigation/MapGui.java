package me.wallhacks.spark.gui.clickGui.panels.navigation;

import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.gui.panels.GuiPanelButton;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.maps.SparkMap;
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

        zoom = 128;
        offsetX = 0;
        offsetY = 0;
        screenInfoCoords = null;
    }

    int dim = 0;
    double zoom = 128.6;
    double offsetX = 0;
    double offsetY = 0;

    MapGuiSubMenu mapGuiSubMenu = new MapGuiSubMenu(this);

    Vec2i screenInfoCoords;

    final ResourceLocation layersIcon = new ResourceLocation("textures/icons/layersicon.png");
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


    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);



        double mWheel = zoom*deltaTime*(Mouse.getDWheel())*0.0002;

        if(mWheel != 0)
        {
            zoom = MathHelper.clamp(zoom+mWheel,20,200);
            if(screenInfoCoords != null)
                screenInfoCoords = null;
        }




        Vec2d pos = MapRender.ConvertPos(new Vec2d(mc.player.posX,mc.player.posZ),mc.player.dimension,dim);
        MapRender.RenderWholeMap(posX,posY,width,height,(int)zoom,pos.x,pos.y,offsetX,offsetY,dim, MouseX, MouseY, true);





        int layerButton = 18;


        dimButton.setPositionAndSize(posX+3,posY+height-3-layerButton,layerButton,layerButton);

        dimButton.drawBackGround(guiSettings.getGuiScreenBackgroundColor().getRGB());
        dimButton.drawBackGround(guiSettings.getGuiMainPanelBackgroundColor().getRGB());
        GuiUtil.drawCompleteImage(posX+3+2,posY+height-3-layerButton+2,layerButton-4,layerButton-4,layersIcon, Color.WHITE);
        dimButton.renderContent(MouseX, MouseY, deltaTime);




        Vec2i mousePosOnMap = SparkMap.getWorldPosFromScreenPosOnMap(zoom,pos,MouseX-offsetX,MouseY-offsetY,posX+width/2,posY+height/2);
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
