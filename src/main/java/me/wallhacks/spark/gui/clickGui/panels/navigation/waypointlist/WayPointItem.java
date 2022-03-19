package me.wallhacks.spark.gui.clickGui.panels.navigation.waypointlist;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.gui.clickGui.panels.navigation.NavigationGui;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelBase;
import me.wallhacks.spark.manager.WaypointManager;
import me.wallhacks.spark.util.render.ColorUtil;

public class WayPointItem extends GuiPanelBase {


    NavigationGui navigationGui;
    WaypointManager.Waypoint waypoint;
    public WayPointItem(WaypointManager.Waypoint waypoint, NavigationGui navigationGui) {
        this.waypoint = waypoint;
        this.navigationGui = navigationGui;
    }

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);



        super.renderContent(MouseX, MouseY, deltaTime);






        int xp = fontManager.drawString(waypoint.getName(),posX,posY+4,isMouseOn ? guiSettings.getContrastColor().brighter().getRGB() : guiSettings.getContrastColor().getRGB());

        xp = fontManager.drawString(ChatFormatting.ITALIC+waypoint.getDimName(),xp+5,posY+4, ColorUtil.getDimColor(waypoint.getDim()).getRGB());


        int y = posY+4+ fontManager.getTextHeight()/2;

        int x = posX + width - 10;

        drawQuad(x-6,y-6,12,12,isMouseOn ? guiSettings.getContrastColor().brighter().getRGB() : guiSettings.getContrastColor().getRGB());
        drawQuad(x-5,y-5,10,10,waypoint.getColor().getRGB());



        height = 8 + fontManager.getTextHeight();



    }

    @Override
    public void onClickDown(int MouseButton, int MouseX, int MouseY) {
        super.onClickDown(MouseButton, MouseX, MouseY);
        navigationGui.EditWaypoint(waypoint);
    }
}
