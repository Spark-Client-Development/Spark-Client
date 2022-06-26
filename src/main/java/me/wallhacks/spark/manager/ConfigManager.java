package me.wallhacks.spark.manager;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.systems.clientsetting.ClientSetting;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.Setting;
import me.wallhacks.spark.systems.setting.SettingGroup;
import me.wallhacks.spark.util.FileUtil;
import me.wallhacks.spark.util.MC;

import java.util.ArrayList;

public class ConfigManager implements MC {


    public ConfigManager() {
        Spark.eventBus.register(this);
    }


    String getConfigPath() {
        return Spark.ParentPath.getAbsolutePath() + System.getProperty("file.separator") + "configs";
    }


    public void Load() {

        LoadFromConfig();

    }


    public void Save() {


        SaveFromConfig(false);

    }


    public void LoadFromConfig() {
        loadSystems();
    }

    public void SaveFromConfig(boolean saveDefaults) {
        saveSystems(saveDefaults);
    }

    String getSystemSettingFile(SettingsHolder holder) {
        String base = getConfigPath() + System.getProperty("file.separator") + "systems" + System.getProperty("file.separator");
        if (holder instanceof Module)
            base = base + "modules" + System.getProperty("file.separator");
        if (holder instanceof ClientSetting)
            base = base + "clientSettings" + System.getProperty("file.separator");
        if (holder instanceof HudElement)
            base = base + "huds" + System.getProperty("file.separator");
        return base + holder.getName() + ".cfg";
    }


    private void loadSystems() {
        for (SettingsHolder system : SystemManager.getSystems()) {
            loadSettingHolder(system, getSystemSettingFile(system));
        }
    }

    void loadSettingHolder(SettingsHolder holder, String file) {
        try {
            String s = FileUtil.read(file);
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
                            if (n.equalsIgnoreCase(setting.getName()) || n.equalsIgnoreCase(setting.getsettingsHolder().getName() + "/" + setting.getName()))
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

    private void saveSystems(boolean saveDefaults) {
        for (SettingsHolder system : SystemManager.getSystems()) {
            if (system instanceof SettingGroup) continue;
            saveSettingHolder(system, getSystemSettingFile(system), saveDefaults);
        }
    }

    void saveSettingHolder(SettingsHolder holder, String file, boolean saveDefaults) {
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
                lines.add(setting.getsettingsHolder().getName() + "/" + setting.getName() + ":" + (saveDefaults ? setting.getDefaultValueString() : setting.getValueString()));

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


}

