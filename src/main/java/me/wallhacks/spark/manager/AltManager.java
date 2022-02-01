package me.wallhacks.spark.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.panels.GuiPanelScroll;
import me.wallhacks.spark.util.*;
import me.wallhacks.spark.util.auth.account.MSAccount;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.Session;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.WorldInfo;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import me.wallhacks.spark.gui.panels.GuiPanelInputField;
import me.wallhacks.spark.gui.altList.AltList;
import me.wallhacks.spark.systems.clientsetting.clientsettings.GuiSettings;
import me.wallhacks.spark.util.auth.MSAuth;
import me.wallhacks.spark.util.objects.FakeWorld;
import me.wallhacks.spark.util.objects.Timer;
import me.wallhacks.spark.util.auth.account.Account;
import me.wallhacks.spark.util.auth.account.AccountType;
import me.wallhacks.spark.util.auth.account.MojangAccount;
import scala.xml.dtd.impl.Base;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class AltManager implements MC {
    CopyOnWriteArrayList<Account> accounts;
    public static FakeWorld fakeWorld;
    EntityOtherPlayerMP player;
    Timer timer = new Timer();
    Timer timout = new Timer();
    MSAuth msAuth;
    double offset = 10;
    int fieldOffset;
    int statusOffset;
    boolean addingMojang = false;
    GuiPanelInputField userName;
    GuiPanelInputField password;
    boolean selected;
    AltList altList;
    GuiPanelScroll scroll;
    MojangAccount loginAcc;
    public String status = "";
    boolean justLogin;
    Timer statusTimer = new Timer();

    String key = new RandomString(50).nextString();

    public AltManager() {
        WorldInfo info = new WorldInfo();
        info.setSpawn(new BlockPos(0, 0, 0));
        WorldProvider provider = new WorldProvider() {
            @Override
            public DimensionType getDimensionType() {
                return null;
            }
        };
        fakeWorld = new FakeWorld(info, provider);
        provider.setWorld(fakeWorld);
        userName = new GuiPanelInputField(69, 0, 0, 136, 15, GuiSettings.getInstance().getGuiSettingFieldColor().getRGB(), -1);
        password = new GuiPanelInputField(71, 0, 0, 136, 15, GuiSettings.getInstance().getGuiSettingFieldColor().getRGB(), -1);
        userName.setTextOffsetX(2);
        password.setTextOffsetX(2);
        password.setPassword(true);
        NetworkPlayerInfo playerInfo = new NetworkPlayerInfo(mc.getSession().getProfile());
        player = new EntityOtherPlayerMP(fakeWorld, playerInfo.getGameProfile());
        player.playerInfo = playerInfo;
        player.playerInfo.setGameType(GameType.CREATIVE);
        accounts = new CopyOnWriteArrayList<>();
        loadAlts();
        altList = new AltList();
        altList.setList(this);
        scroll = new GuiPanelScroll(0, 0, 0, 0, altList);
    }
    String getAltFile(String name) {
        String base = Spark.ParentPath.getAbsolutePath() + "\\alts\\";
        return base + name + ".acc";
    }




    public void loadAlts() {
        //get the key
        File authKey = new File(Spark.ParentPath.getAbsolutePath() + "\\alts\\auth.key");
        if (authKey.exists()) {
            key = FileUtil.read(authKey.getAbsolutePath());
            for (String file : FileUtil.listFilesForFolder(Spark.ParentPath.getAbsolutePath() + "\\alts", ".acc")) {
                try {
                    FileInputStream fi_stream = new FileInputStream(Spark.ParentPath.getAbsolutePath() + "\\alts" + "\\" + file);
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
                            addAlt(acc);
                            break;
                        }
                        case "MICROSOFT": {
                            String uuid = br.readLine();
                            String refresh = br.readLine();
                            refresh = EncryptionUtil.decrypt(refresh, key);
                            uuid = EncryptionUtil.decrypt(uuid, key);
                            MSAccount acc = new MSAccount(null, refresh, backupName, uuid);
                            acc.refresh();
                            addAlt(acc);
                            break;
                        }
                        case "CRACKED": {
                            addAlt(new Account(new Session(br.readLine(), "", "", "Mojang"), AccountType.CRACKED, ""));
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

    public void render(int mouseX, int mouseY, float deltaTime) {
        if (!player.getGameProfile().equals(mc.getSession().getProfile()))
            refreshSkin();



        ScaledResolution sr = new ScaledResolution(mc);
        FontManager fontManager = Spark.fontManager;
        int width = sr.getScaledWidth();
        int height = sr.getScaledHeight();
        if (mouseX > width - offset - 20 || !timout.passedS(1)) {
            if (mouseX > width - offset - 20)
                timout.reset();
            if (offset < 150)
                offset += deltaTime;
        } else if (timout.passedS(1)) {
            if (offset > 5)
                offset -= deltaTime*2;
        }
        if (addingMojang) {
            if (fieldOffset < 53) {
                fieldOffset += timer.getPassedTimeMs() / 2;
            }
        } else if (fieldOffset != 0) {
            fieldOffset -= timer.getPassedTimeMs() / 2;
        }
        if (statusOffset < 15 && !status.equals("")) {
            statusOffset += timer.getPassedTimeMs() / 2;
        } else if (statusOffset > 0 && status.equals("")) {
            statusOffset -= timer.getPassedTimeMs() / 2;
        }
        offset = MathHelper.clamp(offset, 5, 145);

        GL11.glPushMatrix();
        GL11.glTranslated(-offset,0,0);

        GuiUtil.addGlScissorOffset(new Vec3d(-offset,0,0));


        mouseX += offset;

        statusOffset = MathHelper.clamp(statusOffset, 0, 15);
        fieldOffset = MathHelper.clamp(fieldOffset, 0, 53);

        timer.reset();
        boolean click = false;
        if (Mouse.isButtonDown(0)) {
            if (!selected) {
                selected = true;
                click = true;
            }
        } else selected = false;
        Gui.drawRect(width, 0, width + 5, height, GuiSettings.getInstance().getGuiMainPanelBackgroundColor().getRGB());
        Gui.drawRect(width, 0, width+150, height, GuiSettings.getInstance().getGuiScreenBackgroundColor().getRGB());
        Gui.drawRect(width + 5, 0, width+150, 13, GuiSettings.getInstance().getGuiMainPanelBackgroundColor().getRGB());
        Gui.drawRect(width + 5, height - 45, width+150, height, GuiSettings.getInstance().getGuiMainPanelBackgroundColor().getRGB());
        Gui.drawRect(width + 5, 13, width+150, 15, GuiSettings.getInstance().getMainColor().getRGB());
        Gui.drawRect(width + 5, height - 47 - fieldOffset - statusOffset, width+150, height - fieldOffset - 45 - statusOffset, GuiSettings.getInstance().getMainColor().getRGB());
        Gui.drawRect(width + 5, height - 45 - fieldOffset - statusOffset, width+150, height - 45, GuiSettings.getInstance().getGuiMainPanelBackgroundColor().getRGB());
        if (fieldOffset != 0) {
            password.posX = width  + 7;
            password.posY = height - 26 - fieldOffset;
            userName.posX = width + 7;
            userName.posY = height - 43 - fieldOffset;
            GuiUtil.glScissor((width - (int) offset + 5), height-(90+fieldOffset), width, 45 + fieldOffset);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            userName.renderContent(mouseX, mouseY, 0);
            password.renderContent(mouseX, mouseY, 0);
            if (password.getText().equals("") && !password.isFocused())
                fontManager.drawString("password " + TextFormatting.DARK_GRAY + "(leave blank for cracked)", width  + 11, height - 22 - fieldOffset, new Color(0xAFAFAF).getRGB());
            if (userName.getText().equals("") && !userName.isFocused())
                fontManager.drawString("username", width + 11, height - 39 - fieldOffset, new Color(0xAFAFAF).getRGB());
            boolean add = GuiUtil.drawButton("Add", width  + 7, height - 9 - fieldOffset, width + 74, height + 6 - fieldOffset, GuiSettings.getInstance().getMainColor(), mouseX, mouseY, loginAcc != null);
            boolean login = GuiUtil.drawButton("Login", width + 76, height - 9 - fieldOffset, width  + 143, height + 6 - fieldOffset, GuiSettings.getInstance().getMainColor(), mouseX, mouseY, loginAcc != null);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            if (loginAcc == null) {
                if (click) {
                    password.setFocused(false);
                    userName.setFocused(false);
                    if (add || login) {
                        if (password.getText() != "") {
                            loginAcc = new MojangAccount(userName.getText(), password.getText(), "", "test");
                            loginAcc.setSession();
                            password.setText("");
                            justLogin = login;
                        } else if (userName.getText() != "") {
                            Session s = new Session(userName.getText(), "", "", "Mojang");
                            if (add) {
                                Account account = new Account(s, AccountType.CRACKED, "");
                                addAlt(account);
                                refresh();
                            } else {
                                mc.session = s;
                            }
                            addingMojang = false;
                            password.setText("");
                        }
                    } else {
                        if (password.isPosIn(mouseX, mouseY))
                            password.setFocused(true);
                        else if (userName.isPosIn(mouseX, mouseY))
                            userName.setFocused(true);
                    }
                }
            } else {
                statusTimer.reset();
                if (loginAcc.session == null) {
                    if (loginAcc.invalid) {
                        status = ChatFormatting.RED + "Wrong password or username.";
                        loginAcc = null;
                    } else {
                        status = GuiUtil.getLoadingText(true);
                    }
                } else {
                    status = "Logged in to " + loginAcc.getName() + "!";
                    if (!justLogin) {
                        addAlt(loginAcc);
                        refresh();
                    } else {
                        mc.session = loginAcc.session;
                    }
                    addingMojang = false;
                    userName.setText("");
                    loginAcc = null;
                }
            }
        }
        if (msAuth != null) {
            statusTimer.reset();
            if (msAuth.acc != null) {
                this.addAlt(msAuth.acc);
                status = "Logged in to " + msAuth.acc.getName();
                msAuth = null;
                refresh();
            } else if (msAuth.failed) {
                msAuth.stop();
                msAuth = null;
            } else status = msAuth.getStatus();
        }
        if (statusTimer.passedMs(2000)) {
            status = "";
        }
        boolean ms = GuiUtil.drawButton(msAuth == null ? "Add MS" : "Cancel", width + 105, height - 40, width + 140, height - 25, GuiSettings.getInstance().getMainColor(), mouseX, mouseY, addingMojang || loginAcc != null);
        boolean mj = GuiUtil.drawButton(addingMojang ? "Cancel" : "Add Alt", width  + 105, height - 20, width + 140, height - 5, GuiSettings.getInstance().getMainColor(), mouseX, mouseY, loginAcc != null || msAuth != null);
        if (statusOffset == 15) {
            fontManager.drawString(status, width + 7, height - 46 - fieldOffset - 10, -1);
        }
        if (click) {
            if (ms) {
                if (msAuth == null)
                    msAuth = new MSAuth();
                else if (msAuth.stop()) {
                    msAuth = null;
                    status = "Cancelled!";
                } else status = "failed to stop server";
            } else if (mj) {
                addingMojang = !addingMojang;
                password.setText("");
                userName.setText("");
                password.setFocused(false);
                userName.setFocused(false);
            }
        }
        fontManager.drawString("Account-Manager", width+65 - fontManager.getTextWidth("Alt Manager") / 2, 4, -1);
        scroll.setPositionAndSize((width + 1), 17, 145, height - 66 - fieldOffset - statusOffset);
        scroll.renderContent(mouseX, mouseY, deltaTime);
        String name = mc.getSession().getProfile().getName();
        boolean cracked = mc.getSession().getToken().equals("");
        GuiUtil.drawEntityOnScreen(width  + 17, height - 4, 19, mouseX, mouseY, player);
        fontManager.drawString(name, width  + 31, height - 40, -1);
        fontManager.drawString((cracked ? TextFormatting.RED + "Cracked" : TextFormatting.GREEN + "Premium"), width + 31, height - 29, -1);

        GuiUtil.addGlScissorOffset(new Vec3d(offset,0,0));
        GL11.glPopMatrix();
    }

    public boolean isMouseIn(int mouseX) {
        int width = new ScaledResolution(mc).getScaledWidth();
        return mouseX > width - offset;
    }

    private void refreshSkin() {
        NetworkPlayerInfo playerInfo = new NetworkPlayerInfo(mc.getSession().getProfile());
        SessionUtils.setSkin(playerInfo, playerInfo.getGameProfile().getId());
        player = new EntityOtherPlayerMP(fakeWorld, playerInfo.getGameProfile());
        player.playerInfo = playerInfo;
        Spark.logger.info(playerInfo.getGameProfile().getId().toString());
    }

    public void refresh() {
        altList.setList(this);
    }

    public void keyTyped(int keyCode, char typedChar) {
        userName.onKey(keyCode, typedChar);
        password.onKey(keyCode, typedChar);
    }

    public void removeAlt(Account account) {
        accounts.remove(account);
        FileUtil.deleteFile(getAltFile(account.getName()));

    }
    public void addAlt(Account account) {
        accounts.add(account);
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
    public CopyOnWriteArrayList<Account> getAlts() {
        return accounts;
    }


}
