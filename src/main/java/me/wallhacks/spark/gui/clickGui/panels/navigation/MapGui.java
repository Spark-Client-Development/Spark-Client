package me.wallhacks.spark.gui.clickGui.panels.navigation;

import com.sun.javafx.geom.Vec2d;
import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.gui.panels.GuiPanelButton;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.render.MapRender;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiConfig;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class MapGui extends GuiPanelBase {


    void resetValues() {
        dim = mc.player.dimension;

        zoom = 128;
        offsetX = 0;
        offsetY = 0;
    }

    int dim;
    double zoom;
    double offsetX;
    double offsetY;


    final ResourceLocation layersIcon = new ResourceLocation("textures/icons/layersicon.png");
    GuiPanelButton dimButton = new GuiPanelButton(() -> {
        dim++;
        if(dim > 1)
            dim = -1;
    },"");


    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);


        zoom = Math.max(50,zoom+zoom*deltaTime*(Mouse.getDWheel())*0.0002);

        Vec2d pos = MapRender.ConvertPos(new Vec2d(mc.player.posX,mc.player.posZ),mc.player.dimension,dim);
        MapRender.RenderWholeMap(posX,posY,width,height,(int)zoom,pos.x,pos.y,offsetX,offsetY,dim);



        int layerButton = 18;


        dimButton.setPositionAndSize(posX+3,posY+height-3-layerButton,layerButton,layerButton);

        dimButton.drawBackGround(guiSettings.getGuiMainPanelBackgroundColor().getRGB());
        dimButton.drawBackGround(guiSettings.getGuiMainPanelBackgroundColor().getRGB());
        GuiUtil.drawCompleteImage(posX+3+2,posY+height-3-layerButton+2,layerButton-4,layerButton-4,layersIcon, Color.WHITE);
        dimButton.renderContent(MouseX, MouseY, deltaTime);

    }

    double lMouseX;
    double lMouseY;

    @Override
    public void onClickDown(int MouseButton, int MouseX, int MouseY) {
        super.onClickDown(MouseButton, MouseX, MouseY);

        lMouseX = MouseX;
        lMouseY = MouseY;
    }

    @Override
    public void onClick(int MouseButton, int MouseX, int MouseY) {
        super.onClick(MouseButton, MouseX, MouseY);

        offsetX += MouseX-lMouseX;
        offsetY += MouseY-lMouseY;

        lMouseX = MouseX;
        lMouseY = MouseY;
    }
}
