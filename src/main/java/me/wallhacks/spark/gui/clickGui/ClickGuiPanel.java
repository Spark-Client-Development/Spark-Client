package me.wallhacks.spark.gui.clickGui;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.manager.FontManager;
import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;

public class ClickGuiPanel {

    public final ClientConfig guiSettings;
    final ClickGuiMenuBase clickGuiMenuBase;
    public final FontManager fontManager;
    public ClickGuiPanel(ClickGuiMenuBase clickGuiMenuBase) {
        this.clickGuiMenuBase = clickGuiMenuBase;
        guiSettings = ClientConfig.getInstance();
        fontManager = Spark.fontManager;
    }




    protected int getWidth() {
        return clickGuiMenuBase.width;
    }
    protected int getHeight() {
        return clickGuiMenuBase.height;
    }

    protected int getCenterX() {
        return getWidth()/2;
    }
    protected int getCenterY() {
        return getHeight()/2;
    }

    public boolean renderBackground() {
        return true;
    }

    public boolean showMenuBar = true;


    public String getName() {
        return "Gui";
    }


    public void init() {


    }

    public void tick() {



    }

    public void renderContent(int MouseX, int MouseY, float deltaTime) {

    }
}
