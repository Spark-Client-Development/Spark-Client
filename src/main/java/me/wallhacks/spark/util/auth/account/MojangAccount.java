package me.wallhacks.spark.util.auth.account;

import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import me.wallhacks.spark.util.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraft.util.text.TextFormatting;

import java.net.Proxy;

public class MojangAccount extends Account {
    String password;
    String mail;
    String name;
    public boolean invalid = false;
    public MojangAccount(String mail, String password, String backupName, String uuid) {
        super(null, AccountType.MOJANG, uuid);
        this.mail = mail;
        this.password = password;
        this.name = backupName;
    }

    @Override
    public String getName() {
        if (session == null) return name;
        return super.getName();
    }

    public String getPassword() {
        return password;
    }

    public String getMail() {
        return mail;
    }

    @Override
    public String getStatus() {
        if (loading) return "Logging in" + GuiUtil.getLoadingText(false);
        if (invalid) return "Mojang account " + TextFormatting.RED + "(Invalid)";
        return "Mojang Account";
    }

    @Override
    public void setSession() {
        new LoginThread().start();
    }

    class LoginThread extends Thread {
        public void run() {
            session = null;
            invalid = false;
            loading = true;
            YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
            auth.setUsername(mail);
            auth.setPassword(password);
            try {
                auth.logIn();
                session = new Session(auth.getSelectedProfile().getName(),auth.getSelectedProfile().getId().toString(),auth.getAuthenticatedToken(),"mojang");
                Minecraft.getMinecraft().session = session;
                uuid = session.getSessionID();
            } catch (Exception e) {
                invalid = true;
            }
            loading = false;
        }
    }
}
