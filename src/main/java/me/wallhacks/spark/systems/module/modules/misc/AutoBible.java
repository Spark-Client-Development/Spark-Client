package me.wallhacks.spark.systems.module.modules.misc;

import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;


@Module.Registration(name = "AutoBible", description = "The best module")
public class AutoBible extends Module {

    BufferedReader bufferedReader;

    @Override
    public void onDisable() {
        bufferedReader = null;
        super.onDisable();
    }

    @Override
    public void onEnable() {
        try {
            InputStream in = new URL(" https://raw.githubusercontent.com/Spark-Client-Development/resources/main/bible.txt").openStream();;

            bufferedReader = new BufferedReader(
                    new InputStreamReader(in, "UTF8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onEnable();
    }

    int ticks = 20;

    @SubscribeEvent
    void OnUpdate(PlayerUpdateEvent event) {

        if(ticks < 0)
        {
            try {
                String n = "";
                while(n.length() == 0)
                    n = bufferedReader.readLine();
                NarratorChatListener.INSTANCE.narrator.say(n);

                mc.player.sendChatMessage(n);

                ticks = (int) (n.length()*2.5);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else
            ticks--;
    }


}