package me.wallhacks.spark.gui.tabs;

import net.minecraft.util.ResourceLocation;

public class ClickGuiTab {

    public String name;
    public ResourceLocation icon;
    public ClickGuiTab(String name, ResourceLocation icon) {
        this.name = name;
        this.icon = icon;
    }

    public void drawTab(int mouseX, int mouseY, int click, int posX, int posY, double deltaTime) {

    }

    public void keyTyped(char typedChar, int keyCode)  {

    }
}
