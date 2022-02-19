package me.wallhacks.spark.gui.clickGui.panels.configs.configList;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.clickGui.panels.configs.Configs;
import me.wallhacks.spark.gui.clickGui.panels.navigation.waypointlist.WayPointItem;
import me.wallhacks.spark.gui.clickGui.panels.socials.Socials;
import me.wallhacks.spark.gui.clickGui.panels.socials.playerLists.PlayerListItem;
import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.gui.panels.GuiPanelInputField;
import me.wallhacks.spark.manager.ConfigManager;
import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;

import java.util.ArrayList;

public class ConfigListGui extends GuiPanelBase {


    ArrayList<ConfigListItem> configListItems = new ArrayList<>();

    Configs configs;

    public ConfigListGui(Configs configs) {
        this.configs = configs;
    }


    public void renderContent(int MouseX, int MouseY, float deltaTime) {


        super.renderContent(MouseX,MouseY,deltaTime);

        RefreshList();

        int spacing = guiSettings.spacing;

        int h = spacing;
        for (ConfigListItem item : configListItems) {


            item.setPositionAndSize(posX+spacing,posY+h,width-spacing*2,18);
            item.renderContent(MouseX,MouseY,deltaTime);

            h += 18 + 2;
        }
        h+=20;

        height = h;





    }



    void RefreshList() {
        ArrayList<ConfigManager.Config> p = new ArrayList<>( Spark.configManager.getConfigs());



        for (int i = configListItems.size()-1; i >= 0; i--) {

            if(p.contains(configListItems.get(i).config))
                p.remove(configListItems.get(i).config);
            else
                configListItems.remove(i);
        }

        for (ConfigManager.Config c : p) {
            configListItems.add(new ConfigListItem(c,configs));
        }
    }





}
