package me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.settings;

import me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.GuiSettingPanel;
import me.wallhacks.spark.util.GuiUtil;
import net.minecraft.util.ResourceLocation;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;

public class GuiEnumSettingPanel extends GuiSettingPanel<ModeSetting> {

    public GuiEnumSettingPanel(ModeSetting setting) {
        super(setting);

        isExtended = false;
    }

    boolean isExtended;
    final static ResourceLocation settingIcon = new ResourceLocation("textures/icons/arrowicon.png");

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);

        String selected = getSetting().getValueName();


        int spacing = 2;
        int FieldSizeX = fontManager.getTextWidth(selected) + spacing*2;
        int FieldSizeY = 14;

        int xoffset = fontManager.drawString(getSetting().getName(), posX, posY+4, guiSettings.getContrastColor().getRGB());

        int y = posY+4 + fontManager.getTextHeight() / 2 - FieldSizeY / 2;
        int x = posX + width - FieldSizeX;






        if(guiSettings.getArrowMode() == 2) {
            x-= 10;

            drawRect(x, y, x + FieldSizeX+10, y + FieldSizeY, guiSettings.getGuiSettingFieldColor().getRGB());





            fontManager.drawString(selected, x+spacing, y + FieldSizeY / 2 - fontManager.getTextHeight() / 2, guiSettings.getContrastColor().getRGB());


            if(guiSettings.getArrowMode() != 0)
                GuiUtil.drawCompleteImageRotated(posX+width-2-6, posY+4+fontManager.getTextHeight()/2-3, 6, 6, isExtended ? 90 : 0, settingIcon, guiSettings.getContrastColor());

        }
        else {


            drawRect(x,y,x+FieldSizeX,y+FieldSizeY, guiSettings.getGuiSettingFieldColor().getRGB());

            fontManager.drawString(selected, x+spacing, y + FieldSizeY / 2 - fontManager.getTextHeight() / 2, guiSettings.getContrastColor().getRGB());



            if(guiSettings.getArrowMode() != 0)
                GuiUtil.drawCompleteImageRotated(xoffset+4,posY+4+fontManager.getTextHeight()/2-4/2,4,4,isExtended ? 90 : 0,settingIcon, guiSettings.getContrastColor());

        }



        if (isExtended) {
            y+=1;
            for (String mode : getSetting().getModes()) {
                y += FieldSizeY;

                FieldSizeX = fontManager.getTextWidth(mode) + spacing*2;

                x = posX + width - FieldSizeX;
                drawRect(x, y, x + FieldSizeX, y + FieldSizeY, guiSettings.getGuiSettingFieldColor().getRGB());
                fontManager.drawString(mode, x+spacing, y + FieldSizeY/2 - fontManager.getTextHeight()/2, guiSettings.getContrastColor().getRGB());


            }

        }
        height = y - posY+6 + FieldSizeY;
    }

    @Override
    public void onClickDown(int MouseButton, int MouseX, int MouseY) {
        super.onClickDown(MouseButton, MouseX, MouseY);

        if(MouseButton == 1)
        {
            isExtended = !isExtended;
        }
        else {

            int y = 14;

            if(MouseY < posY+4+y)
                getSetting().increment();
            else if(isExtended){
                for (String mode : getSetting().getModes()){
                    y+=14;
                    if(MouseY < posY+4+y){
                        getSetting().setValueString(mode);
                        return;
                    }
                }
            }


        }
    }
}
