package me.wallhacks.spark.gui.clickGui.panels.hudeditor.hudsList;

import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.manager.SystemManager;
import me.wallhacks.spark.systems.hud.HudElement;

import java.util.ArrayList;

public class GuiHudListPanel extends GuiPanelBase {
    public GuiHudListPanel(GuiHudPanel guiHudPanel) {
        super(0, 0, 200, 200);

        for (HudElement hud : SystemManager.getHudModules()) {
            huds.add(new GuiHudModulePanel(hud));
        }

        this.guiHudPanel = guiHudPanel;
    }

    public final GuiHudPanel guiHudPanel;




    ArrayList<GuiHudModulePanel> huds = new ArrayList<GuiHudModulePanel>();

    public void renderContent(int MouseX, int MouseY, float deltaTime)
    {

        if(true) {

            int spacing = guiSettings.spacing;

            int y = posY + spacing;

            int moduleHeight = 15;

            for (GuiHudModulePanel m : huds) {

                if(m.hud.getName().toLowerCase().contains(guiHudPanel.moduleSearchField.getText().toLowerCase())) {
                    m.setPositionAndSize(posX+spacing,y,width-spacing*2, moduleHeight);
                    m.renderContent(MouseX,MouseY,deltaTime);



                    y += m.height + spacing;

                }





            }

            height = y+20;

        }


    }


}
