package me.wallhacks.spark.systems.hud.huds;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.systems.hud.InfoHudElement;

@HudElement.Registration(name = "WaterMark", posY = 0, posX = 0, height = 0, width = 0, description = "Shows client watermark", enabled = true)
public class WaterMark extends InfoHudElement {
    @Override
    public void draw(float delta) {
        setInfo(Spark.NAME + "-" + Spark.VERSION);
        drawInfo();
    }
}
