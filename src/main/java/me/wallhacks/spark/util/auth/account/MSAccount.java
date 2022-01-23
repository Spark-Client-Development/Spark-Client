package me.wallhacks.spark.util.auth.account;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.auth.*;
import me.wallhacks.spark.util.objects.AuthException;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;

public class MSAccount extends Account {
    String refreshToken;
    String backupName;
    String accessToken = "";
    boolean invalid = false;


    public MSAccount(Session session, String refreshToken, String backupName, String uuid) {
        super(session, AccountType.MICROSOFT, uuid);
        this.refreshToken = refreshToken;
        this.backupName = backupName;
    }

    @Override
    public String getName() {
        if (session != null) return session.getProfile().getName();
        return backupName;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void refresh() {
        new RefreshThread().start();
    }

    @Override
    public void setSession() {
        new LoginThread().start();
    }

    @Override
    public String getStatus() {
        if (loading && !accessToken.equals("")) return "Logging in" + GuiUtil.getLoadingText(false);
        if (loading) return "Refreshing token" + GuiUtil.getLoadingText(false);
        if (invalid) return "Microsoft Account " + TextFormatting.RED + "(Invalid)";
        return "Microsoft account";
    }

    class RefreshThread extends Thread {
        public void run() {
            loading = true;
            try {
                Request tokenRequest = new Request("https://login.live.com/oauth20_token.srf");
                tokenRequest.header("Content-Type", "application/x-www-form-urlencoded");
                HashMap<Object, Object> req = new HashMap<>();
                req.put("client_id", "f187964d-b663-4d6f-8b35-71f146a1e5b7");
                req.put("refresh_token", refreshToken);
                req.put("redirect_uri", "http://localhost:48375");
                req.put("grant_type", "refresh_token");
                req.put("scope", "XboxLive.signin XboxLive.offline_access");
                req.put("client_secret", "hQH7Q~KLvutybVmaDO8YvJ2HrP_CATs_Lq-Wp");
                tokenRequest.post(req);
                if (tokenRequest.response() < 200 || tokenRequest.response() >= 300)
                    throw new AuthException("authCode2Token response: " + tokenRequest.response());
                Gson gson = new Gson();
                JsonObject resp = gson.fromJson(tokenRequest.body(), JsonObject.class);
                accessToken = resp.get("access_token").toString();
                refreshToken = resp.get("refresh_token").getAsString();
            } catch (Exception e) {
                invalid = true;
            }
            loading = false;
        }
    }

    class LoginThread extends Thread {
        public void run() {
            session = null;
            invalid = false;
            loading = true;
            try {
                XBLToken xblToken = new XBLToken(accessToken);
                XSTSToken xstsToken = new XSTSToken(xblToken.token);
                MCToken mcToken = new MCToken(xstsToken.userHash, xstsToken.token);
                Profile profile = new Profile(mcToken.token);
                session = new Session(profile.name, profile.uuid, mcToken.token, "mojang");
                uuid = profile.uuid;
                Minecraft.getMinecraft().session = session;
            } catch (Exception e) {
                invalid = true;
            }
            loading = false;
        }
    }
}
