package me.wallhacks.spark.gui.clickGui.panels.configs.configList;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.clickGui.panels.socials.Socials;
import me.wallhacks.spark.gui.clickGui.panels.socials.playerLists.PlayerListItem;
import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.gui.panels.GuiPanelInputField;
import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;

import java.util.ArrayList;

public class ConfigListGui extends GuiPanelBase {

    public final GuiPanelInputField moduleSearchField = new GuiPanelInputField(0,0,0,0,0);


    public ArrayList<ConfigListItem> configListItems = new ArrayList<>();



    public void renderContent(int MouseX, int MouseY, float deltaTime) {


        super.renderContent(MouseX,MouseY,deltaTime);



        int spacing = ClientConfig.spacing;

        int h = spacing;
        for (ConfigListItem item : configListItems) {


            item.setPositionAndSize(posX+spacing,posY+h,width-spacing*2,18);
            item.renderContent(MouseX,MouseY,deltaTime);

            h += 18 + 2;
        }
        h+=20;

        height = h;





    }









}
