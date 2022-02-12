package me.wallhacks.spark.systems.hud.huds;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.systems.hud.InfoHudElement;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import net.minecraft.client.Minecraft;

@HudElement.Registration(name = "FPS", description = "Shows you your fps", posX = 0.0, posY = 0.5, width = 30, height = 12)
public class FPS extends InfoHudElement {
    BooleanSetting optifine = new BooleanSetting("Optifine", this, false);

    @Override
    public void draw(float deltaTime) {
        setInfo(String.format(ChatFormatting.GRAY + "FPS %s%s", ChatFormatting.WHITE, (int) (Minecraft.getDebugFPS() * (optifine.getValue() ? 1.5 : 1))));
        drawInfo();
    }
}
