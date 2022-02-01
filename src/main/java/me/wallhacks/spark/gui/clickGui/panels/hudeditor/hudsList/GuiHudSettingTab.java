package me.wallhacks.spark.gui.clickGui.panels.hudeditor.hudsList;

import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.gui.panels.GuiPanelButton;
import me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.GuiEditSettingPanel;
import me.wallhacks.spark.systems.clientsetting.clientsettings.GuiSettings;
import me.wallhacks.spark.systems.clientsetting.clientsettings.HudSettings;

public class GuiHudSettingTab extends GuiPanelBase {

    int moduleListWidth = 96;
    int settingsWidth = 160;

    public GuiHudSettingTab() {
        super();

        height = 238+GuiSettings.getInstance().spacing*3;
        width = moduleListWidth + settingsWidth + GuiSettings.getInstance().spacing*3;



    }


    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {


        super.renderContent(MouseX, MouseY, deltaTime);




        drawBackGround(guiSettings.getGuiScreenBackgroundColor().getRGB());
        drawBackGround(guiSettings.getGuiMainPanelBackgroundColor().getRGB());

        drawEdges(guiSettings.getContrastColor().getRGB());


        int spacing = GuiSettings.getInstance().spacing;



        int nameBarHeight = 18;




        guiSystemManagerPanel.setPositionAndSize(posX+spacing,posY+height-spacing-nameBarHeight,width-spacing*2,nameBarHeight);
        guiSystemManagerPanel.renderContent(MouseX,MouseY,deltaTime);

        int height = this.height-spacing*2-spacing-nameBarHeight;


        guiHudPanel.setPositionAndSize(posX+spacing,posY+spacing,moduleListWidth,height);
        guiHudPanel.renderContent(MouseX,MouseY,deltaTime);


        guiEditSettingPanel.setPositionAndSize(posX+spacing+moduleListWidth+ spacing,posY+spacing,settingsWidth,height);
        guiEditSettingPanel.renderContent(MouseX,MouseY,deltaTime);
    }

    public GuiEditSettingPanel guiEditSettingPanel = new GuiEditSettingPanel();
    public GuiHudPanel guiHudPanel = new GuiHudPanel(0,0,0,0);



    public GuiPanelButton guiSystemManagerPanel = new GuiPanelButton(() -> {
        guiEditSettingPanel.setCurrentSettingsHolder(HudSettings.getInstance());}, HudSettings.getInstance().getName());


    int offsetX = 0;
    int offsetY = 0;
    @Override
    public void onClickDown(int MouseButton, int MouseX, int MouseY) {
        super.onClickDown(MouseButton, MouseX, MouseY);
        offsetX = MouseX-posX;
        offsetY = MouseY-posY;
    }


    @Override
    public void onClick(int MouseButton, int MouseX, int MouseY) {
        super.onClick(MouseButton, MouseX, MouseY);

        posX = MouseX-offsetX;
        posY = MouseY-offsetY;
    }
}
