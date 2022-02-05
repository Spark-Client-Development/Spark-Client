package me.wallhacks.spark.systems.clientsetting.clientsettings;

import baritone.api.Settings;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.client.SettingChangeEvent;
import me.wallhacks.spark.systems.clientsetting.ClientSetting;
import me.wallhacks.spark.systems.setting.Setting;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.HashMap;

@ClientSetting.Registration(name = "BaritoneConfig", description = "Config for baritone")
public class BaritoneConfig extends ClientSetting {
    private static BaritoneConfig INSTANCE;
    HashMap<Setting, Settings.Setting> settingMap = new HashMap<>();
    public BaritoneConfig() {
        INSTANCE = this;
        Spark.eventBus.register(this);
    }

    public static BaritoneConfig getInstance() {
        return INSTANCE;
    }

    public void init(Settings baritone) {
        for (Settings.Setting bSetting : baritone.allSettings) {
            if (bSetting.getType().equals(Boolean.class)) {
                BooleanSetting s = new BooleanSetting(bSetting.getName(), this, (Boolean) bSetting.getValue(), bSetting.getCategory());
                settingMap.put(s, bSetting);
            } else if (bSetting.getType().equals(Color.class)) {
                ColorSetting s = new ColorSetting(bSetting.getName(), this, (Color) bSetting.getValue(), bSetting.getCategory());
                settingMap.put(s, bSetting);
            } else if (bSetting instanceof Settings.DoubleSetting) {
                DoubleSetting s = new DoubleSetting(bSetting.getName(), this, (Double) bSetting.getValue(), ((Settings.DoubleSetting) bSetting).min, ((Settings.DoubleSetting) bSetting).max, bSetting.getCategory());
                settingMap.put(s, bSetting);
            } else if (bSetting instanceof Settings.IntSetting) {
                IntSetting s = new IntSetting(bSetting.getName(), this, (Integer) bSetting.getValue(), ((Settings.IntSetting) bSetting).min, ((Settings.IntSetting) bSetting).max, bSetting.getCategory());
                settingMap.put(s, bSetting);
            }
        }
        Spark.configManager.LoadFromConfig(Spark.configManager.getCurrentConfigName(), true);
    }

    @SubscribeEvent
    public void onSettingChange(SettingChangeEvent event) {
        Setting setting = event.getSetting();
        if (settingMap.containsKey(setting)) {
            settingMap.get(setting).setValue(setting.getValue(), true);
        }
    }

    public void sync(Settings.Setting setting) {
        getKeySetting(setting).setValue(setting.getValue());
    }

    public void syncColor(Settings.Setting setting) {
        Setting s = getKeySetting(setting);
        if (s != null)
            setting.setValue(((ColorSetting) s).getColor(), true);
    }

    private Setting getKeySetting(Settings.Setting setting) {
        for (Setting s : settingMap.keySet()) {
            if (settingMap.get(s) == setting) return s;
        }
        return null;
    }
}
