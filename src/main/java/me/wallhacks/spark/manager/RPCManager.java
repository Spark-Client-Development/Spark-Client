package me.wallhacks.spark.manager;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.client.SettingChangeEvent;
import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;
import me.wallhacks.spark.systems.setting.Setting;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.objects.RPC;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RPCManager implements MC {
    public RPCManager() {
        Spark.eventBus.register(this);
        if (ClientConfig.INSTANCE.rpc.getValue()) RPC.start();
    }

    @SubscribeEvent
    public void onSettingChange(SettingChangeEvent event) {
        Setting setting = event.getSetting();
        if (setting == ClientConfig.INSTANCE.rpc) {
            if ((boolean)setting.getValue()) RPC.start(); else RPC.stop();
        }
    }
}
