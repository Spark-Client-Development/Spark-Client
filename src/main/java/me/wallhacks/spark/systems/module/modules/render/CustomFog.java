package me.wallhacks.spark.systems.module.modules.render;

import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.SettingGroup;
import me.wallhacks.spark.util.render.ColorUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;

import java.awt.*;

@Module.Registration(name = "CustomFog", description = "Stop rendering shit we don't want to render")
public class CustomFog extends Module {

    SettingGroup nether = new SettingGroup("Nether", this);
    private ColorSetting netherMainColor = new ColorSetting("Color",nether, new Color(135, 66, 66, 186),false);
    private IntSetting netherFogDensity = new IntSetting("FogDensity",nether,30,0,100);

    SettingGroup overworld = new SettingGroup("Overworld", this);
    private ColorSetting owMainColor = new ColorSetting("Color",overworld, new Color(67, 102, 176, 181),false);
    private IntSetting owFogDensity = new IntSetting("FogDensity",overworld,30,0,100);

    SettingGroup end = new SettingGroup("End", this);
    private ColorSetting endMainColor = new ColorSetting("Color",end, new Color(62, 190, 145, 134),false);
    private IntSetting endFogDensity = new IntSetting("FogDensity",end,30,0,100);


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
