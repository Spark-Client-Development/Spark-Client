package me.wallhacks.spark.gui.panels;

import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;
import me.wallhacks.spark.util.GuiUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import me.wallhacks.spark.util.MathUtil;

public class GuiPanelScroll extends GuiPanelBase {

    double scroll = 0;
    double smoothScroll = 0;



    final GuiPanelBase content;
    final GuiPanelBase scrollButton = new GuiPanelBase();

    public boolean controlWidth = true;
    public boolean hasScrollBar = true;

    public double getScroll() {
        return scroll;
    }

    public void setScroll(double scroll) {
        this.scroll = scroll;
    }

    public GuiPanelScroll(int posX, int posY, int heightX, int heightY,GuiPanelBase content) {
        super(posX, posY, heightX, heightY);
        this.content = content;

    }
    public GuiPanelScroll(GuiPanelBase content) {
        super(0, 0, 10, 10);
        this.content = content;

    }



    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX,MouseY,deltaTime);

        if(true) {
            //check if Mouse is outside of bounds
            //if thats the case we set it to fucking high numbers to make it not mess with shit
            if(isMouseOn)
                scroll = (-(Mouse.getDWheel()*0.3) + scroll); //no need for delta time here Mouse.getDWheel() takes care of it
            else
                GuiPanelBase.mouseCantBeOn = true;

            scroll = Math.max(0, Math.min(scroll, content.height-height));

            smoothScroll = MathUtil.lerp(smoothScroll,scroll,deltaTime*0.02);



            if(hasScrollBar && (content.height > height))
            {
                scrollButton.width = 8;
                scrollButton.height = height;
                scrollButton.posY = posY;
                scrollButton.posX = posX+width-8;
                scrollButton.renderContent(MouseX, MouseY, deltaTime);



                int h = (int) (height * (height/Math.max(height,content.height+0.01)));

                if(scrollButton.isSelected())
                    scroll = (MouseY-posY-h/2)/(double)(height-h)*( content.height-height);

                int st = (int)((height-h)/Math.max(0.01,content.height-height)*smoothScroll);

                drawQuad(posX+width-8, posY+2+st, 8-2, h-4, ClientConfig.getInstance().getGuiSettingFieldColor().getRGB());
            }



            GL11.glPushMatrix();


            GuiUtil.glScissor(this.posX, this.posY, (this.width), height);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            //scroll view

            content.posX = this.posX;
            content.posY = this.posY;

            GuiUtil.addGlScissorOffset(new Vec3d(0,-smoothScroll,0));
            GL11.glTranslated(0,-smoothScroll,0);






            if(controlWidth)
                content.width = width + ((hasScrollBar && (content.height > height)) ? -8 : 0);



            content.renderContent(MouseX, MouseY+(int)smoothScroll, deltaTime);


            GL11.glDisable(GL11.GL_SCISSOR_TEST);

            GuiUtil.addGlScissorOffset(new Vec3d(0,smoothScroll,0));
            GL11.glPopMatrix();

            GuiPanelBase.mouseCantBeOn = false;

        }
    }
}
