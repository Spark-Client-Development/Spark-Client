package me.wallhacks.spark.gui.clickGui.panels.navigation;

import baritone.Baritone;
import baritone.BaritoneProvider;
import baritone.api.BaritoneAPI;
import baritone.api.cache.IWaypoint;
import baritone.api.cache.Waypoint;
import baritone.api.command.datatypes.ForWaypoints;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.utils.BetterBlockPos;
import com.github.lunatrius.core.util.vector.Vector2d;
import com.sun.javafx.geom.Vec2d;
import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.gui.panels.GuiPanelButton;
import me.wallhacks.spark.systems.hud.huds.Map;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.maps.SparkMap;
import me.wallhacks.spark.util.objects.Vec2i;
import me.wallhacks.spark.util.render.MapRender;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiConfig;
import org.lwjgl.input.Keyboard;
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
    double zoom = 128;
    double offsetX = 0;
    double offsetY = 0;


    Vec2i screenInfoCoords;
    GuiPanelButton gotoButton = new GuiPanelButton(() -> {
        Vec2i pos = SparkMap.getWorldPosFromScreenPosOnMap(zoom, MapRender.ConvertPos(new Vector2d(mc.player.posX,mc.player.posZ),mc.player.dimension,dim),screenInfoCoords.x-offsetX,screenInfoCoords.y-offsetY,posX+width/2,posY+height/2);

        BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalXZ(pos.x,pos.y));

        screenInfoCoords = null;
        //close gui
        mc.displayGuiScreen(null);


    },"Goto");
    GuiPanelButton addWayPointButton = new GuiPanelButton(() -> {
        Vec2i pos = SparkMap.getWorldPosFromScreenPosOnMap(zoom, MapRender.ConvertPos(new Vector2d(mc.player.posX,mc.player.posZ),mc.player.dimension,dim),screenInfoCoords.x-offsetX,screenInfoCoords.y-offsetY,posX+width/2,posY+height/2);


        ForWaypoints.waypoints(BaritoneAPI.getProvider().getPrimaryBaritone()).addWaypoint(new Waypoint("Test", IWaypoint.Tag.USER,new BetterBlockPos(pos.x,(int)mc.player.posY,pos.y)));
        screenInfoCoords = null;
    },"WayPoint");

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
            zoom = MathHelper.clamp(zoom+mWheel,20,128);
            if(screenInfoCoords != null)
                screenInfoCoords = null;
        }




        Vector2d pos = MapRender.ConvertPos(new Vector2d(mc.player.posX,mc.player.posZ),mc.player.dimension,dim);
        MapRender.RenderWholeMap(posX,posY,width,height,(int)zoom,pos.x,pos.y,offsetX,offsetY,dim);





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

            int lenGotoButton = 4+fontManager.getTextWidth(gotoButton.getText());
            int lenAddWayPointButton = 4+fontManager.getTextWidth(addWayPointButton.getText());

            drawQuad(screenInfoCoords.x,screenInfoCoords.y,6+lenGotoButton+lenAddWayPointButton,15,guiSettings.getGuiScreenBackgroundColor().getRGB());
            drawQuad(screenInfoCoords.x,screenInfoCoords.y,6+lenGotoButton+lenAddWayPointButton,15,guiSettings.getGuiMainPanelBackgroundColor().getRGB());


            gotoButton.setPositionAndSize(screenInfoCoords.x+2,screenInfoCoords.y+2,lenGotoButton,11);
            gotoButton.renderContent(MouseX, MouseY, deltaTime);
            addWayPointButton.setPositionAndSize(screenInfoCoords.x+lenGotoButton+4,screenInfoCoords.y+2,lenAddWayPointButton,11);
            addWayPointButton.renderContent(MouseX, MouseY, deltaTime);
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
