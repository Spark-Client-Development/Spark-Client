package me.wallhacks.spark.event.client;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.Event;

public class SettingChangeEvent extends Event {
    Setting setting;

    public SettingChangeEvent(Setting setting) {
        this.setting = setting;
        Spark.eventBus.post(this);
    }

    public Setting getSetting() {
        return setting;
    }
}
