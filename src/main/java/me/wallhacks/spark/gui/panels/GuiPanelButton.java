package me.wallhacks.spark.gui.panels;

import java.awt.*;

public class GuiPanelButton extends GuiPanelBase {

    final Runnable action;

    public GuiPanelButton(Runnable action, String text) {
        super();
        this.action = action;

        this.text = text;
    }
    String text;

    public String getText() {
        return text;
    }

    Color overrideColor = null;

    public void setOverrideColor(Color overrideColor) {
        this.overrideColor = overrideColor;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean drawLine = false;




    @Override
    public void onClickDown(int MouseButton, int MouseX, int MouseY) {
        super.onClickDown(MouseButton, MouseX, MouseY);
        action.run();
    }

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);

        drawBackGround(overrideColor == null ? guiSettings.getGuiSubPanelBackgroundColor().getRGB() : overrideColor.getRGB());


        Color c = isMouseOn ? guiSettings.getContrastColor().brighter() : guiSettings.getContrastColor();

        int sw = fontManager.getTextWidth(text);
        fontManager.drawString(text,posX+width/2-sw/2,posY+height/2-(fontManager.getTextHeight()/2-1),c.getRGB());

        if(drawLine)
        {
            drawHorizontalLine(posX+width/2-(sw/2+3),posX+width/2+(sw/2+3),posY+height/2+fontManager.getTextHeight()/2+2,c.getRGB());
        }

    }
}
