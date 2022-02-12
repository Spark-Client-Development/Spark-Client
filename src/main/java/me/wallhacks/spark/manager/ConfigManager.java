package me.wallhacks.spark.manager;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.client.SettingChangeEvent;
import me.wallhacks.spark.event.player.UpdateWalkingPlayerEvent;
import me.wallhacks.spark.systems.clientsetting.clientsettings.BaritoneConfig;
import me.wallhacks.spark.systems.setting.settings.StringSetting;
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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConfigManager {

    ArrayList<Config> configs = new ArrayList<>();
    Config currentConfig;

    public ConfigManager() {
        Spark.eventBus.register(this);
    }


    //rename file lol
    @SubscribeEvent
    void OnUpdateWalkingEvent(SettingChangeEvent event) {
        for (Config c : configs) {
            if(c.name == event.getSetting())
                c.syncFileName();


        }
    }

    public ArrayList<Config> getConfigs() {
        return configs;
    }

    public Config getCurrentConfig() {
        return currentConfig;
    }


    String getConfigPath(Config config) {
        return Spark.ParentPath.getAbsolutePath() + "\\configs\\" + config.getConfigName();
    }

    public boolean loadConfig(Config config,boolean saveOld) {
        if(!configs.contains(config))
            return false;

        if(saveOld)
            SaveFromConfig(currentConfig);
        currentConfig = config;
        FileUtil.write(Spark.ParentPath.getAbsolutePath()+"\\config.sex",currentConfig.getConfigName());

        if(FileUtil.exists(getConfigPath(config))){
            LoadFromConfig(currentConfig, true);
            LoadFromConfig(currentConfig, false);
        }

        return true;
    }
    public boolean deleteConfig(Config config) {
        if(configs.size() <= 1)
            return false;

        if(!configs.contains(config))
            return false;

        String p = getConfigPath(config);


        if(FileUtil.exists(p))
            FileUtil.deleteDirectory(p);


        configs.remove(config);

        if(currentConfig == config)
        {
            loadConfig(configs.get(0),false);
        }


        return true;
    }

    public boolean createConfig(Config config) {
        if(configs.contains(config))
            return false;
        configs.add(config);
        SaveFromConfig(config);
        return true;
    }

    public Config getConfigFromName(String s) {
        for (Config c : configs) {
            if(c.getConfigName().equals(s))
                return c;
        }
        return null;
    }


    public void Load(boolean loadBaritone) {
        configs.clear();
        currentConfig = null;
        String configFolder = (Spark.ParentPath.getAbsolutePath() + "\\configs");
        for (String s : FileUtil.listFolderForFolder(configFolder)) {
            Config c = new Config(s);
            configs.add(c);
            loadSettingHolder(c,getConfigPath(c)+"\\configData.sex");
            c.name.setValue(s);
        }
        if(FileUtil.exists(Spark.ParentPath.getAbsolutePath()+"\\config.sex"))
            currentConfig = getConfigFromName(FileUtil.read(Spark.ParentPath.getAbsolutePath()+"\\config.sex"));

        if(currentConfig == null)
        {
            if(configs.size() <= 0)
                configs.add(new Config("Default"));
            currentConfig = configs.get(0);
        }

        LoadFromConfig(currentConfig, false);
        if(loadBaritone)
            LoadFromConfig(currentConfig, true);
    }

    public void Save() {
        FileUtil.write(Spark.ParentPath.getAbsolutePath()+"\\config.sex",currentConfig.getConfigName());

        String configFolder = (Spark.ParentPath.getAbsolutePath() + "\\configs");


        deleteUnused:
        for (String s : FileUtil.listFolderForFolder(configFolder)) {
            for (Config c : configs) {
                if(c.getConfigName().equals(s))
                    continue deleteUnused;
            }
            FileUtil.deleteDirectory(configFolder+"\\"+s);
        }
        for (Config c : configs) {
            saveSettingHolder(c,getConfigPath(c)+"\\configData.sex");
        }

        SaveFromConfig(currentConfig);

    }


    public void LoadFromConfig(Config config, boolean baritone) {
        loadSystems(config, baritone);
    }

    public void SaveFromConfig(Config config) {
        saveSystems(config);
    }

    String getSystemSettingFile(SettingsHolder holder,Config config) {
        String base = getConfigPath(config) + "\\systems\\";
        if (holder instanceof Module)
            base = base + "modules\\";
        if (holder instanceof ClientSetting)
            base = base + "clientSettings\\";
        if (holder instanceof HudElement)
            base = base + "huds\\";
        return base + holder.getName() + ".sex";
    }



    private void loadSystems(Config config, boolean baritone) {
        for (SettingsHolder system : SystemManager.getSystems()) {
            if (system instanceof BaritoneConfig && !baritone) continue;
            if (!(system instanceof BaritoneConfig) && baritone) continue;

            loadSettingHolder(system,FileUtil.read(getSystemSettingFile(system,config)));
        }
    }

    void loadSettingHolder(SettingsHolder holder,String s) {
        try {

            if (s != null) {
                String[] List = s.split("\n");
                for (String var : List) {
                    String n = var.split(":")[0];
                    if (var.split(":").length > 1) {
                        String v = var.split(":")[1];

                        if (holder instanceof Module) {
                            if (n.equalsIgnoreCase("Toggled"))
                                ((Module) holder).setEnabled(Boolean.parseBoolean(v));
                            if (n.equalsIgnoreCase("Hold"))
                                ((Module) holder).setHold(Boolean.parseBoolean(v));
                            if (n.equalsIgnoreCase("Visible"))
                                ((Module) holder).setVisible(Boolean.parseBoolean(v));
                            if (n.equalsIgnoreCase("Muted"))
                                ((Module) holder).setMuted(Boolean.parseBoolean(v));
                            if (n.equalsIgnoreCase("KeyBind"))
                                ((Module) holder).setBind(Integer.parseInt(v));
                        }
                        if (holder instanceof HudElement) {
                            if (n.equalsIgnoreCase("Toggled"))
                                ((HudElement) holder).setEnabled(Boolean.parseBoolean(v));
                            if (n.equalsIgnoreCase("PosPercentX"))
                                ((HudElement) holder).setPercentPosX(Double.parseDouble(v));
                            if (n.equalsIgnoreCase("PosPercentY"))
                                ((HudElement) holder).setPercentPosY(Double.parseDouble(v));
                            if (n.equalsIgnoreCase("SnappedElement"))
                                ((HudElement) holder).setSnappedElement(Integer.parseInt(v));
                        }
                        for (Setting<?> setting : holder.getSettings()) {
                            if (n.equalsIgnoreCase(setting.getName()) || n.equalsIgnoreCase(setting.getCategory() + "/" + setting.getName()))
                                setting.setValueString(v);

                        }

                    }

                }
                holder.onConfigLoad();
            }
        } catch (Exception e) {
            Spark.logger.info("Failed to load config for " + holder.getName());
            e.printStackTrace();
        }
    }

    private void saveSystems(Config config) {
        for (SettingsHolder system : SystemManager.getSystems()) {
            saveSettingHolder(system,getSystemSettingFile(system,config));
        }
    }

    void saveSettingHolder(SettingsHolder holder,String file) {
        try {
            ArrayList<String> lines = new ArrayList<String>();

            if (holder instanceof Module) {
                lines.add("Toggled:" + ((Module) holder).isEnabled());
                lines.add("KeyBind:" + ((Module) holder).getBind());
                lines.add("Hold:" + ((Module) holder).isHold());
                lines.add("Visible:" + ((Module) holder).isVisible());
                lines.add("Muted:" + ((Module) holder).isMuted());
            }
            if (holder instanceof HudElement) {
                lines.add("Toggled:" + ((HudElement) holder).isEnabled());
                lines.add("PosPercentX:" + ((HudElement) holder).getPercentPosX());
                lines.add("PosPercentY:" + ((HudElement) holder).getPercentPosY());
                lines.add("SnappedElement:" + ((HudElement) holder).getSnappedElement());
            }

            for (Setting<?> setting : holder.getSettings())
                lines.add(setting.getCategory() + "/" + setting.getName() + ":" + setting.getValueString());

            String content = "";
            for (String e : lines)
                content = content + e + "\n";

            FileUtil.write(file, content);

            holder.onConfigSave();

        } catch (Exception e) {
            Spark.logger.info("Failed to save config for " + holder.getName());
            e.printStackTrace();
        }
    }


    public static class Config extends SettingsHolder {
        public StringSetting name = new StringSetting("Name",this,"","General");
        //todo make this a string list LOL
        StringSetting loadOnIp = new StringSetting("LoadOnIp",this,"","General");

        String fileName;
        public Config(String inName) {
            fileName = inName;
            name.setValue(fileName);
        }

        public void syncFileName() {
            if(fileName != name.getValue())
            {
                String configFolder = (Spark.ParentPath.getAbsolutePath() + "\\configs");
                if(name.getValue().length() > 0 || Spark.configManager.getConfigs().contains(name.getName()) || !FileUtil.renameDirectory(configFolder+"\\"+fileName,configFolder+"\\"+name.getValue()))
                    name.setValue(this.fileName);
            }

            this.fileName = name.getValue();
        }

        public boolean isServer(String ip) {
            return loadOnIp.getValue().equals(ip);
        }

        @Override
        public String getName() {
            return getConfigName();
        }

        public String getConfigName() {
            return fileName;
        }



        @Override
        public boolean equals(Object o)
        {
            if (o == this)
                return true;
            if ((o instanceof Config))
            {
                Config other = (Config)o;
                if(other.getName() == null)
                    return false;
                return other.getName().equalsIgnoreCase(getName());
            }
            if(o instanceof String)
                return ((String)o).equalsIgnoreCase(getName());


            return false;
        }
        @Override
        public int hashCode() {
            return getConfigName().hashCode();
        }
    }
}

