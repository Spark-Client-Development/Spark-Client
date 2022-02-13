package me.wallhacks.spark.manager;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.client.SettingChangeEvent;
import me.wallhacks.spark.systems.clientsetting.clientsettings.BaritoneConfig;
import me.wallhacks.spark.systems.setting.settings.StringSetting;
import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.systems.clientsetting.ClientSetting;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.Setting;
import me.wallhacks.spark.util.FileUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class ConfigManager {

    ArrayList<Config> configs = new ArrayList<>();
    Config currentConfig;

    public ConfigManager() {
        Spark.eventBus.register(this);
    }


    //rename file lol
    @SubscribeEvent
    void OnSettingChangeEvent(SettingChangeEvent event) {
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
        return Spark.ParentPath.getAbsolutePath() + System.getProperty("file.separator")+"configs"+System.getProperty("file.separator") + config.getConfigName();
    }

    public void copyAndPasteConfig(Config from,Config to) {

        FileUtil.copy(getConfigPath(from),getConfigPath(to));

        if(to == currentConfig)
            loadConfig(currentConfig,false);
    }

    public boolean loadConfig(Config config,boolean saveOld) {
        if(!configs.contains(config))
            return false;

        if(saveOld)
            SaveFromConfig(currentConfig,false);
        currentConfig = config;

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
        SaveFromConfig(config,true);
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
        String configFolder = (Spark.ParentPath.getAbsolutePath() + System.getProperty("file.separator")+"configs");
        for (String s : FileUtil.listFolderForFolder(configFolder)) {
            Config c = new Config(s);
            configs.add(c);
            loadSettingHolder(c,getConfigPath(c)+System.getProperty("file.separator")+"configData.sex");
            c.name.setValue(s);
        }
        if(FileUtil.exists(Spark.ParentPath.getAbsolutePath()+System.getProperty("file.separator")+"config.sex"))
            currentConfig = getConfigFromName(FileUtil.read(Spark.ParentPath.getAbsolutePath()+System.getProperty("file.separator")+"config.sex"));

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

    public void SaveConfigConfigs(boolean overrideUnused) {
        FileUtil.write(Spark.ParentPath.getAbsolutePath()+System.getProperty("file.separator")+"config.sex",currentConfig.getConfigName());
        for (Config c : configs) {
            saveSettingHolder(c,getConfigPath(c)+System.getProperty("file.separator")+"configData.sex",true);
        }

        String configFolder = (Spark.ParentPath.getAbsolutePath() + System.getProperty("file.separator")+"configs");

        if(overrideUnused)
        {
            deleteUnused:
            for (String s : FileUtil.listFolderForFolder(configFolder)) {
                for (Config c : configs) {
                    if(c.getConfigName().equals(s))
                        continue deleteUnused;
                }
                FileUtil.deleteDirectory(configFolder+System.getProperty("file.separator")+s);
            }
        }
    }

    public void Save() {

        SaveConfigConfigs(true);


        SaveFromConfig(currentConfig,false);

    }


    public void LoadFromConfig(Config config, boolean baritone) {
        loadSystems(config, baritone);
    }

    public void SaveFromConfig(Config config,boolean saveDefaults) {
        saveSystems(config,saveDefaults);
    }

    String getSystemSettingFile(SettingsHolder holder,Config config) {
        String base = getConfigPath(config) + System.getProperty("file.separator")+"systems"+System.getProperty("file.separator");
        if (holder instanceof Module)
            base = base + "modules"+System.getProperty("file.separator");
        if (holder instanceof ClientSetting)
            base = base + "clientSettings"+System.getProperty("file.separator");
        if (holder instanceof HudElement)
            base = base + "huds"+System.getProperty("file.separator");
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
                            if (n.equalsIgnoreCase("PercentPosSnappedX"))
                                ((HudElement) holder).setPercentPosSnappedX(Integer.parseInt(v));
                            if (n.equalsIgnoreCase("PercentPosSnappedY"))
                                ((HudElement) holder).setPercentPosSnappedY(Integer.parseInt(v));

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

    private void saveSystems(Config config,boolean saveDefaults) {
        for (SettingsHolder system : SystemManager.getSystems()) {
            saveSettingHolder(system,getSystemSettingFile(system,config),saveDefaults);
        }
    }

    void saveSettingHolder(SettingsHolder holder,String file,boolean saveDefaults) {
        try {
            ArrayList<String> lines = new ArrayList<String>();

            if (holder instanceof Module) {
                lines.add("Toggled:" + (saveDefaults ? ((Module) holder).getMod().enabled() : ((Module) holder).isEnabled()));
                lines.add("KeyBind:" + (saveDefaults ? ((Module) holder).getMod().bind() : ((Module) holder).getBind()));
                lines.add("Hold:" + (saveDefaults ? ((Module) holder).getMod().hold() : ((Module) holder).isHold()));
                lines.add("Visible:" + (saveDefaults ? ((Module) holder).getMod().visible() : ((Module) holder).isVisible()));
                lines.add("Muted:" + (saveDefaults ? ((Module) holder).getMod().muted() : ((Module) holder).isMuted()));
            }
            if (holder instanceof HudElement) {
                lines.add("Toggled:" + (saveDefaults ? ((HudElement) holder).getMod().enabled() : ((HudElement) holder).isEnabled()));
                lines.add("PosPercentX:" + (saveDefaults ? ((HudElement) holder).getMod().posX() : ((HudElement) holder).getPercentPosX()));
                lines.add("PosPercentY:" + (saveDefaults ? ((HudElement) holder).getMod().posY() : ((HudElement) holder).getPercentPosY()));
                lines.add("SnappedElement:" + (saveDefaults ? ((HudElement) holder).getMod().snappedElement() : ((HudElement) holder).getSnappedElement()));
                lines.add("PercentPosSnappedX:" + (saveDefaults ? ((HudElement) holder).getMod().snappedXPos() : ((HudElement) holder).getPercentPosSnappedX()));
                lines.add("PercentPosSnappedY:" + (saveDefaults ? ((HudElement) holder).getMod().snappedYPos() : ((HudElement) holder).getPercentPosSnappedY()));

            }

            for (Setting<?> setting : holder.getSettings())
                lines.add(setting.getCategory() + "/" + setting.getName() + ":" + (saveDefaults ? setting.getDefaultValueString() : setting.getValueString()));

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
                String configFolder = (Spark.ParentPath.getAbsolutePath() + System.getProperty("file.separator")+"configs");
                if(name.getValue().length() <= 0 || Spark.configManager.getConfigs().contains(name.getName()) || !FileUtil.renameDirectory(configFolder+System.getProperty("file.separator")+fileName,configFolder+System.getProperty("file.separator")+name.getValue()))
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

