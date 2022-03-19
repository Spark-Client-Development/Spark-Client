package me.wallhacks.spark.gui.clickGui.panels.navigation.waypointlist;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.clickGui.panels.navigation.NavigationGui;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelBase;
import me.wallhacks.spark.manager.WaypointManager;

import java.util.ArrayList;

public class WayPointList extends GuiPanelBase {

    public WayPointList(NavigationGui navigationGui){
        this.navigationGui = navigationGui;
    }

    NavigationGui navigationGui;

    ArrayList<WayPointItem> wayPointListItems = new ArrayList<>();


    void RefreshList() {
        ArrayList<WaypointManager.Waypoint> p = new ArrayList<>(Spark.waypointManager.getWayPoints());

        for (int i = wayPointListItems.size()-1; i >= 0; i--) {
            if(p.contains(wayPointListItems.get(i).waypoint))
                p.remove(wayPointListItems.get(i).waypoint);
            else
                wayPointListItems.remove(i);
        }

        for (WaypointManager.Waypoint waypoint : p) {
            wayPointListItems.add(new WayPointItem(waypoint,navigationGui));
        }
    }



    public void renderContent(int MouseX, int MouseY, float deltaTime) {

        RefreshList();

        super.renderContent(MouseX,MouseY,deltaTime);



        int spacing = guiSettings.spacing;

        int h = spacing;
        for (WayPointItem item : wayPointListItems) {


            item.setPositionAndSize(posX+spacing,posY+h,width-spacing*2,18);
            item.renderContent(MouseX,MouseY,deltaTime);

            h += 18 + 2;
        }
        h+=20;

        height = h;





    }




}
