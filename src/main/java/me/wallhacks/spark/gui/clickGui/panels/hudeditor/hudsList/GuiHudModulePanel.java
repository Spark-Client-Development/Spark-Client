package me.wallhacks.spark.gui.clickGui.panels.hudeditor.hudsList;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.clickGui.panels.hudeditor.HudEditor;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelBase;
import me.wallhacks.spark.systems.hud.HudElement;

import java.awt.*;

public class GuiHudModulePanel extends GuiPanelBase {

    public final HudElement hud;

    public GuiHudModulePanel(HudElement hud) {
        super(0, 0, 0, 0);
        this.hud = hud;
    }



    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);


        drawBackGround((hud.isEnabled() ? guiSettings.getMainColor() : guiSettings.getGuiSettingFieldColor()).getRGB());


        Color c = isMouseOn ? guiSettings.getContrastColor().brighter() : guiSettings.getContrastColor();

        fontManager.drawString(hud.getName(),posX+4,posY+height/2-(fontManager.getTextHeight()/2-1),c.getRGB());
    }

    @Override
    public void onClickDown(int Mouse, int MouseX, int MouseY) {
        super.onClickDown(Mouse, MouseX, MouseY);
        if(Mouse == 0)
            hud.toggle();
        else if(Mouse == 1)
            Spark.clickGuiScreen.getPanelOfType(HudEditor.class).guiHudSettingTab.guiEditSettingPanel.setCurrentSettingsHolder(hud);
    }
}
