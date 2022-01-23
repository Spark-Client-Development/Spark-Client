package me.wallhacks.spark.manager;

import me.wallhacks.spark.Spark;
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
    public final File ParentPath;

    public ConfigManager() {
        ParentPath = new File(Minecraft.getMinecraft().gameDir.getParent(), Spark.MODID);
    }

    public void Load() {
        loadSystems();
        loadFriends();
    }

    public void Save() {
        saveSystems();
        saveFriends();
        saveAlts();
    }

    String getSystemSettingFile(SettingsHolder holder) {
        String base = ParentPath.getAbsolutePath() + "\\systems\\";
        if (holder instanceof Module)
            base = base + "modules\\";
        if (holder instanceof ClientSetting)
            base = base + "clientSettings\\";
        if (holder instanceof HudElement)
            base = base + "huds\\";
        return base + holder.getName() + ".sex";
    }

    private void saveAlts() {
        //delete old alt directory
        FileUtil.deleteDirectory(ParentPath.getAbsolutePath() + "\\alts");

        //start by making random string for auth key
        String key = new RandomString(50).nextString();
        //save the key
        FileUtil.write(ParentPath.getAbsolutePath() + "\\alts\\auth.key", key);
        for (Account account : Spark.altManager.accounts) {
            try {
                ArrayList<String> lines = new ArrayList<>();
                switch (account.accountType) {
                    //save accounts encrypted with the key
                    case MICROSOFT: {
                        lines.add("MICROSOFT");
                        lines.add(EncryptionUtil.encrypt(account.getUUID(), key));
                        lines.add(EncryptionUtil.encrypt(((MSAccount) account).getRefreshToken(), key));
                        break;
                    }
                    case MOJANG: {
                        lines.add("MOJANG");
                        lines.add(EncryptionUtil.encrypt(account.getUUID(), key));
                        lines.add(EncryptionUtil.encrypt(((MojangAccount) account).getMail(), key));
                        lines.add(EncryptionUtil.encrypt(((MojangAccount) account).getPassword(), key));
                        break;
                    }
                    case CRACKED: {
                        lines.add("CRACKED");
                        lines.add(account.getName());
                        break;
                    }
                }
                String content = "";
                for (String e : lines)
                    content = content + e + "\n";

                FileUtil.write(getAltFile(account.getName()), content);
            } catch (Exception fucked) {
                //saik
            }
        }
    }

    public void loadAlts(AltManager altManager) {
        //get the key
        File authKey = new File(ParentPath.getAbsolutePath() + "\\alts\\auth.key");
        if (authKey.exists()) {
            String key = FileUtil.read(authKey.getAbsolutePath());
            for (String file : FileUtil.listFilesForFolder(ParentPath.getAbsolutePath() + "\\alts", ".acc")) {
                try {
                    FileInputStream fi_stream = new FileInputStream(ParentPath.getAbsolutePath() + "\\alts" + "\\" + file);
                    DataInputStream di_stream = new DataInputStream(fi_stream);
                    BufferedReader br = new BufferedReader(new InputStreamReader(di_stream));
                    String backupName = file.substring(0, file.length() - 4);
                    switch (br.readLine()) {
                        case "MOJANG": {
                            String uuid = br.readLine();
                            String mail = br.readLine();
                            String password = br.readLine();
                            mail = EncryptionUtil.decrypt(mail, key);
                            password = EncryptionUtil.decrypt(password, key);
                            uuid = EncryptionUtil.decrypt(uuid, key);
                            MojangAccount acc = new MojangAccount(mail, password, backupName, uuid);
                            altManager.accounts.add(acc);
                            break;
                        }
                        case "MICROSOFT": {
                            String uuid = br.readLine();
                            String refresh = br.readLine();
                            refresh = EncryptionUtil.decrypt(refresh, key);
                            uuid = EncryptionUtil.decrypt(uuid, key);
                            MSAccount acc = new MSAccount(null, refresh, backupName, uuid);
                            acc.refresh();
                            altManager.accounts.add(acc);
                            break;
                        }
                        case "CRACKED": {
                            altManager.accounts.add(new Account(new Session(br.readLine(), "", "", "Mojang"), AccountType.CRACKED, ""));
                            break;
                        }
                    }
                } catch (Exception fucked) {
                    Spark.logger.info("TEST");
                    fucked.printStackTrace();
                }
            }
        }
    }

    private void loadSystems() {
        for (SettingsHolder system : SystemManager.getSystems()) {
            try {
                String s = FileUtil.read(getSystemSettingFile(system));
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

    private void saveSystems() {
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

                FileUtil.write(getSystemSettingFile(system), content);

                system.onConfigSave();

            } catch (Exception e) {
                Spark.logger.info("Failed to save config for " + system.getName());
                e.printStackTrace();
            }
        }
    }


    String getFriendsFile() {
        String base = ParentPath.getAbsolutePath() + "\\socials\\";
        return base + "friends.sex";
    }

    String getAltFile(String name) {
        String base = ParentPath.getAbsolutePath() + "\\alts\\";
        return base + name + ".acc";
    }

    private void loadFriends() {
        try {
            String s = FileUtil.read(getFriendsFile());
            if (s != null) {
                String[] List = s.split("\n");
                Spark.socialManager.clearFriends();
                for (String var : List) {
                    if (var != "") {
                        UUID uuid = UUID.fromString(var);
                        Spark.socialManager.addFriend(uuid);
                    }
                }

            }
        } catch (Exception e) {
            Spark.logger.info("Failed to load friends");
            e.printStackTrace();
        }
    }

    private void saveFriends() {
        try {
            ArrayList<String> lines = new ArrayList<String>();

            String content = "";
            for (UUID e : Spark.socialManager.getFriends())
                content = content + e.toString() + "\n";

            FileUtil.write(getFriendsFile(), content);


        } catch (Exception e) {
            Spark.logger.info("Failed to save friends");
            e.printStackTrace();
        }
    }


}
