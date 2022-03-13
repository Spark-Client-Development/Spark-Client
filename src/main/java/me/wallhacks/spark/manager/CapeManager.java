package me.wallhacks.spark.manager;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.client.ThreadEvent;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.util.MC;
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



    public CapeManager() {
        Spark.eventBus.register(this);

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


        } catch (Exception problem) {
            problem.printStackTrace();
        }
    }



    void addToCatchMap(String value) {
        try {


            String list[] = value.split(":");

            int delay = 500;
            try {
                if( (list.length > 1))
                    delay = Integer.parseInt(list[1]);
            }
            catch (NumberFormatException exception)
            {

            }

            boolean rgb = (list.length > 1) ? "true".equalsIgnoreCase(list[list.length-1]) : false;





            String[] capeLocations = list[0].split(",");

            BufferedImage[] capes = new BufferedImage[capeLocations.length];
            for (int i = 0; i < capeLocations.length; i++) {
                capes[i] = (ImageIO.read(new URL("https://raw.githubusercontent.com/Spark-Client-Development/resources/main/capes/" + capeLocations[i] + ".png")));

            }
            Spark.logger.info("Loaded cape from '"+value+"' Capes: "+capes.length+" Delay: "+delay);

            toUpdateImage.add(value);
            capeCache.put(value, new Cape(capes, delay, rgb));
        } catch (Exception problem) {
            problem.printStackTrace();
        }

    }

    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        try {
            if (toUpdateImage.size() > 0) {
                String uuid = toUpdateImage.peek();
                capeCache.get(uuid).updateImage();
                toUpdateImage.remove(uuid);
            }
        } catch (NullPointerException problem) {
            //ignore the problem
            //hope it goes away by itself
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

    public boolean isRGB(String uuid) {
        Cape cape = getCape(uuid);

        if(cape == null)
            return false;

        return cape.isRgb();
    }

    @Nullable
    public ResourceLocation getCapeForUser(String uuid) {
        Cape cape = getCape(uuid);

        if(cape == null)
            return null;

        return cape.getCapeLocation();
    }

    public Cape getCape(String uuid) {
        if(!capeMap.containsKey(uuid))
            return null;

        String value = capeMap.get(uuid);

        if(!capeCache.containsKey(value)) {

            if(!toLoad.contains(value))
                toLoad.add(value);
            return null;
        }
        return capeCache.get(value);
    }

    public class Cape {

        final BufferedImage[] capesImage;
        final ResourceLocation[] capes;
        final int delay;
        final boolean rgb;

        public void updateImage() {
            for (int i = 0; i < capesImage.length; i++) {
                final DynamicTexture texture = new DynamicTexture(capesImage[i]);
                capes[i] = mc.getTextureManager().getDynamicTextureLocation("spark/capes", texture);
            }
        }

        public Cape(BufferedImage[] capesImage, int delay, boolean rgb) {
            this.capesImage = capesImage;
            this.capes = new ResourceLocation[capesImage.length];
            this.delay = delay;
            this.rgb = rgb;
        }
        public Cape(ResourceLocation[] capes, int delay, boolean rgb) {
            this.capes = capes;
            this.rgb = rgb;
            this.capesImage = null;
            this.delay = delay;
        }

        public boolean isRgb() {
            return rgb;
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
