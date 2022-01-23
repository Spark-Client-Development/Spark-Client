package me.wallhacks.spark.util.auth;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.wallhacks.spark.util.objects.AuthException;

import java.io.IOException;

public class MCToken {
    public String token;
    public MCToken(String userHash, String xstsToken) throws AuthException {
        try {
            Request request = new Request("https://api.minecraftservices.com/authentication/login_with_xbox");
            request.header("Content-Type", "application/json");
            request.header("Accept", "application/json");
            JsonObject req = new JsonObject();
            req.addProperty("identityToken", "XBL3.0 x=" + userHash + ";" + xstsToken);
            request.post(req.toString());
            if (request.response() < 200 || request.response() >= 300)
                throw new AuthException("authMinecraft response: " + request.response());
            Gson gson = new Gson();
            token = gson.fromJson(request.body(), JsonObject.class).get("access_token").getAsString();
        } catch (IOException e) {
            throw new AuthException("Failed getting mcToken");
        }
    }
}
