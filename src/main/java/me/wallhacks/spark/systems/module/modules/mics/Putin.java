package me.wallhacks.spark.systems.module.modules.mics;

import com.mojang.authlib.GameProfile;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.StringSetting;
import me.wallhacks.spark.util.SessionUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.UUID;

@Module.Registration(name = "Putin", description = "The best module")
public class Putin extends Module {

    Clip clip;
    AudioInputStream inputStream;


    Color FogColor = new Color(160,0,0,140);
    float FogPower = 2;

    public final ResourceLocation putinSkin = new ResourceLocation("textures/putin.png");

    String s = "";

    public Putin() {
        try {


            //relative ot jar file/src folder
            String filePath = "assets/minecraft/sounds/wideputinwalking.wav";

            //important shit for loading this input stream thing
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(filePath);

            InputStream bufferedIn = new BufferedInputStream(in);


            inputStream = AudioSystem.getAudioInputStream(bufferedIn);
            clip = AudioSystem.getClip();
            clip.open(inputStream);

            clip.start();



            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            double gain = .5D; // number between 0 and 1 (loudest)
            float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);

            clip.stop();
        } catch (Exception e) {
            e.printStackTrace();

        }

        instance = this;
    }
    public static Putin instance;


    @SubscribeEvent
    public void onFog(EntityViewRenderEvent.FogColors event) {
        event.setRed(FogColor.getRed()/255f);
        event.setGreen(FogColor.getGreen()/255f);
        event.setBlue(FogColor.getBlue()/255f);


    }

    @SubscribeEvent
    public void onFog(EntityViewRenderEvent.FogDensity event) {
        event.setDensity((16f*20f)/FogPower);

        event.setCanceled(true);
    }

    @Override
    public void onDisable() {
        try {
            clip.stop();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    @Override
    public void onEnable() {


        try {
            clip.start();

            clip.loop(Clip.LOOP_CONTINUOUSLY);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }










}
