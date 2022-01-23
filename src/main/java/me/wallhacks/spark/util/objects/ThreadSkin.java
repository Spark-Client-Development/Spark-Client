package me.wallhacks.spark.util.objects;

import me.wallhacks.spark.util.SessionUtils;
import net.minecraft.client.network.NetworkPlayerInfo;

public class ThreadSkin extends Thread {
    NetworkPlayerInfo info;
    public ThreadSkin(NetworkPlayerInfo info) {
        this.info = info;
    }
    public void run() {
        SessionUtils.setSkin(info, info.getGameProfile().getId());
    }
}

