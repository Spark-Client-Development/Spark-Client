package me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting;

import me.wallhacks.spark.gui.dvdpanels.GuiPanelBase;
import me.wallhacks.spark.systems.setting.Setting;

public class GuiSettingPanel<T extends Setting> extends GuiPanelBase {
    public GuiSettingPanel(T setting) {
        super(0, 0, 0, 20);

        this.setting = setting;
    }
    final T setting;

    protected T getSetting(){
        return setting;
    }
}
