package me.wallhacks.spark.systems.module.modules.render;

import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.render.ColorUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;

import java.awt.*;

@Module.Registration(name = "CustomFog", description = "Stop rendering shit we don't want to render")
public class CustomFog extends Module {

    private ColorSetting netherMainColor = new ColorSetting("Color",this, new Color(137, 80, 80,255),false,"Nether");

    private IntSetting netherFogDensity = new IntSetting("FogDensity",this,30,0,100,"Nether");


    private ColorSetting owMainColor = new ColorSetting("Color",this, new Color(137, 80, 80,255),false,"Overworld");

    private IntSetting owFogDensity = new IntSetting("FogDensity",this,30,0,100,"Overworld");


    private ColorSetting endMainColor = new ColorSetting("Color",this, new Color(137, 80, 80,255),false,"End");

    private IntSetting endFogDensity = new IntSetting("FogDensity",this,30,0,100,"End");


    @SubscribeEvent
    public void onFog(EntityViewRenderEvent.FogColors event) {
        Color color = Color.WHITE;
        switch (mc.player.dimension)
        {
            case -1:
                color = netherMainColor.getColor();
                break;
            case 0:
                color = owMainColor.getColor();
                break;
            case 1:
                color = endMainColor.getColor();
                break;
        }

        event.setRed(color.getRed()/255f);
        event.setGreen(color.getGreen()/255f);
        event.setBlue(color.getBlue()/255f);
    }

    @SubscribeEvent
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        int fogDensity = 30;
        switch (mc.player.dimension)
        {
            case -1:
                fogDensity = netherFogDensity.getValue();
                break;
            case 0:
                fogDensity = owFogDensity.getValue();
                break;
            case 1:
                fogDensity = endFogDensity.getValue();
                break;
        }

        event.setDensity((fogDensity/100f)*event.getDensity());

        GlStateManager.setFog(GlStateManager.FogMode.EXP);
        ColorUtil.glColor(Color.WHITE);
        event.setCanceled(true);
    }

}
