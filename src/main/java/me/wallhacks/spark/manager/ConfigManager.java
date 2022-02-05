package me.wallhacks.spark.manager;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.clientsetting.clientsettings.BaritoneConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.systems.clientsetting.ClientSetting;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.Setting;
import me.wallhacks.spark.util.EncryptionUtil;
import me.wallhacks.spark.util.FileUtil;
import me.wallhacks.spark.util.RandomString;
import me.wallhacks.spark.util.auth.account.Account;
import me.wallhacks.spark.util.auth.account.AccountType;
import me.wallhacks.spark.util.auth.account.MSAccount;
import me.wallhacks.spark.util.auth.account.MojangAccount;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

public class ConfigManager {




    String config = "default";

    public String getCurrentConfigName() {
        return config;
    }


    String getConfigPath(String name) {
        return Spark.ParentPath.getAbsolutePath() + "\\configs\\" + name;
    }

    public boolean loadConfig(String name) {
        if(!FileUtil.exists(getConfigPath(name)))
            return false;

        SaveFromConfig(config);
        config = name;
        LoadFromConfig(config, true);
        LoadFromConfig(config, false);
        return true;
    }
    public boolean deleteConfig(String name) {
        if(!FileUtil.exists(getConfigPath(name)))
            return false;


        return FileUtil.deleteDirectory(getConfigPath(name));
    }

    public boolean saveToConfig(String name) {
        SaveFromConfig(name);
        return true;
    }

    public void Load() {
        if(FileUtil.exists(Spark.ParentPath.getAbsolutePath()+"\\config.sex"))
            config = FileUtil.read(Spark.ParentPath.getAbsolutePath()+"\\config.sex");
        LoadFromConfig(config, false);
    }

    public void Save() {
        FileUtil.write(Spark.ParentPath.getAbsolutePath()+"\\config.sex",config);
        SaveFromConfig(config);

    }


    public void LoadFromConfig(String configName, boolean baritone) {
        loadSystems(configName, baritone);
    }

    public void SaveFromConfig(String configName) {
        saveSystems(configName);
    }

    String getSystemSettingFile(SettingsHolder holder,String configName) {
        String base = getConfigPath(configName) + "\\systems\\";
        if (holder instanceof Module)
            base = base + "modules\\";
        if (holder instanceof ClientSetting)
            base = base + "clientSettings\\";
        if (holder instanceof HudElement)
            base = base + "huds\\";
        return base + holder.getName() + ".sex";
    }



    private void loadSystems(String configName, boolean baritone) {
        for (SettingsHolder system : SystemManager.getSystems()) {
            if (system instanceof BaritoneConfig && !baritone) continue;
            if (!(system instanceof BaritoneConfig) && baritone) continue;
            try {
                String s = FileUtil.read(getSystemSettingFile(system,configName));
                if (s != null) {
                    String[] List = s.split("\n");
                    for (String var : List) {
                        String n = var.split(":")[0];
                        if (var.split(":").length > 1) {
                            String v = var.split(":")[1];

                            if (system instanceof Module) {
                                if (n.equalsIgnoreCase("Toggled"))
                                    ((Module) system).setEnabled(Boolean.parseBoolean(v));
                                if (n.equalsIgnoreCase("Hold"))
                                    ((Module) system).setHold(Boolean.parseBoolean(v));
                                if (n.equalsIgnoreCase("Visible"))
                                    ((Module) system).setVisible(Boolean.parseBoolean(v));
                                if (n.equalsIgnoreCase("Muted"))
                                    ((Module) system).setMuted(Boolean.parseBoolean(v));
                                if (n.equalsIgnoreCase("KeyBind"))
                                    ((Module) system).setBind(Integer.parseInt(v));
                            }
                            if (system instanceof HudElement) {
                                if (n.equalsIgnoreCase("Toggled"))
                                    ((HudElement) system).setEnabled(Boolean.parseBoolean(v));
                                if (n.equalsIgnoreCase("PosPercentX"))
                                    ((HudElement) system).setPercentPosX(Double.parseDouble(v));
                                if (n.equalsIgnoreCase("PosPercentY"))
                                    ((HudElement) system).setPercentPosY(Double.parseDouble(v));
                                if (n.equalsIgnoreCase("SnappedElement"))
                                    ((HudElement) system).setSnappedElement(Integer.parseInt(v));
                            }
                            for (Setting<?> setting : system.getSettings()) {
                                if (n.equalsIgnoreCase(setting.getName()) || n.equalsIgnoreCase(setting.getCategory() + "/" + setting.getName()))
                                    setting.setValueString(v);

                            }

                        }

                    }
                    system.onConfigLoad();
                }
            } catch (Exception e) {
                Spark.logger.info("Failed to load config for " + system.getName());
                e.printStackTrace();
            }
        }
    }

    private void saveSystems(String configName) {
        for (SettingsHolder system : SystemManager.getSystems()) {
            try {
                ArrayList<String> lines = new ArrayList<String>();

                if (system instanceof Module) {
                    lines.add("Toggled:" + ((Module) system).isEnabled());
                    lines.add("KeyBind:" + ((Module) system).getBind());
                    lines.add("Hold:" + ((Module) system).isHold());
                    lines.add("Visible:" + ((Module) system).isVisible());
                    lines.add("Muted:" + ((Module) system).isMuted());
                }
                if (system instanceof HudElement) {
                    lines.add("Toggled:" + ((HudElement) system).isEnabled());
                    lines.add("PosPercentX:" + ((HudElement) system).getPercentPosX());
                    lines.add("PosPercentY:" + ((HudElement) system).getPercentPosY());
                    lines.add("SnappedElement:" + ((HudElement) system).getSnappedElement());
                }

                for (Setting<?> setting : system.getSettings())
                    lines.add(setting.getCategory() + "/" + setting.getName() + ":" + setting.getValueString());

                String content = "";
                for (String e : lines)
                    content = content + e + "\n";

                FileUtil.write(getSystemSettingFile(system,configName), content);

                system.onConfigSave();

            } catch (Exception e) {
                Spark.logger.info("Failed to save config for " + system.getName());
                e.printStackTrace();
            }
        }
    }


    public ArrayList<String> getList() {
        return FileUtil.listFolderForFolder(Spark.ParentPath.getAbsolutePath() + "\\configs");
    }
}

