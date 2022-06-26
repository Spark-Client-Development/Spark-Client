package me.wallhacks.spark.systems.setting;

import me.wallhacks.spark.systems.SettingsHolder;

import java.util.function.Predicate;

public class SettingGroup extends SettingsHolder {
    private String name;
    SettingsHolder holder;

    private Predicate<Boolean> visible;


    public SettingGroup(String name, SettingsHolder holder) {
        visible = null;
        holder.addGroup(this);
        this.name = name;
        this.holder = holder;
    }

    public boolean isVisible() {
        if (this.visible == null) {
            return true;
        }
        return this.visible.test(true);
    }

    public SettingGroup setVisible(Predicate<Boolean> visible) {
        this.visible = visible;
        return this;
    }

    @Override
    public void addSetting(Setting<?> setting) {
        super.addSetting(setting);
        if(!holder.getSettings().contains(setting))
            holder.addSetting(setting);
    }

    @Override
    public String getName() {
        return name;
    }
}
