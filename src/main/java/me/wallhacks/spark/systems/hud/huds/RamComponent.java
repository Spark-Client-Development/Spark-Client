package me.wallhacks.spark.systems.hud.huds;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.systems.hud.InfoHudElement;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@HudElement.Registration(name = "Ram", posX = 0.2, posY = 0.9, width = 0, height = 0, description = "Shows your ram in the hud")
public class RamComponent extends InfoHudElement {
    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        setInfo(ChatFormatting.GRAY + "Ram: " + ChatFormatting.WHITE + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1048576 + "/" +Runtime.getRuntime().maxMemory()/1048576);
    }

    @Override
    public void draw(float deltaTime) {
        drawInfo();
    }
}
