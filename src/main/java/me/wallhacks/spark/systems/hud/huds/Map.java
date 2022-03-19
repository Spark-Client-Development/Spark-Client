package me.wallhacks.spark.systems.hud.huds;

import me.wallhacks.spark.systems.clientsetting.clientsettings.HudSettings;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.render.MapRender;
import net.minecraft.client.gui.Gui;

import java.lang.reflect.Array;
import java.util.Arrays;


@HudElement.Registration(name = "Map", posX = 1, posY = 0, height = 128, width = 128, description = "Shows your inventory", drawBackground = false)
public class Map extends HudElement {

    IntSetting size = new IntSetting("GuiSize",this,100,50,128);
    IntSetting zoom = new IntSetting("MapSize",this,200,200,600);

    @Override
    public void draw(float deltaTime) {
        super.draw(deltaTime);

        setWidth(size.getValue());
        setHeight(size.getValue());

        Gui.drawRect(getRenderPosX(), getRenderPosY(), getEndRenderPosX(), getEndRenderPosY(), HudSettings.getInstance().getGuiHudListBackgroundColor().getRGB());

        MapRender.RenderWholeMap(getRenderPosX()+2,this.getRenderPosY()+2,getWidth()-4,getHeight()-4,zoom.getValue(),mc.player.posX,mc.player.posZ,0,0,mc.player.dimension, 0, 0, false,false);
    }
}
