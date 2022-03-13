package me.wallhacks.spark.gui.clickGui.panels.navigation;

import baritone.api.BaritoneAPI;
import baritone.api.cache.IWaypoint;
import baritone.api.cache.Waypoint;
import baritone.api.command.datatypes.ForWaypoints;
import baritone.api.pathing.goals.GoalXZ;
import baritone.api.utils.BetterBlockPos;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.gui.panels.GuiPanelButton;
import me.wallhacks.spark.gui.panels.GuiPanelInputField;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.maps.SparkMap;
import me.wallhacks.spark.util.objects.Vec2d;
import me.wallhacks.spark.util.objects.Vec2i;
import me.wallhacks.spark.util.render.MapRender;
import me.wallhacks.spark.util.render.RenderUtil;
import net.minecraft.client.renderer.entity.Render;

public class MapGuiSubMenu extends GuiPanelBase {

    MapGui mapGui;

    public MapGuiSubMenu(MapGui mapGui) {
        this.mapGui = mapGui;
    }

    GuiPanelButton gotoButton = new GuiPanelButton(() -> {
        Vec2i pos = SparkMap.getWorldPosFromScreenPosOnMap(mapGui.zoom, MapRender.ConvertPos(new Vec2d(mc.player.posX,mc.player.posZ),mc.player.dimension,mapGui.dim),posX-mapGui.offsetX,posY-mapGui.offsetY,mapGui.posX+mapGui.width/2,mapGui.posY+mapGui.height/2);

        BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalXZ(pos.x,pos.y));

        //close gui
        mc.displayGuiScreen(null);

        mapGui.screenInfoCoords = null;
    },"Goto");
    GuiPanelInputField inputField = new GuiPanelInputField(8,0,0,0,0);

    boolean addingWayPoint = false;

    GuiPanelButton addWayPointButton = new GuiPanelButton(() -> {
        Vec2i pos = SparkMap.getWorldPosFromScreenPosOnMap(mapGui.zoom, MapRender.ConvertPos(new Vec2d(mc.player.posX,mc.player.posZ),mc.player.dimension,mapGui.dim),posX-mapGui.offsetX,posY-mapGui.offsetY,mapGui.posX+mapGui.width/2,mapGui.posY+mapGui.height/2);

        if(addingWayPoint && inputField.getText().length() > 0)
        {
            Spark.waypointManager.createWayPoint(pos, mapGui.dim,inputField.getText());

            mapGui.screenInfoCoords = null;
        }
        addingWayPoint = !addingWayPoint;
    },"WayPoint");


    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);

        addWayPointButton.setText("Waypoint");
        int lenGotoButton = 4+fontManager.getTextWidth(gotoButton.getText());
        int lenAddWayPointButton = 4+fontManager.getTextWidth(addWayPointButton.getText());


        width = 6+lenGotoButton+lenAddWayPointButton;

        RenderUtil.drawFilledCircle(posX,posY,3,guiSettings.getContrastColor().getRGB());
        drawQuad(posX,posY,width,height,guiSettings.getContrastColor().getRGB());
        drawQuad(posX,posY,width,height,guiSettings.getGuiScreenBackgroundColor().getRGB());

        height = 15;

        if(addingWayPoint)
        {
            inputField.setPositionAndSize(posX+2,posY+2,width-4,11);
            inputField.setBackGroundColor(guiSettings.getGuiSubPanelBackgroundColor().getRGB());
            inputField.renderContent(MouseX, MouseY, deltaTime);

            addWayPointButton.setText(inputField.getText().length() > 0 ? "Add Waypoint" : "Cancel");
            addWayPointButton.setPositionAndSize(posX+2,posY+11+4,width-4,11);
            addWayPointButton.renderContent(MouseX, MouseY, deltaTime);

            height+=11+2;
        }
        else
        {
            gotoButton.setPositionAndSize(posX+2,posY+2,lenGotoButton,11);
            gotoButton.renderContent(MouseX, MouseY, deltaTime);
            addWayPointButton.setText("Waypoint");
            addWayPointButton.setPositionAndSize(posX+lenGotoButton+4,posY+2,lenAddWayPointButton,11);
            addWayPointButton.renderContent(MouseX, MouseY, deltaTime);
        }



    }
}
