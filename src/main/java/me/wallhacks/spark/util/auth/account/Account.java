package me.wallhacks.spark.util.auth.account;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraft.util.text.TextFormatting;

public class Account {
    public Session session;
    public AccountType accountType;
    public boolean loading = false;
    public String uuid;

    public Account(Session session, AccountType accountType, String uuid) {
        this.session = session;
        this.accountType = accountType;
        this.uuid = uuid;
    }

    public void login() {
        if (session == null) {
            this.setSession();
        } else Minecraft.getMinecraft().session = session;
    }

    public void setSession() {
    }

    public String getName() {
        return session.getProfile().getName();
    }

    public String getUUID() {
        if (session != null)
            return session.getProfile().getId().toString();
        else return uuid;
    }

    public String getStatus() {
        return TextFormatting.RED + "Cracked";
    }
}
