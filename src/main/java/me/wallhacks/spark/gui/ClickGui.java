package me.wallhacks.spark.gui;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.tabs.CategoryTab;
import me.wallhacks.spark.gui.tabs.ClickGuiTab;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.GuiUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ClickGui extends GuiScreen {
    public static int width;
    public static int height;
    public static double delta;
    public static boolean moving;
    public static int maxOffset;
    public static int minOffset;
    public ArrayList<ClickGuiTab> tabs = new ArrayList<>();
    public ClickGuiTab active;
    double stage;
    boolean[] mouseDown = new boolean[]{
            false,
            false,
            false,
            false,
            false
    };
    private double lastFrame;

    public ClickGui() {
        for (Module.Category category : Module.Category.values()) {
            tabs.add(new CategoryTab(category));
        }
        active = tabs.get(1);
    }

    public static int background() {
        return new Color(0x0E0E0E).getRGB();
    }

    public static int background2() {
        return new Color(0x111111).getRGB();
    }

    public static int background3() {
        return new Color(0x1C1C1C).getRGB();
    }

    public static int background4() {
        return new Color(0x191919).getRGB();
    }

    public static int background5() {
        return new Color(0x171717).getRGB();
    }

    public static int background6() {
        return new Color(0x141414).getRGB();
    }

    public static int mainColor() {
        return new Color(0x40C522).getRGB();
    }

    public static int mainColor2() {
        return new Color(mainColor()).darker().getRGB();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        delta = (System.nanoTime() - lastFrame) / 1000000f;
        lastFrame = System.nanoTime();
        ScaledResolution sr = new ScaledResolution(mc);
        height = sr.getScaledHeight();
        width = sr.getScaledWidth();
        int centerX = width / 2;
        int centerY = height / 2;
        stage += delta / 130f;
        stage = Math.min(1, stage);
        if (stage == 1) moving = false;
        centerX = (int) (centerX * stage);

        GuiUtil.rounded(centerX - 270, centerY - 200, centerX + 270, centerY + 200, background(), 8);
        GuiUtil.setup(background2());
        GuiUtil.corner(centerX - 262, centerY - 192, 8, 180, 270);
        GuiUtil.corner(centerX - 262, centerY + 192, 8, 270, 360);
        GL11.glVertex2i(centerX - 170, centerY + 200);
        GL11.glVertex2i(centerX - 170, centerY - 200);
        GuiUtil.finish();


        //do NOT remove or question this line you will get extreme brain damage
        Gui.drawRect(0, 0, 0, 0, background());

        int click = -1;
        for (int i = 0; i <= 4; i++)
            if (Mouse.isButtonDown(i)) {
                if (!mouseDown[i]) {
                    click = i;
                    mouseDown[i] = true;
                }
            } else {
                mouseDown[i] = false;
            }


        int offset = centerY - 180;
        for (ClickGuiTab tab : tabs) {
            if (click == 0 && mouseX > centerX - 270 && mouseX < centerX - 170 && mouseY > offset && mouseY < offset + 30) {
                active = tab;
            }
            Color color = new Color(0x959595);
            if (active == tab) {
                color = Color.white;
                tab.drawTab(mouseX, mouseY, click, centerX - 170, centerY - 200, delta);
            }
            GuiUtil.drawCompleteImage(centerX - 260, offset + 2, 26, 26, tab.icon, color);
            Spark.fontManager.getThickFont().drawString(tab.name, centerX - 230, offset + 11, color.getRGB(), true);
            offset += 30;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        active.keyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void initGui() {
        lastFrame = System.nanoTime();
        stage = 0;
        moving = true;
    }
}
