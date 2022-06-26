package me.wallhacks.spark.gui.components.settings;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.ClickGui;
import me.wallhacks.spark.gui.components.ModuleComponent;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.GuiUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class ModeSelector extends SettingComponent {
    static ResourceLocation arrow;
    ModeSetting setting;
    int width;
    boolean open = false;
    double state = 0;

    public ModeSelector(ModeSetting setting) {
        if (arrow == null) {
            arrow = new ResourceLocation("textures/icons/arrowicon.png");
        }
        this.setting = setting;
        for (String s : setting.getModes()) {
            int t = Spark.fontManager.getTextWidth(s);
            if (t > width) {
                width = t;
            }
        }
        width += 12;
    }

    @Override
    public int drawComponent(int posX, int posY, double deltaTime, int click, int mouseX, int mouseY) {
        Spark.fontManager.drawString(setting.getName(), posX + 6, posY + 5, -1);
        String display = setting.getValueString();
        int length = Spark.fontManager.getTextWidth(display);
        GuiUtil.rounded(posX + 180 - width, posY + 2, posX + 190, (int) (posY + 14 + offset() * state), ClickGui.background3(), 3);
        Gui.drawRect(0, 0, 0, 0, 0);
        int offset = (int) (-offset() + offset() * state);
        Spark.fontManager.drawString(display, posX + 185 - length, posY + 5, -1);
        GuiUtil.drawCompleteImageRotated(posX + 184 - width, posY + 5, 5, 5, (float) (90 * state), arrow, Color.WHITE);
        if (state != 0) {
            GuiUtil.glScissor(posX, Math.max(posY + 14, ClickGui.minOffset), 200, ClickGui.minOffset > posY + 14 ? ClickGui.maxOffset : Math.max(ClickGui.maxOffset + ClickGui.minOffset - (posY + 14), 0));
            for (String mode : setting.getModes()) {
                if (mode.equals(setting.getValue())) continue;
                boolean hover = false;
                if (mouseX > posX + 180 - width && mouseX < posX + 190 && mouseY > posY + 12 + offset && mouseY < posY + 25 + offset) {
                    hover = true;
                    if (click == 0) {
                        click = -1;
                        setting.setValue(mode);
                    }
                }
                GuiUtil.drawRect(posX + 180 - width, posY + 12 + offset, posX + 190, posY + 25 + offset, hover ? ClickGui.background() : ClickGui.background4());
                Spark.fontManager.drawString(mode, posX + 185 - Spark.fontManager.getTextWidth(mode), posY + 16 + offset, -1);
                offset += 13;
            }
        }
        if (mouseX > posX && mouseX < posX + 200 && mouseY > posY && mouseY < posY + 21) {
            if (click == 0)
                setting.increment();
            else if (click == 1) open = !open;
        }
        if (open && state != 1) {
            state = Math.min(1, state + deltaTime * 0.01);
        } else if (!open && state != 0) {
            state = Math.max(0, state - deltaTime * 0.01);
        }
        return (int) (17 + offset * state);
    }

    private int offset() {
        return (setting.getModes().size() - 1) * 13;
    }

    @Override
    public int getHeight() {
        return (int) (17 + offset() * state);
    }

    @Override
    public boolean visible() {
        return setting.isVisible();
    }
}
