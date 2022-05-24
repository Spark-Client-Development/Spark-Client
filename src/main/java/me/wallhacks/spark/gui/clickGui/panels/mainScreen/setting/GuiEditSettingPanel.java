package me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.settings.*;
import me.wallhacks.spark.gui.clickGui.settingScreens.kitSetting.KitSettingGui;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelBase;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelButton;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelScroll;
import me.wallhacks.spark.manager.ConfigManager;
import me.wallhacks.spark.manager.WaypointManager;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.module.modules.misc.InventoryManager;
import me.wallhacks.spark.systems.setting.settings.*;
import me.wallhacks.spark.util.GuiUtil;
import net.minecraft.util.ResourceLocation;
import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.systems.setting.Setting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

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
                        if(s instanceof VectorSetting)
                            settings.add(new GuiVectorSettingPanel((VectorSetting) s));
                    }
                }
                GuiEditSettingPanelGroup g = new GuiEditSettingPanelGroup(group,guiEditSettingPanelHolder,settings);
                g.setExtended(groups.size() <= 4);
                guiEditSettingPanelHolder.groups.add(g);
            }


            buttonFunction = null;
            if(currentSettingsHolder instanceof InventoryManager)
            {
                buttonFunction = buttonFunction = new GuiPanelButton[] {
                        new GuiPanelButton(() -> {
                    mc.displayGuiScreen(new KitSettingGui(Spark.clickGuiScreen));
                },"Kit Editor")};
            }
            if(currentSettingsHolder instanceof HudElement)
            {
                buttonFunction = new GuiPanelButton[] {
                        new GuiPanelButton(() -> {
                            HudElement h = (HudElement)currentSettingsHolder;
                            h.resetPos();
                        },"Reset Location")
                };

            }
            if(currentSettingsHolder instanceof WaypointManager.Waypoint)
            {
                buttonFunction = new GuiPanelButton[] {
                        new GuiPanelButton(() -> {
                            WaypointManager.Waypoint waypoint = (WaypointManager.Waypoint) currentSettingsHolder;

                            Spark.waypointManager.getWayPoints().remove(waypoint);
                        }, "Delete"),

                };

            }

        }
    }

    SettingsHolder currentSettingsHolder = null;

    GuiEditSettingPanelHolder guiEditSettingPanelHolder = new GuiEditSettingPanelHolder(this);
    GuiEditModuleSettings guiEditModuleSettings = new GuiEditModuleSettings(this);
    GuiPanelScroll guiPanelScroll = new GuiPanelScroll(posX, posY, width, height,guiEditSettingPanelHolder);

    GuiPanelButton[] buttonFunction;

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




            guiEditModuleSettings.setPositionAndSize(posX,posY+height-18,guiPanelScroll.width,18);
            Yoffset += guiEditModuleSettings.height + guiSettings.spacing;
            guiEditModuleSettings.renderContent(MouseX,MouseY,deltaTime);


        }

        if(buttonFunction != null && buttonFunction.length > 0)
        {


            int w = guiPanelScroll.width/buttonFunction.length-(guiSettings.spacing*(buttonFunction.length-1)/2);
            for (int i = 0; i < buttonFunction.length; i++) {
                buttonFunction[i].setPositionAndSize(posX+i*(w+guiSettings.spacing),posY+height-Yoffset-18,w,18);

                buttonFunction[i].renderContent(MouseX,MouseY,deltaTime);
            }
            Yoffset += buttonFunction[0].height + guiSettings.spacing;
        }

        guiPanelScroll.setPositionAndSize(posX,posY+18+ guiSettings.spacing,width,height-18-guiSettings.spacing-Yoffset);





        guiPanelScroll.drawBackGround(guiSettings.getGuiSubPanelBackgroundColor().getRGB());
        guiPanelScroll.renderContent(MouseX,MouseY,deltaTime);

    }




}
