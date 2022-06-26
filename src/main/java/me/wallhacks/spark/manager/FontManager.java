package me.wallhacks.spark.manager;

import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.objects.GameFontRenderer;
import net.minecraft.client.Minecraft;
import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;
import java.util.Locale;

public class FontManager implements MC {
    private final String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(Locale.ENGLISH);
    public String fontName = "Tahoma";
    public int fontSize = 17;
    private GameFontRenderer font = new GameFontRenderer(new Font(fontName, Font.PLAIN, fontSize), true, false);
    private GameFontRenderer largeFont = new GameFontRenderer(new Font(fontName, Font.PLAIN, 27), true, false);
    private GameFontRenderer thickFont = new GameFontRenderer(getClientFont("Thick.ttf", 20), true, false);
    private GameFontRenderer badaboom = new GameFontRenderer(getClientFont("badaboom.ttf", 27), true, false);

    public String[] getFonts() {
        return fonts;
    }

    public void setFont() {
        this.font = new GameFontRenderer(new Font(fontName, Font.PLAIN, fontSize), true, false);
        this.largeFont = new GameFontRenderer(new Font(fontName, Font.PLAIN, 23), true, false);
    }

    public GameFontRenderer getBadaboom() {
        return this.badaboom;
    }

    public GameFontRenderer getLargeFont() {
        return this.largeFont;
    }

    public GameFontRenderer getThickFont() {
        return this.thickFont;
    }

    public void reset() {
        this.setFont("Tahoma");
        this.setFontSize(17);
        this.setFont();
    }

    public boolean setFont(String fontName) {
        for (String font : fonts) {
            if (fontName.equalsIgnoreCase(font)) {
                this.fontName = font;
                this.setFont();
                return true;
            }
        }
        return false;
    }

    public int drawString(String text, int x, int y, int color) {
        if (getCustomFontEnabled())
            font.drawString(text, x, y, color, getFontShadow());
        else
            Minecraft.getMinecraft().fontRenderer.drawString(text, x, y, color, getFontShadow());
        return x + getTextWidth(text);
    }

    public int getTextWidth(String text) {
        if (getCustomFontEnabled())
            return font.getStringWidth(text);
        else
            return Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
    }

    public int getTextHeight() {
        if (getCustomFontEnabled())
            return font.getStringHeight();
        else
            return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
    }

    public void setFontSize(int size) {
        this.fontSize = size;
        this.setFont();
    }

    private static Font getClientFont(final String fontName, final float size) {
        try {
            final InputStream inputStream = FontManager.class.getResourceAsStream("/assets/minecraft/fonts/" + fontName);
            Font awtClientFont = Font.createFont(0, inputStream);
            awtClientFont = awtClientFont.deriveFont(0, size);
            inputStream.close();
            return awtClientFont;
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("default", 0, (int) size);
        }
    }

    public static boolean getCustomFontEnabled(){
        return ClientConfig.getInstance().getCustomFontEnabled();
    }
    public static boolean getFontShadow(){
        return ClientConfig.getInstance().getFontShadow();
    }
}