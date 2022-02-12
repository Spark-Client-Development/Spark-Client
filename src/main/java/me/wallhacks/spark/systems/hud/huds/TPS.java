package me.wallhacks.spark.systems.hud.huds;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.systems.hud.InfoHudElement;
import me.wallhacks.spark.util.MathUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@HudElement.Registration(name = "TPS", description = "Shows you the server tickrate", posX = 0.0, posY = 0.55, width = 30, height = 12)
public class TPS extends InfoHudElement {
    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        setInfo(String.format(ChatFormatting.GRAY + "TPS %s%s", ChatFormatting.WHITE, MathUtil.roundAvoid(Spark.tickManager.getTickRate(), 2)));
    }

    @Override
    public void draw(float d) {
        drawInfo();
    }
}
