package me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.settings;

import me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.GuiSettingPanel;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;

public class GuiBooleanSettingPanel extends GuiSettingPanel<BooleanSetting> {

    public GuiBooleanSettingPanel(BooleanSetting setting) {
        super(setting);
    }



    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);




        fontManager.drawString(getSetting().getName(),posX,posY+4, guiSettings.getContrastColor().getRGB());




        if(guiSettings.getToggleSliders())
        {
            int FieldSizeX = 20;
            int FieldSizeY = 10;

            int y = posY+4+ fontManager.getTextHeight()/2-FieldSizeY/2;

            int x = posX + width - FieldSizeX;

            drawRect(x,y,x+FieldSizeX,y+FieldSizeY, getSetting().isOn() ? guiSettings.getMainColor().getRGB() : guiSettings.getGuiSettingFieldColor().getRGB());
            if (getSetting().isOn()) {
                drawRect(x + 8,y + 2,x+FieldSizeX - 2,y+FieldSizeY -2, guiSettings.getContrastColor().getRGB());
            } else {
                drawRect(x + 2,y + 2,x+FieldSizeX - 8,y+FieldSizeY -2, guiSettings.getContrastColor().getRGB());
            }
        }
        else{
            String s = getSetting().isOn() ? "On" : "Off";



            int spacing = 2;
            int FieldSizeX = fontManager.getTextWidth(s) + spacing*2;
            int FieldSizeY = 14;


            int y = posY+4+ fontManager.getTextHeight()/2-FieldSizeY/2;

            int x = posX + width - FieldSizeX;

            drawRect(x,y,x+FieldSizeX,y+FieldSizeY, guiSettings.getGuiSettingFieldColor().getRGB());



            fontManager.drawString(s,x+spacing,y+FieldSizeY/2-fontManager.getTextHeight()/2,getSetting().isOn() ? guiSettings.getMainColor().getRGB() : guiSettings.getContrastColor().getRGB());

        }



        height = 6 +  fontManager.getTextHeight();


    }

    @Override
    public void onClickDown(int MouseButton, int MouseX, int MouseY) {
        super.onClickDown(MouseButton, MouseX, MouseY);

        getSetting().toggle();
    }
}
