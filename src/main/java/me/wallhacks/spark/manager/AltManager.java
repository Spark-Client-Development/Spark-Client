package me.wallhacks.spark.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.panels.GuiPanelScroll;
import me.wallhacks.spark.util.GuiUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.Session;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.auth.MSAuth;
import me.wallhacks.spark.util.SessionUtils;
import me.wallhacks.spark.util.objects.FakeWorld;
import me.wallhacks.spark.util.objects.Timer;
import me.wallhacks.spark.util.auth.account.Account;
import me.wallhacks.spark.util.auth.account.AccountType;
import me.wallhacks.spark.util.auth.account.MojangAccount;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class AltManager implements MC {
    public CopyOnWriteArrayList<Account> accounts;
    public static FakeWorld fakeWorld;
    EntityOtherPlayerMP player;
    Timer timer = new Timer();
    Timer timout = new Timer();
    MSAuth msAuth;
    int offset = 10;
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
        Spark.configManager.loadAlts(this);
        altList = new AltList();
        altList.setList(this);
        scroll = new GuiPanelScroll(0, 0, 0, 0, altList);
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
                offset += timer.getPassedTimeMs();
        } else if (timout.passedS(1)) {
            if (offset > 5)
                offset -= timer.getPassedTimeMs();
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
        statusOffset = MathHelper.clamp(statusOffset, 0, 15);
        fieldOffset = MathHelper.clamp(fieldOffset, 0, 53);
        offset = MathHelper.clamp(offset, 5, 145);
        timer.reset();
        boolean click = false;
        if (Mouse.isButtonDown(0)) {
            if (!selected) {
                selected = true;
                click = true;
            }
        } else selected = false;
        Gui.drawRect(width - offset, 0, width - offset + 5, height, GuiSettings.getInstance().getGuiMainPanelBackgroundColor().getRGB());
        Gui.drawRect(width - offset, 0, width, height, GuiSettings.getInstance().getGuiScreenBackgroundColor().getRGB());
        Gui.drawRect(width - offset + 5, 0, width, 13, GuiSettings.getInstance().getGuiMainPanelBackgroundColor().getRGB());
        Gui.drawRect(width - offset + 5, height - 45, width, height, GuiSettings.getInstance().getGuiMainPanelBackgroundColor().getRGB());
        Gui.drawRect(width - offset + 5, 13, width, 15, GuiSettings.getInstance().getMainColor().getRGB());
        Gui.drawRect(width - offset + 5, height - 47 - fieldOffset - statusOffset, width, height - fieldOffset - 45 - statusOffset, GuiSettings.getInstance().getMainColor().getRGB());
        Gui.drawRect(width - offset + 5, height - 45 - fieldOffset - statusOffset, width, height - 45, GuiSettings.getInstance().getGuiMainPanelBackgroundColor().getRGB());
        if (fieldOffset != 0) {
            password.posX = width - offset + 7;
            password.posY = height - 26 - fieldOffset;
            userName.posX = width - offset + 7;
            userName.posY = height - 43 - fieldOffset;
            GL11.glScissor((width - offset + 5)* 2, 90, width*2, 90 + fieldOffset * 2);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            userName.renderContent(mouseX, mouseY, 0);
            password.renderContent(mouseX, mouseY, 0);
            if (password.getText().equals("") && !password.isFocused())
                fontManager.drawString("password " + TextFormatting.DARK_GRAY + "(leave blank for cracked)", width - offset + 11, height - 22 - fieldOffset, new Color(0xAFAFAF).getRGB());
            if (userName.getText().equals("") && !userName.isFocused())
                fontManager.drawString("username", width - offset + 11, height - 39 - fieldOffset, new Color(0xAFAFAF).getRGB());
            boolean add = GuiUtil.drawButton("Add", width - offset + 7, height - 9 - fieldOffset, width - offset + 74, height + 6 - fieldOffset, GuiSettings.getInstance().getMainColor(), mouseX, mouseY, loginAcc != null);
            boolean login = GuiUtil.drawButton("Login", width - offset + 76, height - 9 - fieldOffset, width - offset + 143, height + 6 - fieldOffset, GuiSettings.getInstance().getMainColor(), mouseX, mouseY, loginAcc != null);
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
                                accounts.add(account);
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
                        accounts.add(loginAcc);
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
                this.accounts.add(msAuth.acc);
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
        boolean ms = GuiUtil.drawButton(msAuth == null ? "Add MS" : "Cancel", width - offset + 105, height - 40, width - offset + 140, height - 25, GuiSettings.getInstance().getMainColor(), mouseX, mouseY, addingMojang || loginAcc != null);
        boolean mj = GuiUtil.drawButton(addingMojang ? "Cancel" : "Add Alt", width - offset + 105, height - 20, width - offset + 140, height - 5, GuiSettings.getInstance().getMainColor(), mouseX, mouseY, loginAcc != null || msAuth != null);
        if (statusOffset == 15) {
            fontManager.drawString(status, width - offset + 7, height - 46 - fieldOffset - 10, -1);
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
        fontManager.drawString("Account-Manager", width - offset + 75 - fontManager.getTextWidth("Alt Manager") / 2, 4, -1);
        scroll.setPositionAndSize((width - offset + 1), 17, 145, height - 66 - fieldOffset - statusOffset);
        scroll.renderContent(mouseX, mouseY, deltaTime);
        String name = mc.getSession().getProfile().getName();
        boolean cracked = mc.getSession().getToken().equals("");
        GuiUtil.drawEntityOnScreen(width - offset + 17, height - 4, 19, mouseX, mouseY, player);
        fontManager.drawString(name, width - offset + 31, height - 40, -1);
        fontManager.drawString((cracked ? TextFormatting.RED + "Cracked" : TextFormatting.GREEN + "Premium"), width - offset + 31, height - 29, -1);
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
}
