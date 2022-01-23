package me.wallhacks.spark.util.auth;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.wallhacks.spark.util.objects.AuthException;

import java.io.IOException;

public class Profile {
    public String uuid;
    public String name;
    public Profile(String accessToken) throws AuthException {
        try {
            Request request = new Request("https://api.minecraftservices.com/entitlements/mcstore");
            request.header("Authorization", "Bearer " + accessToken);
            request.get();
            if (request.response() < 200 || request.response() >= 300)
                throw new IllegalArgumentException("checkGameOwnership response: " + request.response());
            Gson gson = new Gson();
            if (gson.fromJson(request.body(), JsonObject.class).getAsJsonArray("items").size() == 0)
                throw new AuthException("Game not owned");
            Request pRequest = new Request("https://api.minecraftservices.com/minecraft/profile");
            pRequest.header("Authorization", "Bearer " + accessToken);
            pRequest.get();
            if (pRequest.response() < 200 || pRequest.response() >= 300)
                throw new IllegalArgumentException("getProfile response: " + pRequest.response());
            JsonObject resp = gson.fromJson(pRequest.body(), JsonObject.class);
            uuid = resp.get("id").getAsString();
            name = resp.get("name").getAsString();
        } catch (IOException e) {
            throw new AuthException("Failed getting mc profile");
        }
    }
}
