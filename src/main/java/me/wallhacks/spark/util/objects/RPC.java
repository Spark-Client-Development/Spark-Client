package me.wallhacks.spark.util.objects;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;
import me.wallhacks.spark.util.MC;

public class RPC implements MC {
    private static final DiscordRichPresence presence = new DiscordRichPresence();
    private static final DiscordRPC rpc = DiscordRPC.INSTANCE;;
    private static Thread thread;

    public static void start() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        rpc.Discord_Initialize("935160718041440317", handlers, true, "");
        presence.startTimestamp = System.currentTimeMillis() / 1000L;
        presence.largeImageKey = "spark";
        presence.largeImageText = "Version: " + Spark.VERSION;
        rpc.Discord_UpdatePresence(presence);
        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                rpc.Discord_RunCallbacks();
                presence.details = ClientConfig.getInstance().rpcusername.isOn() ? mc.session.getUsername() : "";
                presence.state  = ClientConfig.getInstance().rpcip.isOn() ? ((mc.isSingleplayer() ? "Playing alone" : (mc.getCurrentServerData() != null ? "With friends on "+mc.getCurrentServerData().serverIP : "In Menu"))) : "";

                rpc.Discord_UpdatePresence(presence);
                try {
                    Thread.sleep(2000L);
                }
                catch (InterruptedException interruptedException) {}
            }
        }, "RPC-Callback-Handler");
        thread.start();
    }

    public static void stop() {
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }
        rpc.Discord_Shutdown();
    }
}
