package me.wallhacks.spark.gui.components.settings;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.ClickGui;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.util.GuiUtil;

public class BooleanComponent extends SettingComponent {
    BooleanSetting setting;
    public BooleanComponent(BooleanSetting setting) {
        this.setting = setting;
        state = setting.getValue() ? 1 : 0;
    }
    double state;
    @Override
    public int drawComponent(int posX, int posY, double deltaTime, int click, int mouseX, int mouseY) {
        Spark.fontManager.drawString(setting.getName(), posX + 6, posY + 5, -1);
        GuiUtil.rounded(posX + 175, posY + 7, posX + 190, posY + 11, setting.getValue() ? ClickGui.mainColor2() : ClickGui.background3(), 2);
        if (click == 0 && mouseX > posX && mouseX < posX + 200 && mouseY > posY && mouseY < posY + 21) setting.toggle();
        if (setting.getValue() && state != 1) {
            state = Math.min(1, state + deltaTime*0.01);
        } else if (!setting.getValue() && state != 0) {
            state = Math.max(0, state - deltaTime*0.01);
        }
        GuiUtil.setup(ClickGui.mainColor());
        int x = (int) (posX + 175 + 15*state);
        GuiUtil.corner(x,posY + 9, 4, 0, 360);
        GuiUtil.finish();
        return 17;
    }

    @Override
    public int getHeight() {
        return 17;
    }

    @Override
    public boolean visible() {
        return setting.isVisible();
    }
}
