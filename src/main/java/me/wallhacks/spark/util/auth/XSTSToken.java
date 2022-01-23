package me.wallhacks.spark.util.auth;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.wallhacks.spark.util.objects.AuthException;

import java.io.IOException;

public class XSTSToken {
    public String token;
    public String userHash;
    public XSTSToken(String XBLToken) throws AuthException {
        try {
            Request pr = new Request("https://xsts.auth.xboxlive.com/xsts/authorize");
            pr.header("Content-Type", "application/json");
            pr.header("Accept", "application/json");
            JsonObject req = new JsonObject();
            JsonObject reqProps = new JsonObject();
            JsonArray userTokens = new JsonArray();
            userTokens.add(XBLToken);
            reqProps.add("UserTokens", userTokens); //Singleton JSON Array.
            reqProps.addProperty("SandboxId", "RETAIL");
            req.add("Properties", reqProps);
            req.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
            req.addProperty("TokenType", "JWT");
            pr.post(req.toString()); //Note: Here we're encoding parameters as JSON. ('key': 'value')
            if (pr.response() == 401) throw new AuthException("No XBox account found");
            if (pr.response() < 200 || pr.response() >= 300)
                throw new AuthException("authXSTS response: " + pr.response());
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(pr.body(), JsonObject.class);
            userHash = json.getAsJsonObject("DisplayClaims").getAsJsonArray("xui").get(0).getAsJsonObject().get("uhs").getAsString();
            token = json.get("Token").toString();
            token = token.substring(1, token.length() - 1);
        } catch (IOException e) {
            throw new AuthException("Failed getting the XSTSToken");
        }
    }
}
