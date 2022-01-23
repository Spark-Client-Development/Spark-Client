package me.wallhacks.spark.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.apache.commons.io.IOUtils;


public class SessionUtils implements MC {
    private static HashMap<UUID, Property> skinMap = new HashMap<>();
    public static boolean setSkin(NetworkPlayerInfo info, UUID uuid) {
        Property p;
        if (skinMap.containsKey(uuid)) {
            p = skinMap.get(uuid);
        } else {
            p = getTexture(uuid);
            skinMap.put(uuid, p);
        }
        if(p != null) {
            info.getGameProfile().getProperties().put("textures", p);
            info.playerTexturesLoaded = false;
            info.loadPlayerTextures();
            return true;
        }
        else
            return false;
    }

    public static UUID fromString(String uuid) {
            try {
                return UUIDTypeAdapter.fromString(uuid);
            }
            catch (IllegalArgumentException var2) {
                return null;
            }
    }

    public static Property getTexture(UUID uuid) {
        try {
            URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
            JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            String texture = textureProperty.get("value").getAsString();
            String signature = textureProperty.get("signature").getAsString();

            return new Property("textures", texture, signature);
        } catch (Exception e) {
            return null;
        }
    }



    public static UUID getid(String name) {

        if(knownPlayerName.containsKey(name))
        {
            return knownPlayerName.get(name);
        }


        URLConnection request;
        try {
            request = new URL("https://api.mojang.com/users/profiles/minecraft/"+name).openConnection();

            request.connect();

            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
            String id = rootobj.get("id").getAsString(); //just grab the zipcode
            id = java.util.UUID.fromString(
                    id
                            .replaceFirst(
                                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
                            )
            ).toString();
            UUID uuid = UUID.fromString(id);
            knownPlayerUUID.put(uuid, name);
            knownPlayerName.put(name, uuid);

            return uuid;
        } catch (Exception e) {
            //dont print the stacktrace it annoys the fuck out of me
        }
        return null;

    }

    private static HashMap<String, UUID> knownPlayerName = new HashMap<>();
    private static HashMap<UUID, String> knownPlayerUUID = new HashMap<>();


    public static String getname(UUID uuid) {

        if(knownPlayerUUID.containsKey(uuid))
        {
            return knownPlayerUUID.get(uuid);
        }


        URLConnection request;
        try {
            request = new URL("https://api.mojang.com/user/profiles/"+uuid.toString().replace("-", "")+"/names").openConnection();

            request.connect();



            String nameJson = IOUtils.toString(new URL("https://api.mojang.com/user/profiles/"+uuid.toString().replace("-", "")+"/names"));
            JsonParser jp = new JsonParser();
            JsonArray nameValue = (JsonArray) jp.parse(nameJson);
            String playerSlot = nameValue.get(nameValue.size()-1).toString();
            JsonObject nameObject = (JsonObject) jp.parse(playerSlot);

            knownPlayerUUID.put(uuid, nameObject.get("name").toString());
            knownPlayerName.put(nameObject.get("name").toString(), uuid);
            return nameObject.get("name").toString().replace('"'+"","");



            //return name;
        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;

    }


}
