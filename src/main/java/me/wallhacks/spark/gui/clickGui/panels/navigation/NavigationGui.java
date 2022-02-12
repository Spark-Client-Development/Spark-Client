package me.wallhacks.spark.gui.clickGui.panels.navigation;

import me.wallhacks.spark.gui.clickGui.ClickGuiMenuBase;
import me.wallhacks.spark.gui.clickGui.ClickGuiPanel;
import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.MC;

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

        mapGui.resetValues();
    }

    MapGui mapGui = new MapGui();

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {


        GuiPanelBase.drawRect(getWidth()/2-400/2-4,getHeight()/2-140-4,getWidth()/2+400/2+4,getHeight()/2+140+4, guiSettings.getGuiMainPanelBackgroundColor().getRGB());

        mapGui.setPositionAndSize(getWidth()/2-400/2,getHeight()/2-140,400,280);



        mapGui.renderContent(MouseX, MouseY, deltaTime);

    }



}
