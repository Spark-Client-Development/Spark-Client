package me.wallhacks.spark.gui.clickGui.panels.mainScreen.moduleslist;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.clickGui.panels.mainScreen.SystemsScreen;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelBase;
import me.wallhacks.spark.systems.module.Module;

import java.awt.*;

public class GuiModulePanel extends GuiPanelBase {

    public final Module module;

    public GuiModulePanel(Module module) {
        super(0, 0, 0, 0);

        this.module = module;
    }



    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);


        drawBackGround((module.isEnabled() ? guiSettings.getMainColor() : guiSettings.getGuiSettingFieldColor()).getRGB());


        Color c = isMouseOn ? guiSettings.getContrastColor().brighter() : guiSettings.getContrastColor();

        fontManager.drawString(module.getName(),posX+4,posY+height/2-(fontManager.getTextHeight()/2-1),c.getRGB());
    }

    @Override
    public void onClickDown(int Mouse, int MouseX, int MouseY) {
        super.onClickDown(Mouse, MouseX, MouseY);
        if(Mouse == 0)
            module.toggle();
        else if(Mouse == 1)
            Spark.clickGuiScreen.getPanelOfType(SystemsScreen.class).guiEditSettingPanel.setCurrentSettingsHolder(module);
    }
}
