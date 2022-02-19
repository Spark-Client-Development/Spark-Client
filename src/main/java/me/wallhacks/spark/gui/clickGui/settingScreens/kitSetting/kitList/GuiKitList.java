package me.wallhacks.spark.gui.clickGui.settingScreens.kitSetting.kitList;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.clickGui.panels.configs.configList.ConfigListItem;
import me.wallhacks.spark.gui.clickGui.settingScreens.kitSetting.KitSettingGui;
import me.wallhacks.spark.gui.clickGui.settingScreens.listScreen.ListSettingGuiItem;
import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.gui.panels.GuiPanelInputField;
import me.wallhacks.spark.manager.ConfigManager;
import me.wallhacks.spark.systems.module.modules.misc.InventoryManager;
import me.wallhacks.spark.systems.setting.settings.ListSelectSetting;
import me.wallhacks.spark.util.GuiUtil;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class GuiKitList extends GuiPanelBase {

    final KitSettingGui kitSettingGui;
    public GuiKitList(KitSettingGui kitSettingGui) {

        this.kitSettingGui = kitSettingGui;


    }
    ArrayList<GuiKitItem> guiLists = new ArrayList<GuiKitItem>();

    void refresh(){
        ArrayList<String> p = new ArrayList<>( InventoryManager.instance.getKitNames());



        for (int i = guiLists.size()-1; i >= 0; i--) {

            if(p.contains(guiLists.get(i).selection))
                p.remove(guiLists.get(i).selection);
            else
                guiLists.remove(i);
        }

        for (String c : p) {
            guiLists.add(new GuiKitItem(this,c));
        }
    }



    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);

        refresh();

        int h = guiSettings.spacing;

        for (GuiKitItem sels : guiLists) {

            sels.posY = posY+h;
            sels.width = width-guiSettings.spacing*2;
            sels.posX = posX+guiSettings.spacing;
            sels.renderContent(MouseX, MouseY, deltaTime);


            h += 18 + 2;



        }

        height = h + 20;

    }
}
