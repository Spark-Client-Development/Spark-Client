package me.wallhacks.spark.gui.clickGui.panels.navigation;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.clickGui.ClickGuiMenuBase;
import me.wallhacks.spark.gui.clickGui.ClickGuiPanel;
import me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.GuiEditSettingPanel;
import me.wallhacks.spark.gui.clickGui.panels.navigation.waypointlist.WayPointList;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelButton;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelScroll;
import me.wallhacks.spark.manager.WaypointManager;
import me.wallhacks.spark.systems.clientsetting.clientsettings.MapConfig;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.objects.Vec2i;
import net.minecraft.client.gui.Gui;

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
        guiMapConfig.setCurrentSettingsHolder(MapConfig.getInstance());
    }


    final WayPointList configListGui = new WayPointList(this);
    final GuiPanelScroll guiPanelScroll = new GuiPanelScroll(0, 0, 0, 0,configListGui);

    final GuiEditSettingPanel guiEditSettingPanel = new GuiEditSettingPanel();

    final MapGui map = new MapGui();

    final GuiEditSettingPanel guiMapConfig = new GuiEditSettingPanel();


    int screen = 0;


    final GuiPanelButton MapButton = new GuiPanelButton(() -> {
        screen = 0;
    }, "Maps");
    final GuiPanelButton WayPointButton = new GuiPanelButton(() -> {
        screen = 1;
    }, "Waypoints");
    final GuiPanelButton ConfigButton = new GuiPanelButton(() -> {
        screen = 2;
    }, "Config");


    final GuiPanelButton addWayPointButton = new GuiPanelButton(() -> {

        guiEditSettingPanel.setCurrentSettingsHolder(Spark.waypointManager.createWayPoint(new Vec2i((int)mc.player.posX,(int)mc.player.posZ),mc.player.dimension));
    }, "AddWaypoint");








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

        if(screen == 0)
        {
            map.setPositionAndSize(x,y,width,height);
            map.renderContent(MouseX, MouseY, deltaTime);
        }
        else if(screen == 2)
        {
            guiMapConfig.setPositionAndSize(x,y,width,height);
            guiMapConfig.renderContent(MouseX, MouseY, deltaTime);
        }
        else
        {
            Gui.drawRect(x,y,x+ListWidth,y+searchFieldHeight,guiSettings.getGuiSubPanelBackgroundColor().getRGB());

            fontManager.drawString("Waypoints",x+4,y+searchFieldHeight/2-fontManager.getTextHeight()/2, guiSettings.getContrastColor().getRGB());

            guiPanelScroll.setPositionAndSize(x,y + searchFieldHeight + guiSettings.spacing,ListWidth,height-searchFieldHeight*2-2*guiSettings.spacing);
            guiPanelScroll.drawBackGround(guiSettings.getGuiSubPanelBackgroundColor().getRGB());
            guiPanelScroll.renderContent(MouseX,MouseY,deltaTime);

            addWayPointButton.setPositionAndSize(x,y+height-searchFieldHeight,ListWidth,searchFieldHeight);
            addWayPointButton.renderContent(MouseX, MouseY, deltaTime);

            x += ListWidth + guiSettings.spacing;


            int yh = height;


            if(!Spark.waypointManager.getWayPoints().contains(guiEditSettingPanel.getCurrentSettingsHolder()))
                guiEditSettingPanel.setCurrentSettingsHolder(null);
            guiEditSettingPanel.setPositionAndSize(x,y,ListWidth,yh);
            guiEditSettingPanel.renderContent(MouseX,MouseY,deltaTime);


        }



        x = getCenterX()-width/2;
        y = getCenterY()-height/2 + height+guiSettings.spacing + searchFieldHeight + guiSettings.spacing;


        Gui.drawRect(x-4,y-4,x+width+4,y+18+4, guiSettings.getGuiMainPanelBackgroundColor().getRGB());

        int buttonWidth = (width-guiSettings.spacing*2)/3;

        MapButton.setOverrideColor(guiSettings.getGuiSubPanelBackgroundColor());
        MapButton.setPositionAndSize(x,y,buttonWidth,searchFieldHeight);
        MapButton.renderContent(MouseX,MouseY,deltaTime);


        x += buttonWidth + guiSettings.spacing;

        WayPointButton.setOverrideColor(guiSettings.getGuiSubPanelBackgroundColor());
        WayPointButton.setPositionAndSize(x,y,buttonWidth,searchFieldHeight);
        WayPointButton.renderContent(MouseX,MouseY,deltaTime);


        x += buttonWidth + guiSettings.spacing;

        ConfigButton.setOverrideColor(guiSettings.getGuiSubPanelBackgroundColor());
        ConfigButton.setPositionAndSize(x,y,buttonWidth,searchFieldHeight);
        ConfigButton.renderContent(MouseX,MouseY,deltaTime);






    }


    public void EditWaypoint(WaypointManager.Waypoint waypoint)
    {

        guiEditSettingPanel.setCurrentSettingsHolder(waypoint);
    }
}
