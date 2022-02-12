package me.wallhacks.spark.gui.clickGui;

import me.wallhacks.spark.gui.panels.GuiPanelScreen;
import me.wallhacks.spark.util.render.MapRender;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class MapGui extends GuiPanelScreen {


    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);

        drawRect(10,10,width-10,height-10,new Color(200, 200, 200,255).getRGB());

        //todo make better
        MapRender.RenderWholeMap(2,2,width-4,height-4,128,mc.player.posX,mc.player.posZ,0,0);


    }
}
