package me.wallhacks.spark.util.auth;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.wallhacks.spark.util.objects.AuthException;

import java.io.IOException;

public class XBLToken {
    public String token;
    public XBLToken(String accessToken) throws AuthException {
        try {
            Request request = new Request("https://user.auth.xboxlive.com/user/authenticate");
            request.header("Content-Type", "application/json");
            request.header("Accept", "application/json");
            JsonObject req = new JsonObject();
            JsonObject properties = new JsonObject();
            properties.addProperty("AuthMethod", "RPS");
            properties.addProperty("SiteName", "user.auth.xboxlive.com");
            properties.addProperty("RpsTicket", "d=" + accessToken);
            req.add("Properties", properties);
            req.addProperty("RelyingParty", "http://auth.xboxlive.com");
            req.addProperty("TokenType", "JWT");
            request.post(req.toString());
            Gson gson = new Gson();
            if (request.response() == 401) throw new AuthException("No XBox account found");
            if (request.response() < 200 || request.response() >= 300)
                throw new IllegalArgumentException("authXBL response: " + request.response());
            token = gson.fromJson(request.body(), JsonObject.class).get("Token").getAsString();
        } catch (IOException e) {
        	e.printStackTrace();
            throw  new AuthException("Failed generating XBLToken");
        }
    }
}
