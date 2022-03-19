package me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.settings;

import me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.GuiSettingPanel;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelInputField;
import me.wallhacks.spark.systems.setting.settings.VectorSetting;
import net.minecraft.util.math.Vec3i;

public class GuiVectorSettingPanel extends GuiSettingPanel<VectorSetting> {

    public GuiVectorSettingPanel(VectorSetting setting) {
        super(setting);

        guiPanelInputFieldX = new GuiPanelInputField(0,0,0,0,0);
        guiPanelInputFieldX.setText(setting.getValue().getX()+"");

        guiPanelInputFieldY = new GuiPanelInputField(0,0,0,0,0);
        guiPanelInputFieldY.setText(setting.getValue().getY()+"");

        guiPanelInputFieldZ = new GuiPanelInputField(0,0,0,0,0);
        guiPanelInputFieldZ.setText(setting.getValue().getZ()+"");


    }

    final GuiPanelInputField guiPanelInputFieldX;
    final GuiPanelInputField guiPanelInputFieldY;
    final GuiPanelInputField guiPanelInputFieldZ;

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);



        if(guiPanelInputFieldX.isFocused())
            try {
                String s = guiPanelInputFieldX.getText();
                if(s.endsWith("."))s=s+"0";
                if(s.equals(""))s = "0";
                if(s.equals("-"))s = "-1";
                int parse = Integer.parseInt(s);
                if(parse != getSetting().getValue().getX())
                    getSetting().setValue(new Vec3i(parse,getSetting().getValue().getY(),getSetting().getValue().getZ()));
            }
            catch (NumberFormatException e)
            {
                guiPanelInputFieldX.setText(getSetting().getValue().getX()+"");
            }
        else
            guiPanelInputFieldX.setText(getSetting().getValue().getX()+"");

        if(guiPanelInputFieldY.isFocused())
            try {
                String s = guiPanelInputFieldY.getText();
                if(s.endsWith("."))s=s+"0";
                if(s.equals(""))s = "0";
                if(s.equals("-"))s = "-1";
                int parse = Integer.parseInt(s);
                if(parse != getSetting().getValue().getY())
                    getSetting().setValue(new Vec3i(getSetting().getValue().getX(),parse,getSetting().getValue().getZ()));
            }
            catch (NumberFormatException e)
            {
                guiPanelInputFieldY.setText(getSetting().getValue().getY()+"");
            }
        else
            guiPanelInputFieldY.setText(getSetting().getValue().getY()+"");

        if(guiPanelInputFieldZ.isFocused())
            try {
                String s = guiPanelInputFieldZ.getText();
                if(s.endsWith("."))s=s+"0";
                if(s.equals(""))s = "0";
                if(s.equals("-"))s = "-1";
                int parse = Integer.parseInt(s);
                if(parse != getSetting().getValue().getZ())
                    getSetting().setValue(new Vec3i(getSetting().getValue().getX(),getSetting().getValue().getY(),parse));
            }
            catch (NumberFormatException e)
            {
                guiPanelInputFieldZ.setText(getSetting().getValue().getZ()+"");
            }
        else
            guiPanelInputFieldZ.setText(getSetting().getValue().getZ()+"");



        int inputFieldWidth = 45;
        int inputFieldHeight = 14;

        fontManager.drawString(getSetting().getName(),posX,posY+4, guiSettings.getContrastColor().getRGB());


        int x = 0;

        guiPanelInputFieldX.setBackGroundColor(guiSettings.getGuiSettingFieldColor().getRGB());
        guiPanelInputFieldX.setPositionAndSize(posX+x,posY+4+ 1 +  fontManager.getTextHeight(),inputFieldWidth,inputFieldHeight);
        guiPanelInputFieldX.renderContent(MouseX, MouseY, deltaTime);

        x += inputFieldWidth+4;

        if(getSetting().hasY())
        {
            guiPanelInputFieldY.setBackGroundColor(guiSettings.getGuiSettingFieldColor().getRGB());
            guiPanelInputFieldY.setPositionAndSize(posX+x,posY+4+ 1 +  fontManager.getTextHeight(),inputFieldWidth,inputFieldHeight);
            guiPanelInputFieldY.renderContent(MouseX, MouseY, deltaTime);

            x += inputFieldWidth+4;
        }

        guiPanelInputFieldZ.setBackGroundColor(guiSettings.getGuiSettingFieldColor().getRGB());
        guiPanelInputFieldZ.setPositionAndSize(posX+x,posY+4+ 1 +  fontManager.getTextHeight(),inputFieldWidth,inputFieldHeight);
        guiPanelInputFieldZ.renderContent(MouseX, MouseY, deltaTime);

        x += inputFieldWidth+4;





        height = inputFieldHeight + 4 +  fontManager.getTextHeight();


    }


}
