package me.wallhacks.spark.gui.components.settings;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.ClickGui;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.MathUtil;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;

public class IntSlider extends SettingComponent {
    IntSetting setting;
    double progress;
    boolean sliding;
    double target;
    public IntSlider(IntSetting setting) {
        this.setting = setting;
        progress = setting.getValue()/(setting.getMax() - setting.getMin())*190;
        target = progress;
    }
    @Override
    public int drawComponent(int posX, int posY, double deltaTime, int click, int mouseX, int mouseY) {
        Spark.fontManager.drawString(setting.getName(), posX + 6, posY + 5, -1);
        String display = setting.getValueString();
        int length = Spark.fontManager.getTextWidth(display);
        GuiUtil.rounded(posX + 180 - length, posY + 2, posX + 190, posY + 14, ClickGui.background3(), 3);
        Spark.fontManager.drawString(display, posX + 185 - length, posY + 5, -1);
        if (click == 0 && mouseX > posX + 2 && mouseX < posX + 198 && mouseY > posY + 14 && mouseY < posY + 24) {
            sliding = true;
        }
        if (sliding) {
            if (!Mouse.isButtonDown(0)) {
                sliding = false;
            } else {
                double mouse = MathHelper.clamp(mouseX - posX - 10, 0, 180)/180f;
                double value = (setting.getMax() - setting.getMin())*mouse;
                value+=0.5;
                value-=(value)%1;
                setting.setValue((int) value);
                target = setting.getValue() / (setting.getMax() - setting.getMin()) * 180;
            }
        }
        if (target != progress) {
            progress = MathUtil.lerp(progress, target, deltaTime * 0.02);
            if (Math.abs(progress - target) < 1) {
                progress=target;
            }
        }
        GuiUtil.rounded(posX + 10, posY + 16, (int) (posX + 10 + progress), posY + 20, ClickGui.mainColor2(), 2);
        GuiUtil.rounded((int) (posX + 10 + progress), posY + 16, posX + 190, posY + 20, ClickGui.background3(), 2);
        GuiUtil.setup(ClickGui.mainColor());
        GuiUtil.corner((int) (posX + 10 + progress),posY + 18, 4, 0, 360);
        GuiUtil.finish();
        return getHeight();
    }

    @Override
    public int getHeight() {
        return 23;
    }

    @Override
    public boolean visible() {
        return setting.isVisible();
    }
}
