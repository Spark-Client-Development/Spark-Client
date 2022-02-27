package me.wallhacks.spark.manager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.client.ThreadEvent;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.render.ColorUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;


public class CapeManager implements MC {
    private HashMap<String, String> capeMap = new HashMap<String, String>();
    private HashMap<String, Cape> capeCache = new HashMap<String, Cape>();

    private ConcurrentLinkedQueue<String> toLoad = new ConcurrentLinkedQueue<String>();
    private ConcurrentLinkedQueue<String> toUpdateImage = new ConcurrentLinkedQueue<String>();

    private HashSet<String> fancy = new HashSet<String>();

    final Cape location;

    public CapeManager() {
        Spark.eventBus.register(this);

        ResourceLocation[] capes = new ResourceLocation[200];
        for (int i = 0; i < 200; i++) {
            capes[i] = new ResourceLocation("textures/capes/rgb/"+i+".png");
        }
        location = new Cape(capes,4);


        try {
            URL cache = new URL("https://raw.githubusercontent.com/Spark-Client-Development/resources/main/capes/users.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(cache.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String[] split = inputLine.split(":",2);

                if(split.length < 2)
                    continue;
                capeMap.put(split[0], split[1]);
                Spark.logger.info("Added cape: "+split[0] +" "+split[1]);
            }


            cache = new URL("https://raw.githubusercontent.com/Spark-Client-Development/resources/main/capes/fancyCapeUsers.txt");
            in = new BufferedReader(new InputStreamReader(cache.openStream()));
            while ((inputLine = in.readLine()) != null) {
                fancy.add(inputLine);
            }
        } catch (Exception problem) {
            problem.printStackTrace();
        }
    }



    void addToCatchMap(String uuid) {
        try {
            String value = capeMap.get(uuid);

            String list[] = value.split(":");
            int delay = (list.length > 1) ? Integer.parseInt(list[1]) : 500;

            String[] capeLocations = list[0].split(",");

            BufferedImage[] capes = new BufferedImage[capeLocations.length];
            for (int i = 0; i < capeLocations.length; i++) {
                capes[i] = (ImageIO.read(new URL("https://raw.githubusercontent.com/Spark-Client-Development/resources/main/capes/" + capeLocations[i] + ".png")));

            }
            Spark.logger.info("Loaded cape for "+uuid+" Capes: "+capes.length+" Delay: "+delay);

            toUpdateImage.add(uuid);
            capeCache.put(uuid, new Cape(capes, delay));
        } catch (Exception problem) {
            problem.printStackTrace();
        }

    }

    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        if (toUpdateImage.size() > 0)
        {
            String uuid = toUpdateImage.peek();
            capeCache.get(uuid).updateImage();
            toUpdateImage.remove(uuid);
        }
    }

    @SubscribeEvent
    public void onThread(ThreadEvent event) {
        while (toLoad.size() > 0)
        {
            String uuid = toLoad.peek();
            addToCatchMap(uuid);
            toLoad.remove(uuid);
        }
    }

    @Nullable
    public ResourceLocation getCapeForUser(String uuid) {


        if(fancy.contains(uuid))
        {
            return location.getCapeLocation();

        }

        if(!capeCache.containsKey(uuid))
        {

            if(capeMap.containsKey(uuid) && !toLoad.contains(uuid))
                toLoad.add(uuid);
            return null;
        }



        return capeCache.get(uuid).getCapeLocation();
    }

    public class Cape {

        final BufferedImage[] capesImage;
        final ResourceLocation[] capes;
        final int delay;

        public void updateImage() {
            for (int i = 0; i < capesImage.length; i++) {
                final DynamicTexture texture = new DynamicTexture(capesImage[i]);
                capes[i] = mc.getTextureManager().getDynamicTextureLocation("spark/capes", texture);
            }
        }

        public Cape(BufferedImage[] capesImage, int delay) {
            this.capesImage = capesImage;
            this.capes = new ResourceLocation[capesImage.length];
            this.delay = delay;
        }
        public Cape(ResourceLocation[] capes, int delay) {
            this.capes = capes;
            this.capesImage = null;
            this.delay = delay;
        }

        public ResourceLocation getCapeLocation() {

            if(capes.length == 1)
                return capes[0];

            double rand = (System.currentTimeMillis() * 6.28 / capes.length / delay) % capes.length;

            int index = (int) Math.min(Math.floor(rand),capes.length-1);
            return capes[index];
        }
    }
}
