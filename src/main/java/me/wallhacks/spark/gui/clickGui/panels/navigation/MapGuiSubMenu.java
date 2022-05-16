package me.wallhacks.spark.gui.clickGui.panels.navigation;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelBase;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelButton;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelInputField;
import me.wallhacks.spark.util.maps.SparkMap;
import me.wallhacks.spark.util.objects.Vec2d;
import me.wallhacks.spark.util.objects.Vec2i;
import me.wallhacks.spark.util.render.MapRender;
import me.wallhacks.spark.util.render.RenderUtil;

public class MapGuiSubMenu extends GuiPanelBase {

    MapGui mapGui;

    public MapGuiSubMenu(MapGui mapGui) {
        this.mapGui = mapGui;
    }


    GuiPanelInputField inputField = new GuiPanelInputField(8,0,0,0,0);



    GuiPanelButton addWayPointButton = new GuiPanelButton(() -> {
        Vec2i pos = SparkMap.getWorldPosFromScreenPosOnMap(mapGui.zoom, new Vec2d(mc.player.posX,mc.player.posZ),posX-mapGui.offsetX,posY-mapGui.offsetY,mapGui.posX+mapGui.width/2,mapGui.posY+mapGui.height/2);

        if(inputField.getText().length() > 0)
        {
            Spark.waypointManager.createWayPoint(pos, mapGui.dim,inputField.getText());

            mapGui.screenInfoCoords = null;
        }

    },"Add Waypoint");


    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);



        int lenAddWayPointButton = 4+fontManager.getTextWidth(addWayPointButton.getText());


        width = lenAddWayPointButton;

        RenderUtil.drawFilledCircle(posX,posY,3,guiSettings.getContrastColor().getRGB());
        drawQuad(posX,posY,width,height,guiSettings.getContrastColor().getRGB());
        drawQuad(posX,posY,width,height,guiSettings.getGuiScreenBackgroundColor().getRGB());

        height = 15;

        inputField.setPositionAndSize(posX+2,posY+2,width-4,11);
        inputField.setBackGroundColor(guiSettings.getGuiSubPanelBackgroundColor().getRGB());
        inputField.renderContent(MouseX, MouseY, deltaTime);


        addWayPointButton.setPositionAndSize(posX+2,posY+11+4,width-4,11);
        addWayPointButton.renderContent(MouseX, MouseY, deltaTime);

        height+=11+2;



    }
}
