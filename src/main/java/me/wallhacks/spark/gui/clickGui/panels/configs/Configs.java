package me.wallhacks.spark.gui.clickGui.panels.configs;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.clickGui.ClickGuiMenuBase;
import me.wallhacks.spark.gui.clickGui.ClickGuiPanel;
import me.wallhacks.spark.gui.clickGui.panels.configs.configList.ConfigListGui;
import me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.GuiEditSettingPanel;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelButton;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelScroll;
import me.wallhacks.spark.manager.ConfigManager;
import net.minecraft.client.gui.Gui;

public class Configs extends ClickGuiPanel {

    public Configs(ClickGuiMenuBase clickGuiMenuBase) {
        super(clickGuiMenuBase);

    }

    @Override
    public void init() {
        super.init();

    }

    @Override
    public String getName() {
        return "Configs";
    }



    public final ConfigListGui configListGui = new ConfigListGui(this);
    public final GuiPanelScroll guiPanelScroll = new GuiPanelScroll(0, 0, 0, 0,configListGui);

    public final GuiEditSettingPanel guiEditSettingPanel = new GuiEditSettingPanel();

    public ConfigManager.Config copied;

    public final GuiPanelButton addButton = new GuiPanelButton(() -> {
        int i = 1;
        while(!Spark.configManager.createConfig(new ConfigManager.Config("Config"+i)))
            i++;

        guiEditSettingPanel.setCurrentSettingsHolder(Spark.configManager.getConfigs().get(Spark.configManager.getConfigs().size()-1));

    }, "Create Config");
    public final GuiPanelButton refreshButton = new GuiPanelButton(() -> {
        Spark.configManager.SaveConfigConfigs(false);
        Spark.configManager.Load();

    }, "Refresh");





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



        Gui.drawRect(x,y,x+ListWidth,y+searchFieldHeight,guiSettings.getGuiSubPanelBackgroundColor().getRGB());

        fontManager.drawString("Config List",x+4,y+searchFieldHeight/2-fontManager.getTextHeight()/2, guiSettings.getContrastColor().getRGB());







        guiPanelScroll.setPositionAndSize(x,y + searchFieldHeight + guiSettings.spacing,ListWidth,height-searchFieldHeight-guiSettings.spacing);
        guiPanelScroll.drawBackGround(guiSettings.getGuiSubPanelBackgroundColor().getRGB());
        guiPanelScroll.renderContent(MouseX,MouseY,deltaTime);


        x += ListWidth + guiSettings.spacing;


        int yh = height;


        if(!Spark.configManager.getConfigs().contains(guiEditSettingPanel.getCurrentSettingsHolder()))
            guiEditSettingPanel.setCurrentSettingsHolder(null);
        guiEditSettingPanel.setPositionAndSize(x,y,ListWidth,yh);
        guiEditSettingPanel.renderContent(MouseX,MouseY,deltaTime);







        x = getCenterX()-width/2;
        y += height+guiSettings.spacing + searchFieldHeight + guiSettings.spacing;


        Gui.drawRect(x-4,y-4,x+width+4,y+18+4, guiSettings.getGuiMainPanelBackgroundColor().getRGB());


        refreshButton.setOverrideColor(guiSettings.getGuiSubPanelBackgroundColor());
        refreshButton.setPositionAndSize(x,y,ListWidth,searchFieldHeight);
        refreshButton.renderContent(MouseX,MouseY,deltaTime);


        x += ListWidth + guiSettings.spacing;

        addButton.setOverrideColor(guiSettings.getGuiSubPanelBackgroundColor());
        addButton.setPositionAndSize(x,y,ListWidth,searchFieldHeight);
        addButton.renderContent(MouseX,MouseY,deltaTime);






    }




    public void CopyPasteConfig(ConfigManager.Config config) {
        if(copied == null)
        {
            copied = config;
        }
        else
        {
            if(config != copied)
                Spark.configManager.copyAndPasteConfig(copied,config);
            copied = null;
        }


    }

    public void LoadConfig(ConfigManager.Config config) {
        Spark.configManager.loadConfig(config,true);
    }

    public void EditConfig(ConfigManager.Config config) {
        guiEditSettingPanel.setCurrentSettingsHolder(config);
    }











}
