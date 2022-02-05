package me.wallhacks.spark.gui.clickGui.settingScreens;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.panels.GuiPanelButton;
import me.wallhacks.spark.gui.panels.GuiPanelScreen;
import me.wallhacks.spark.systems.setting.Setting;
import me.wallhacks.spark.util.GuiUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;

public class SettingScreen<T extends Setting> extends GuiPanelScreen {
    public SettingScreen(T setting) {
        super(Spark.clickGuiScreen);

        this.setting = setting;
    }

    final static ResourceLocation settingIcon = new ResourceLocation("textures/icons/settingsicon.png");

    protected int settingPosX = 0;
    protected int settingPosY = 0;
    protected int settingHeight = 230;
    protected int settingWidth = 200;

    GuiPanelButton backButton = new GuiPanelButton(() -> {close();},"Back");

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);


        ClientConfig guiSettings =  ClientConfig.getInstance();


        drawRect(0,0,this.width,this.height, guiSettings.getGuiScreenBackgroundColor().getRGB());

        int nameBarHeight = 18;
        int width = settingWidth;
        int height = settingHeight+ClientConfig.spacing+nameBarHeight;


        int x = getCenterX()-width/2;
        int y = getCenterY()-height/2;





        Gui.drawRect(x-4,y-4,x+width+4,y+height+4, guiSettings.getGuiMainPanelBackgroundColor().getRGB());


        drawRect(x,y,x+width,y+nameBarHeight, guiSettings.getGuiSubPanelBackgroundColor().getRGB());



        fontManager.drawString(setting.getSettingsHolder().getName()+" - "+setting.getName(),x+2+nameBarHeight,y+nameBarHeight/2-fontManager.getTextHeight()/2, guiSettings.getContrastColor().getRGB());

        GuiUtil.drawCompleteImage(x+3,y+3, nameBarHeight-6, nameBarHeight-6,settingIcon, guiSettings.getContrastColor());


        settingPosX = x;
        settingPosY = y+ClientConfig.spacing+nameBarHeight;




        y += height + 20;


        Gui.drawRect(x-4,y-4,x+width+4,y+18+4, guiSettings.getGuiMainPanelBackgroundColor().getRGB());

        backButton.setPositionAndSize(x,y,width,18);
        backButton.renderContent(MouseX, MouseY, deltaTime);

    }


    public void renderSetting(int MouseX, int MouseY, float deltaTime) {



    }

    final T setting;

    public T getSetting(){
        return setting;
    }
}
