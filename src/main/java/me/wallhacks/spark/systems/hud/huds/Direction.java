package me.wallhacks.spark.systems.hud.huds;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.systems.hud.InfoHudElement;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

@HudElement.Registration(name = "Direction", description = "Shows you the direction you are looking in", posY = 0.3, posX = 0, height = 0, width = 0)
public class Direction extends InfoHudElement {
    @Override
    public void draw(float deltaTime) {
        setInfo(String.format("%s" + " " + ChatFormatting.GRAY + "%s", this.getFacing(), this.getTowards()));
        drawInfo();
    }

    private String getFacing()
    {
        switch (MathHelper.floor((double) (Minecraft.getMinecraft().player.rotationYaw * 8.0F / 360.0F) + 0.5D) & 7)
        {
            case 0:
                return "South";
            case 1:
                return "South West";
            case 2:
                return "West";
            case 3:
                return "North West";
            case 4:
                return "North";
            case 5:
                return "North East";
            case 6:
                return "East";
            case 7:
                return "South East";
        }
        return "Invalid";
    }

    private String getTowards()
    {
        switch (MathHelper.floor((double) (Minecraft.getMinecraft().player.rotationYaw * 8.0F / 360.0F) + 0.5D) & 7)
        {
            case 0:
                return "+Z";
            case 1:
                return "-X +Z";
            case 2:
                return "-X";
            case 3:
                return "-X -Z";
            case 4:
                return "-Z";
            case 5:
                return "+X -Z";
            case 6:
                return "+X";
            case 7:
                return "+X +Z";
        }
        return "Invalid";
    }
}
