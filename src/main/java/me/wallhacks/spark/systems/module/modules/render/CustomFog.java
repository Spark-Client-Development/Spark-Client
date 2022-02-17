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

    private ColorSetting mainColor = new ColorSetting("Color",this, new Color(137, 80, 80,255),false,"Fog");

    private IntSetting fogDensity = new IntSetting("FogDensity",this,30,0,100,"Fog");


    @SubscribeEvent
    public void onFog(EntityViewRenderEvent.FogColors event) {
        event.setRed(mainColor.getColor().getRed()/255f);
        event.setGreen(mainColor.getColor().getGreen()/255f);
        event.setBlue(mainColor.getColor().getBlue()/255f);
    }

    @SubscribeEvent
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        event.setDensity((fogDensity.getValue()/100f)*event.getDensity());

        GlStateManager.setFog(GlStateManager.FogMode.EXP);
        ColorUtil.glColor(Color.WHITE);
        event.setCanceled(true);
    }

}
