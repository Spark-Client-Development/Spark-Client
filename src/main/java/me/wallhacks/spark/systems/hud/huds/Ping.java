package me.wallhacks.spark.systems.hud.huds;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.systems.hud.InfoHudElement;
import net.minecraft.client.Minecraft;

@HudElement.Registration(name = "Ping", posX = 0, posY = 0.6, width = 0, height = 0, description = "Shows your ping in the hud")
public class Ping extends InfoHudElement {
    @Override
    public void draw(float deltaTime) {
        setInfo(String.format(ChatFormatting.GRAY + "Ping %s%s", ChatFormatting.WHITE, (int) (mc.getCurrentServerData() != null ? mc.currentServerData.pingToServer : -1)));
        drawInfo();
    }
}
