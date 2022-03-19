package me.wallhacks.spark.gui.clickGui.panels.socials.playerLists;

import com.mojang.authlib.GameProfile;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelBase;
import me.wallhacks.spark.manager.SocialManager;
import me.wallhacks.spark.util.GuiUtil;
import net.minecraft.client.network.NetworkPlayerInfo;
import me.wallhacks.spark.util.SessionUtils;
import me.wallhacks.spark.util.render.RenderUtil;

import java.awt.*;
import java.util.UUID;

public class PlayerListItem extends GuiPanelBase {

    public SocialManager.SocialEntry player;
    NetworkPlayerInfo item;


    public String getName() {
        if (item != null)
            return item.getGameProfile().getName();
        else return player.getName();
    }

    public PlayerListItem(SocialManager.SocialEntry player) {
        this.player = player;
        Setup();
    }
    public PlayerListItem(NetworkPlayerInfo info) {
        item = info;
        Spark.threadManager.execute(
                () -> {
                    this.player = SocialManager.getSocialFromNetworkPlayerInfo(info);
                    Setup();
                }
        );
    }


    void Setup() {
        if(mc.player.connection.getPlayerInfo(player.getName()) == null) {

            // fb43302e-b957-46af-822d-7742d624dd24 is just a placeholder for null LOL
            Spark.threadManager.execute(() -> {
                this.item = new NetworkPlayerInfo(new GameProfile(player.getUUID() != null ? player.getUUID() : UUID.fromString("fb43302e-b957-46af-822d-7742d624dd24"),player.getName()));
                SessionUtils.setSkin(item, player.getUUID());
            });
        }
        else
        {
            item = mc.player.connection.getPlayerInfo(player.getName());
        }

    }

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);

        if(item == null || player == null)
            return;

        //drawBackGround(guiSettings.getGuiSubPanelBackgroundColor().getRGB());
        fontManager.drawString(item.gameProfile.getName(),posX+height,posY+height/2-fontManager.getTextHeight()/2,guiSettings.getContrastColor().getRGB());

        GuiUtil.renderPlayerHead(item,posX+2,posY+2,height-4);


        if(mc.player.connection.getPlayerInfo(player.getName()) != null) {
            Color c = new Color(0, 220, 27,240);
            RenderUtil.drawFilledCircle(posX+height-2,posY+height-2,2,c.getRGB());
        }


    }
}
