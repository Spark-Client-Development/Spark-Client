package me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting;

import me.wallhacks.spark.gui.panels.GuiPanelBase;

import java.util.List;

public class GuiEditSettingPanelGroup extends GuiPanelBase {

    public GuiEditSettingPanelGroup(String name,GuiEditSettingPanelHolder guiEditSettingPanelHolder,List<GuiSettingPanel> settings) {
        super(0, 0, 0, 0);

        this.name = name;
        this.guiEditSettingPanelHolder = guiEditSettingPanelHolder;
        this.settings = settings;

    }
    final GuiEditSettingPanelHolder guiEditSettingPanelHolder;
    final List<GuiSettingPanel> settings;
    final String name;



    boolean isExtended = true;

    public void setExtended(boolean extended) {
        isExtended = extended;
    }

    public boolean isExtended() {
        return isExtended;
    }

    public void renderContent(int MouseX, int MouseY, float deltaTime) {

        super.renderContent(MouseX,MouseY,deltaTime);

        int h = 0;


        int settingsShow = 0;
        for (GuiSettingPanel panel : settings)
        {
            if(panel.getSetting().isVisible())
                settingsShow++;
        }

        if(settingsShow == 0){
            height = 0;
            return;
        }


        if(guiEditSettingPanelHolder.groups.size() > 1)
        {
            int color = guiSettings.getContrastColor().getRGB();

            fontManager.drawString(name,posX+width/2-fontManager.getTextWidth(name)/2,posY+h,color);
            h+= fontManager.getTextHeight();
            drawHorizontalLine(posX+6,posX+width-6,posY+h,color);
            h+= 8;
        }
        else if(!isExtended)
            isExtended = true;


        if(isExtended)
        {
            for (GuiSettingPanel panel : settings) {
                if(panel.getSetting().isVisible()){

                    panel.setPositionAndSize(posX+6,posY+h,width-12-2,panel.height);
                    panel.renderContent(MouseX,MouseY,deltaTime);

                    h+= panel.height + 8;
                }


            }
            h+= 3;
        }
        h+= 5;


        height = h;
    }

    @Override
    public void onClickDown(int MouseButton, int MouseX, int MouseY) {
        super.onClickDown(MouseButton, MouseX, MouseY);

        if(guiEditSettingPanelHolder.groups.size() > 1)
        if(MouseY < posY + fontManager.getTextHeight()+2)
            isExtended = !isExtended;
    }
}
