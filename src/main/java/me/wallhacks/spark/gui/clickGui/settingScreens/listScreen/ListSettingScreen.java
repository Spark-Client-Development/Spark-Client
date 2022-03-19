package me.wallhacks.spark.gui.clickGui.settingScreens.listScreen;

import me.wallhacks.spark.gui.clickGui.settingScreens.SettingScreen;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelInputField;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelScroll;
import me.wallhacks.spark.systems.setting.settings.ListSelectSetting;
import me.wallhacks.spark.util.GuiUtil;
import net.minecraft.util.ResourceLocation;
import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;


public class ListSettingScreen extends SettingScreen<ListSelectSetting> {
    public ListSettingScreen(ListSelectSetting setting) {
        super(setting);
    }

    GuiPanelInputField guiPanelInputField = new GuiPanelInputField(0,0,0,0,0);
    GuiPanelScroll guiPanelScroll = new GuiPanelScroll(0,0,230,200,new ListSettingGuiList(getSetting(), guiPanelInputField));

    final static ResourceLocation searchIcon = new ResourceLocation("textures/icons/searchicon.png");


    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);

        ClientConfig guiSettings =  ClientConfig.getInstance();


        guiPanelInputField.setBackGroundColor(guiSettings.getGuiSubPanelBackgroundColor().getRGB());

        int searchFieldHeight = 18;

        guiPanelInputField.setTextOffsetX(searchFieldHeight);

        guiPanelInputField.setPositionAndSize(settingPosX,settingPosY+settingHeight-searchFieldHeight,settingWidth,searchFieldHeight);
        guiPanelInputField.renderContent(MouseX, MouseY, deltaTime);
        GuiUtil.drawCompleteImage(settingPosX+3,settingPosY+settingHeight-searchFieldHeight+3, searchFieldHeight-6, searchFieldHeight-6,searchIcon, guiSettings.getContrastColor());



        guiPanelScroll.setPositionAndSize(settingPosX,settingPosY,settingWidth,settingHeight-18-ClientConfig.spacing);
        guiPanelScroll.drawBackGround(guiSettings.getGuiSubPanelBackgroundColor().getRGB());
        guiPanelScroll.renderContent(MouseX, MouseY, deltaTime);


    }


}
