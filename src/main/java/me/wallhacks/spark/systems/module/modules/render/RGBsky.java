package me.wallhacks.spark.systems.module.modules.render;

import me.wallhacks.spark.event.render.SkyEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.render.ColorUtil;
import me.wallhacks.spark.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

@Module.Registration(name = "RGBsky", description = "The best module")
public class RGBsky extends Module {


    @SubscribeEvent
    public void RenderSky(SkyEvent event) {

        Color up = ColorUtil.fromHSB((System.currentTimeMillis() % 6000) / 6000F, 1f, 1f);
        Color down = ColorUtil.fromHSB(((System.currentTimeMillis() - 1200) % 6000) / 6000F, 1f, 1f);

        RenderUtil.drawSkyBox(up,down);

        event.setCanceled(true);
    }






}
