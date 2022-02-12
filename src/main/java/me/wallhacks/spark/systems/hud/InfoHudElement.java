package me.wallhacks.spark.systems.hud;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.clientsetting.clientsettings.HudSettings;
import net.minecraft.client.gui.Gui;

public class InfoHudElement extends HudElement {
    String info;
    public void drawInfo() {
        setHeight(fontManager.getTextHeight() + 2);
        setWidth(fontManager.getTextWidth(info) + 3);
        Spark.fontManager.drawString(info, getRenderPosX() + (alignLeft() ? 1 : 2), getRenderPosY() + 2, -1);
        Gui.drawRect(getRenderPosX() + (alignLeft() ? (getWidth() - 1) : 0), getRenderPosY(), getRenderPosX() + (alignLeft() ? getWidth() : 1), getRenderPosY() + getHeight(), HudSettings.INSTANCE.getGuiHudMainColor().getRGB());
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
