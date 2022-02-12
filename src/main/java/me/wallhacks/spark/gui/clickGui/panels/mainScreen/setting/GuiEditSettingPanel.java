package me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting;

import me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.settings.*;
import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.gui.panels.GuiPanelScroll;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.*;
import me.wallhacks.spark.util.GuiUtil;
import net.minecraft.util.ResourceLocation;
import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.systems.setting.Setting;

import java.util.ArrayList;

public class GuiEditSettingPanel extends GuiPanelBase {

    public GuiEditSettingPanel() {
        super();


    }

    final static ResourceLocation settingIcon = new ResourceLocation("textures/icons/settingsicon.png");



    public SettingsHolder getCurrentSettingsHolder(){
        return currentSettingsHolder;
    }

    public void setCurrentSettingsHolder(SettingsHolder currentSettingsHolder){
        this.currentSettingsHolder = currentSettingsHolder;


        guiEditSettingPanelHolder.groups = new ArrayList<>();

        if(currentSettingsHolder != null)
        {
            ArrayList<String> groups = new ArrayList<>();
            for (Setting s : currentSettingsHolder.getSettings()) {
                if(!groups.contains(s.getCategory()))
                    groups.add(s.getCategory());
            }

            for (String group : groups) {
                ArrayList<GuiSettingPanel> settings = new ArrayList<>();
                for (Setting s : currentSettingsHolder.getSettings()) {
                    if(s.getCategory().equals(group)){


                        if(s instanceof IntSetting)
                            settings.add(new GuiIntSettingPanel((IntSetting) s));
                        if(s instanceof DoubleSetting)
                            settings.add(new GuiDoubleSettingPanel((DoubleSetting) s));
                        if(s instanceof BooleanSetting)
                            settings.add(new GuiBooleanSettingPanel((BooleanSetting) s));
                        if(s instanceof ModeSetting)
                            settings.add(new GuiEnumSettingPanel((ModeSetting) s));
                        if(s instanceof KeySetting)
                            settings.add(new GuiKeySettingPanel((KeySetting) s));
                        if(s instanceof ColorSetting)
                            settings.add(new GuiColorSettingPanel((ColorSetting) s));
                        if(s instanceof StringSetting)
                            settings.add(new GuiStringSettingPanel((StringSetting) s));
                        if(s instanceof ListSelectSetting)
                            settings.add(new GuiListSettingPanel((ListSelectSetting) s));
                    }
                }
                GuiEditSettingPanelGroup g = new GuiEditSettingPanelGroup(group,guiEditSettingPanelHolder,settings);
                g.setExtended(groups.size() <= 4);
                guiEditSettingPanelHolder.groups.add(g);
            }






        }
    }

    SettingsHolder currentSettingsHolder = null;

    GuiEditSettingPanelHolder guiEditSettingPanelHolder = new GuiEditSettingPanelHolder(this);
    GuiEditModuleSettings guiEditModuleSettings = new GuiEditModuleSettings(this);
    GuiPanelScroll guiPanelScroll = new GuiPanelScroll(posX, posY, width, height,guiEditSettingPanelHolder);


    public void renderContent(int MouseX, int MouseY, float deltaTime) {

       // super.renderContent(MouseX,MouseY,deltaTime);

        int nameBarHeight = 18;

        drawRect(posX,posY,posX+width,posY+nameBarHeight, guiSettings.getGuiSubPanelBackgroundColor().getRGB());


        if(currentSettingsHolder != null)
        {
            String s = currentSettingsHolder.getName();

            fontManager.drawString(s,posX+2+nameBarHeight,posY+nameBarHeight/2-fontManager.getTextHeight()/2, guiSettings.getContrastColor().getRGB());
        }
        GuiUtil.drawCompleteImage(posX+3,posY+3, nameBarHeight-6, nameBarHeight-6,settingIcon, guiSettings.getContrastColor());


        int Yoffset = 0;

        if(currentSettingsHolder instanceof Module)
        {
            Yoffset += guiEditModuleSettings.height + guiSettings.spacing;
            guiEditModuleSettings.setPositionAndSize(posX,posY+height-18,guiPanelScroll.width,18);

            guiEditModuleSettings.renderContent(MouseX,MouseY,deltaTime);

        }

        guiPanelScroll.setPositionAndSize(posX,posY+18+ guiSettings.spacing,width,height-18-guiSettings.spacing-Yoffset);





        guiPanelScroll.drawBackGround(guiSettings.getGuiSubPanelBackgroundColor().getRGB());
        guiPanelScroll.renderContent(MouseX,MouseY,deltaTime);

    }




}
