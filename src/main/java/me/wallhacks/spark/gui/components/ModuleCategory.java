package me.wallhacks.spark.gui.components;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.ClickGui;
import me.wallhacks.spark.gui.components.settings.*;
import me.wallhacks.spark.systems.setting.Setting;
import me.wallhacks.spark.systems.setting.SettingGroup;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.GuiUtil;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;

import static me.wallhacks.spark.gui.ClickGui.maxOffset;
import static me.wallhacks.spark.gui.ClickGui.minOffset;

public class ModuleCategory extends SettingComponent {
    static ResourceLocation arrow;
    ArrayList<SettingComponent> components = new ArrayList<>();
    SettingGroup group;
    boolean open = false;
    double state;

    public ModuleCategory(SettingGroup group) {
        this.group = group;
        if (arrow == null) {
            arrow = new ResourceLocation("textures/icons/arrowicon.png");
        }
        for (Setting<?> setting : group.getSettings()) {
            if (setting instanceof IntSetting) {
                components.add(new IntSlider((IntSetting) setting));
            } else if (setting instanceof DoubleSetting) {
                components.add(new DoubleSlider((DoubleSetting) setting));
            } else if (setting instanceof BooleanSetting) {
                components.add(new BooleanComponent((BooleanSetting) setting));
            } else if (setting instanceof ModeSetting) {
                components.add(new ModeSelector((ModeSetting) setting));
            }
        }
    }

    @Override
    public int drawComponent(int posX, int posY, double deltaTime, int click, int mouseX, int mouseY) {
        boolean hover = false;
        if (mouseX > posX && mouseX < posX + 200 && mouseY > posY && mouseY < posY + 20) {
            hover = true;
            if (click == 1) {
                open = !open;
            }
        }
        int offset = 0;
        for (SettingComponent setting : components) {
            offset += setting.getHeight();
        }
        int renderOffset = (int) (state * offset);
        if (open && state != 1) {
            state = Math.min(1, state + deltaTime * 0.01);
        } else if (!open && state != 0) {
            state = Math.max(0, state - deltaTime * 0.01);
        }
        GuiUtil.drawRect(posX, posY, posX + 200, posY + 20, hover ? ClickGui.background5() : ClickGui.background4());
        GuiUtil.drawCompleteImageRotated(posX + 182, posY + 7, 5, 5, (float) (90 * state), arrow, Color.WHITE);
        Spark.fontManager.drawString(group.getName(), posX + 5, posY + 7, -1);
        int max = minOffset > posY + 20 ? maxOffset : Math.max(minOffset + maxOffset - (posY + 20), 0);
        int min = Math.max(posY + 20, minOffset);
        if (renderOffset != 0) {
            int currentY = (-offset + renderOffset + posY + 20);
            maxOffset = max;
            minOffset = min;
            GuiUtil.glScissor(posX, minOffset, 200, maxOffset);
            GuiUtil.drawRect((float) posX, currentY, (float) posX + 200, (float) posY + renderOffset + 20, ClickGui.background6());
            for (SettingComponent setting : components) {
                if (currentY > minOffset + maxOffset) {
                    break;
                }
                if (setting.getHeight() + currentY < minOffset) {
                    currentY += setting.getHeight();
                } else {
                    GuiUtil.glScissor(posX, minOffset, 200, maxOffset);
                    currentY += setting.drawComponent(posX, currentY, deltaTime, click, mouseX, mouseY);
                }
            }
        }
        return 20 + renderOffset;
    }

    @Override
    public int getHeight() {
        int offset = 0;
        for (SettingComponent setting : components) {
            offset += setting.getHeight();
        }
        offset *= state;
        return offset + 20;
    }

    @Override
    public boolean visible() {
        return group.isVisible();
    }
}
