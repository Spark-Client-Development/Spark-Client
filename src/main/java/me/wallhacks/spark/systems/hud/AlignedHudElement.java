package me.wallhacks.spark.systems.hud;

import net.minecraft.client.gui.Gui;

import java.util.ArrayList;
import java.util.Collections;

public abstract class AlignedHudElement extends HudElement {


    protected boolean alignLeft() {
        return 0.5 > getPercentPosX();
    }

    protected boolean alignTop() {
        return 0.5 > getPercentPosY();
    }

    protected int getBackgroundColor() {
        return hudSettings.getGuiHudListBackgroundColor().getRGB();
    }

    protected int getColor(int x, int y, String text) {
        return hudSettings.getGuiHudSecondColor().getRGB();
    }

    protected void drawList(ArrayList<String> l) {

        ArrayList<String> list = new ArrayList(l);

        if (!alignTop())
            Collections.reverse(list);


        int startX = (alignLeft() ? getRenderPosX() : getEndRenderPosX() - 2);
        int startY = getRenderPosY();


        int biggestMod = -1;
        for (String string : list) {
            int widthstring = fontManager.getTextWidth(string);
            if (biggestMod < widthstring)
                biggestMod = widthstring;
        }

        setHeight(Math.max(8, list.size() * (fontManager.getTextHeight() + 2)));
        setWidth((biggestMod > 0 ? biggestMod : 30) + 3);

        int h = 0;
        for (String string : list) {
            int widthstring = fontManager.getTextWidth(string);

            int y = startY + h;
            int x = startX + (alignLeft() ? 0 : -widthstring);

            int color = getColor(x, h, string);

            Gui.drawRect(x, y, x + widthstring + 2, y + fontManager.getTextHeight() + 2, getBackgroundColor());
            if (!alignLeft())
                Gui.drawRect(x - 1, y, x, y + fontManager.getTextHeight() + 2, color);
            else
                Gui.drawRect(x + widthstring + 2, y, x + widthstring + 3, y + fontManager.getTextHeight() + 2, color);

            fontManager.drawString(string, x + 1, y + 2, color);
            h += fontManager.getTextHeight() + 2;

            if (biggestMod < widthstring)
                biggestMod = widthstring;
        }


    }

}
