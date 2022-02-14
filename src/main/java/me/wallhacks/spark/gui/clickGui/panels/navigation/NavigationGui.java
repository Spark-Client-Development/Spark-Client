package me.wallhacks.spark.gui.clickGui.panels.navigation;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalXZ;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.clickGui.ClickGuiMenuBase;
import me.wallhacks.spark.gui.clickGui.ClickGuiPanel;
import me.wallhacks.spark.gui.clickGui.panels.configs.configList.ConfigListGui;
import me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.GuiEditSettingPanel;
import me.wallhacks.spark.gui.clickGui.panels.navigation.waypointlist.WayPointList;
import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.gui.panels.GuiPanelButton;
import me.wallhacks.spark.gui.panels.GuiPanelScroll;
import me.wallhacks.spark.manager.ConfigManager;
import me.wallhacks.spark.manager.WaypointManager;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.objects.Vec2i;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class NavigationGui extends ClickGuiPanel implements MC {


    public NavigationGui(ClickGuiMenuBase clickGuiMenuBase) {
        super(clickGuiMenuBase);
    }

    @Override
    public String getName() {
        return "Navigation";
    }


    @Override
    public void init() {
        super.init();
        map.resetValues();
        guiEditSettingPanel.setCurrentSettingsHolder(null);
    }


    final WayPointList configListGui = new WayPointList(this);
    final GuiPanelScroll guiPanelScroll = new GuiPanelScroll(0, 0, 0, 0,configListGui);

    final GuiEditSettingPanel guiEditSettingPanel = new GuiEditSettingPanel();

    final MapGui map = new MapGui();

    boolean inMap = true;

    final GuiPanelButton MapButton = new GuiPanelButton(() -> {
        inMap = true;
    }, "Maps");
    final GuiPanelButton WayPointButton = new GuiPanelButton(() -> {
        inMap = false;
    }, "Waypoints");



    final GuiPanelButton addWayPointButton = new GuiPanelButton(() -> {
        Spark.waypointManager.createWayPoint(new Vec2i((int)mc.player.posX,(int)mc.player.posZ),mc.player.dimension);
    }, "AddWaypoint");




    public final GuiPanelButton deleteButton = new GuiPanelButton(() -> {
        WaypointManager.Waypoint waypoint = (WaypointManager.Waypoint) guiEditSettingPanel.getCurrentSettingsHolder();

        Spark.waypointManager.getWayPoints().remove(waypoint);
        if(guiEditSettingPanel.getCurrentSettingsHolder() == waypoint)
            guiEditSettingPanel.setCurrentSettingsHolder(null);
    }, "Delete");
    public final GuiPanelButton gotoButton = new GuiPanelButton(() -> {
        WaypointManager.Waypoint waypoint = (WaypointManager.Waypoint) guiEditSettingPanel.getCurrentSettingsHolder();

        Vec3i v = waypoint.getLocation();

        Spark.sendInfo("Going to "+waypoint.getName());

        if(waypoint.hasY())
            BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalBlock(new BlockPos(v.getX(),v.getY(),v.getZ())));
        else
            BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalXZ(v.getX(),v.getZ()));

        mc.displayGuiScreen(null);
    }, "Goto");




    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {

        super.renderContent(MouseX,MouseY,deltaTime);

        int ListWidth = 190;
        int height = 238;

        int width = ListWidth + ListWidth + guiSettings.spacing;

        int x = getCenterX()-width/2;
        int y = getCenterY()-height/2;

        //gui background
        Gui.drawRect(x-4,y-4,x+width+4,y+height+4, guiSettings.getGuiMainPanelBackgroundColor().getRGB());


        int searchFieldHeight = 18;

        if(inMap)
        {
            map.setPositionAndSize(x,y,width,height);
            map.renderContent(MouseX, MouseY, deltaTime);
        }
        else
        {
            Gui.drawRect(x,y,x+ListWidth,y+searchFieldHeight,guiSettings.getGuiSubPanelBackgroundColor().getRGB());

            fontManager.drawString("Waypoints",x+4,y+searchFieldHeight/2-fontManager.getTextHeight()/2, guiSettings.getContrastColor().getRGB());

            guiPanelScroll.setPositionAndSize(x,y + searchFieldHeight + guiSettings.spacing,ListWidth,height-searchFieldHeight-guiSettings.spacing);
            guiPanelScroll.drawBackGround(guiSettings.getGuiSubPanelBackgroundColor().getRGB());
            guiPanelScroll.renderContent(MouseX,MouseY,deltaTime);


            x += ListWidth + guiSettings.spacing;


            int yh = height;
            if(guiEditSettingPanel.getCurrentSettingsHolder() instanceof WaypointManager.Waypoint)
            {
                deleteButton.setOverrideColor(guiSettings.getGuiSubPanelBackgroundColor());
                gotoButton.setOverrideColor(guiSettings.getGuiSubPanelBackgroundColor());

                int bw = (ListWidth-guiSettings.spacing)/2;
                deleteButton.setPositionAndSize(x,y+yh-searchFieldHeight,bw,searchFieldHeight);
                gotoButton.setPositionAndSize(x+bw+guiSettings.spacing,y+yh-searchFieldHeight,bw,searchFieldHeight);

                deleteButton.renderContent(MouseX, MouseY, deltaTime);
                gotoButton.renderContent(MouseX, MouseY, deltaTime);
                yh-=searchFieldHeight+guiSettings.spacing;
            }

            guiEditSettingPanel.setPositionAndSize(x,y,ListWidth,yh);
            guiEditSettingPanel.renderContent(MouseX,MouseY,deltaTime);


        }



        x = getCenterX()-width/2;
        y = getCenterY()-height/2 + height+guiSettings.spacing + searchFieldHeight + guiSettings.spacing;


        Gui.drawRect(x-4,y-4,x+width+4,y+18+4, guiSettings.getGuiMainPanelBackgroundColor().getRGB());

        MapButton.setOverrideColor(guiSettings.getGuiSubPanelBackgroundColor());
        MapButton.setPositionAndSize(x,y,ListWidth,searchFieldHeight);
        MapButton.renderContent(MouseX,MouseY,deltaTime);


        x += ListWidth + guiSettings.spacing;

        WayPointButton.setOverrideColor(guiSettings.getGuiSubPanelBackgroundColor());
        WayPointButton.setPositionAndSize(x,y,ListWidth,searchFieldHeight);
        WayPointButton.renderContent(MouseX,MouseY,deltaTime);









    }


    public void EditWaypoint(WaypointManager.Waypoint waypoint)
    {

        guiEditSettingPanel.setCurrentSettingsHolder(waypoint);
    }
}
