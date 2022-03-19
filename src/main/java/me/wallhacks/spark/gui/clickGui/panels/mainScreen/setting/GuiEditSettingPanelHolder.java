package me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting;

import me.wallhacks.spark.gui.dvdpanels.GuiPanelBase;

import java.util.ArrayList;

public class GuiEditSettingPanelHolder extends GuiPanelBase {

    public GuiEditSettingPanelHolder(GuiEditSettingPanel guiEditSettingPanel) {
        super(0, 0, 0, 0);

        this.guiEditSettingPanel = guiEditSettingPanel;


    }

    final GuiEditSettingPanel guiEditSettingPanel;

    ArrayList<GuiEditSettingPanelGroup> groups = new ArrayList<>();

    public void renderContent(int MouseX, int MouseY, float deltaTime) {

        super.renderContent(MouseX,MouseY,deltaTime);

        if(guiEditSettingPanel.getCurrentSettingsHolder() != null)
        {

            int h = 12;

            for (GuiEditSettingPanelGroup panel : groups) {
                panel.setPositionAndSize(posX,posY+h,width,panel.height);
                panel.renderContent(MouseX,MouseY,deltaTime);
                h+= panel.height;
            }

            height = h + 50;







        }
    }
}
