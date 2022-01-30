package me.wallhacks.spark.gui.altList;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.lwjgl.input.Mouse;
import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.manager.AltManager;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.SessionUtils;
import me.wallhacks.spark.util.auth.account.Account;
import me.wallhacks.spark.util.objects.ThreadSkin;

public class AltEntry extends GuiPanelBase {
    Account account;
    EntityOtherPlayerMP player;
    public AltEntry(Account account) {
        this.account = account;
        player = new EntityOtherPlayerMP(AltManager.fakeWorld, new GameProfile(SessionUtils.fromString(account.getUUID()), account.getName()));
        player.playerInfo = new NetworkPlayerInfo(player.getGameProfile());
        ThreadSkin threadSkin = new ThreadSkin(player.playerInfo);
        threadSkin.start();
    }

    @Override
    public void renderContent(int mouseX, int mouseY, float deltaTime) {
        super.renderContent(mouseX,mouseY,deltaTime);
        final ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        int i1 = scaledresolution.getScaledWidth();
        int j1 = scaledresolution.getScaledHeight();
        final int x = Mouse.getX() * i1 / this.mc.displayWidth;
        final int y = j1 - Mouse.getY() * j1 / this.mc.displayHeight - 1;
        fontManager.drawString(account.getName(),posX+ 22, posY + 4, -1);
        if (player != null)
            GuiUtil.drawEntityOnScreen(posX+10,posY+34,16, mouseX, mouseY, player);
        fontManager.drawString(account.getStatus(),posX+ 22,posY+13, -1);
    }
}
