package me.wallhacks.spark.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.util.MC;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;


public class CapeManager implements MC {
    private HashMap<String, ResourceLocation> capeMap = new HashMap<String, ResourceLocation>();
    private HashMap<String, ResourceLocation> capeCache = new HashMap<String, ResourceLocation>();
    public CapeManager() {
        try {
            URL cache = new URL("https://raw.githubusercontent.com/Spark-Client-Development/resources/main/capes/users.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(cache.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String[] split = inputLine.split(":");
                if (capeCache.containsKey(split[1])) {
                    capeMap.put(split[0], capeCache.get(split[1]));
                } else {
                    try {
                        getCape(split[1]);
                        capeMap.put(split[0], capeCache.get(split[1]));
                        Spark.logger.info("Loaded cape:" + split[1] + " for:" + split[0]);
                    } catch (Exception e) {
                        Spark.logger.info("Failed to load cape for uuid: " + split[0]);
                    }
                }
            }
        } catch (Exception problem) {

        }
    }

    //jewed from salhack >:)
    public void getCape(String name) throws MalformedURLException, IOException {
        final DynamicTexture texture = new DynamicTexture(ImageIO.read(new URL("https://raw.githubusercontent.com/Spark-Client-Development/resources/main/capes/" + name + ".png")));
        capeCache.put(name, mc.getTextureManager().getDynamicTextureLocation("spark/capes", texture));
    }

    @Nullable
    public ResourceLocation getCapeForUser(String uuid) {
        return capeMap.getOrDefault(uuid, null);
    }
}
