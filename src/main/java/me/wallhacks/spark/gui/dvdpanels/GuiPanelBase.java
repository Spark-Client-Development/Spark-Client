package me.wallhacks.spark.gui.dvdpanels;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import me.wallhacks.spark.manager.FontManager;

public class GuiPanelBase extends Gui {

    protected final FontManager fontManager;
    protected final Minecraft mc;
    protected final ClientConfig guiSettings;

    public GuiPanelBase() {
        this(0,0,0,0);
    }

    public GuiPanelBase(int posX, int posY, int width, int height) {

        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;


        this.fontManager = Spark.fontManager;
        this.mc = Minecraft.getMinecraft();
        this.guiSettings = ClientConfig.getInstance();
    }



    public String getTooltip(){
        return null;
    }






    public int posX;
    public int posY;
    public int width;
    public int height;


    public boolean isMouseOn = false;

    public void setPositionAndSize(int posX, int posY, int width, int height) {

        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    public static boolean mouseCantBeOn = false;

    public static boolean isMouseIn(int posX,int posY,int sizeX,int sizeY,int MouseX, int MouseY){
        if (mouseCantBeOn)
            return false;
        return MouseX >= posX && MouseY >= posY && MouseX < posX + sizeX && MouseY < posY + sizeY;

    }
    public boolean isPosIn(int x, int y){
        return x >= posX && y >= posY && x < posX + width && y < posY + height;

    }
    public boolean isIn(GuiPanelBase base){
        return (posX < base.posX + base.width &&
                posX + width > base.posX &&
                posY < base.posY + base.height &&
                height + posY > base.posY) ||
                (base.posX < posX + width &&
                base.posX + base.width > posX &&
                base.posY < posY + height &&
                base.height + base.posY > posY);

    }



    long lastKey = 0;

    public void renderContent(int MouseX, int MouseY, float deltaTime)
    {

        this.isMouseOn = isMouseIn(this.posX,this.posY,this.width,this.height,MouseX,MouseY);

        this.MouseX = MouseX;
        this.MouseY = MouseY;

        if(isMouseOn)
            TopMouseOn = this;



    }



    public static void drawQuad(int x, int y, int width, int height, int color) {
        drawRect(x,y,x+width,y+height,color);
    }

    public void drawBackGround(int color){
        drawRect(posX, posY,posX+width,posY+height, color);

    }
    public void drawEdges(int color){
        drawHorizontalLine(posX-1,posX+width,posY-1, color);
        drawVerticalLine(posX-1,posY-1,posY+height,color);
        drawVerticalLine(posX+width,posY-1,posY+height,color);
        drawHorizontalLine(posX-1,posX+width,posY+height, color);

    }




    public boolean isFocused(){
        return this == FocusedMouse;
    }

    public boolean isSelected(){
        return this == SelectedMouse;
    }

    public static GuiPanelBase FocusedMouse = null;
    public static GuiPanelBase SelectedMouse = null;
    public static GuiPanelBase TopMouseOn = null;

    public void setFocused(boolean isFocusedIn) {
        this.FocusedMouse = isFocusedIn ? this : null;
    }

    int MouseX;
    int MouseY;
    public void onClickDown(int MouseButton)
    {
        onClickDown(MouseButton,MouseX,MouseY);
    }
    public void onClick(int MouseButton)
    {
        onClick(MouseButton,MouseX,MouseY);
    }

    public void onClickDown(int MouseButton,int MouseX, int MouseY)
    {



    }
    public void onClick(int MouseButton,int MouseX, int MouseY)
    {



    }



    public void onKey(int KeyCode, char TypedChar)
    {



    }
}
