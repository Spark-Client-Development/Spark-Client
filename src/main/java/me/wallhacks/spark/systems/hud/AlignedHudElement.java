package me.wallhacks.spark.systems.hud;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.util.objects.Pair;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public abstract class AlignedHudElement extends HudElement {

    protected int getBackgroundColor() {
        return hudSettings.getGuiHudListBackgroundColor().getRGB();
    }

    protected void drawList(ArrayList<Pair<String, Integer>> l) {
        ArrayList<Pair<String, Integer>> list = new ArrayList<>(l);

        if (!alignTop())
            Collections.reverse(list);


        int startX = (alignLeft() ? getRenderPosX() : getEndRenderPosX() - 2);
        int startY = getRenderPosY();


        int biggestMod = -1;
        for (Pair<String, Integer> pair : list) {
            int widthstring = fontManager.getTextWidth(pair.getKey());
            if (biggestMod < widthstring)
                biggestMod = widthstring;
        }

        setHeight(Math.max(8, list.size() * (fontManager.getTextHeight() + 2)));
        setWidth((biggestMod > 0 ? biggestMod : 30) + 3);

        int h = 0;
        for (Pair<String, Integer> pair : list) {
            int widthstring = fontManager.getTextWidth(pair.getKey());

            int y = startY + h;
            int x = startX + (alignLeft() ? 0 : -widthstring);

            Gui.drawRect(x, y, x + widthstring + 2, y + fontManager.getTextHeight() + 2, getBackgroundColor());
            if (!alignLeft())
                Gui.drawRect(x - 1, y, x, y + fontManager.getTextHeight() + 2, pair.getValue());
            else
                Gui.drawRect(x + widthstring + 2, y, x + widthstring + 3, y + fontManager.getTextHeight() + 2, pair.getValue());

            fontManager.drawString(pair.getKey(), x + 1, y + 2, pair.getValue());
            h += fontManager.getTextHeight() + 2;
            if (biggestMod < widthstring)
                biggestMod = widthstring;
        }


    }

}
