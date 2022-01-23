package me.wallhacks.spark.gui.clickGui.panels.socials.playerLists;

import com.mojang.authlib.GameProfile;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.util.GuiUtil;
import net.minecraft.client.network.NetworkPlayerInfo;
import me.wallhacks.spark.util.SessionUtils;
import me.wallhacks.spark.util.render.RenderUtil;

import java.awt.*;
import java.util.UUID;

public class PlayerListItem extends GuiPanelBase {

    final public UUID playerId;
    NetworkPlayerInfo item;
    final boolean isOn;

    public String getName() {
        return item.getGameProfile().getName();
    }

    public PlayerListItem(UUID playerId) {
        this.playerId = playerId;


        if(mc.player.connection.getPlayerInfo(playerId) == null) {
            this.item = new NetworkPlayerInfo(new GameProfile(playerId,SessionUtils.getname(playerId)));
            Spark.threadManager.execute(() -> {
                SessionUtils.setSkin(item, playerId);
            });
            isOn = false;
        }
        else
        {
            item = mc.player.connection.getPlayerInfo(playerId);
            isOn = true;
        }
    }


    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);

        //drawBackGround(guiSettings.getGuiSubPanelBackgroundColor().getRGB());
        fontManager.drawString(item.getGameProfile().getName(),posX+height,posY+height/2-fontManager.getTextHeight()/2,guiSettings.getContrastColor().getRGB());
        GuiUtil.renderPlayerHead(item,posX+2,posY+2,height-4);


        if(isOn) {
            Color c = new Color(0, 220, 27,240);
            RenderUtil.drawFilledCircle(posX+height-2,posY+height-2,2,c.getRGB());
        }


    }
}
