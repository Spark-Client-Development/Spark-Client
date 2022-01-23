package me.wallhacks.spark.gui.panels;

import java.awt.*;

public class GuiPanelButton extends GuiPanelBase {

    public GuiPanelButton(int id,String text) {
        super();
        this.id = id;
        this.text = text;
    }
    int id = 0;
    String text;

    Color overrideColor = null;

    public void setOverrideColor(Color overrideColor) {
        this.overrideColor = overrideColor;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean drawLine = false;


    public int getId() {
        return id;
    }

    @Override
    public void onClickDown(int MouseButton, int MouseX, int MouseY) {
        super.onClickDown(MouseButton, MouseX, MouseY);
    }

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);

        drawBackGround(overrideColor == null ? guiSettings.getGuiMainPanelBackgroundColor().getRGB() : overrideColor.getRGB());


        Color c = isMouseOn ? guiSettings.getContrastColor().brighter() : guiSettings.getContrastColor();

        int sw = fontManager.getTextWidth(text);
        fontManager.drawString(text,posX+width/2-sw/2,posY+height/2-(fontManager.getTextHeight()/2-1),c.getRGB());

        if(drawLine)
        {
            drawHorizontalLine(posX+width/2-(sw/2+3),posX+width/2+(sw/2+3),posY+height/2+fontManager.getTextHeight()/2+2,c.getRGB());
        }

    }
}
