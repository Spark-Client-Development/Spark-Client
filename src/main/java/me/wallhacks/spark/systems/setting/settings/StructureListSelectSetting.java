package me.wallhacks.spark.systems.setting.settings;

import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.util.StringUtil;
import me.wallhacks.spark.util.WorldUtils;
import me.wallhacks.spark.util.objects.MCStructures;
import net.minecraft.block.Block;

import java.util.Map;
import java.util.function.Predicate;

public class StructureListSelectSetting extends ListSelectSetting<MCStructures> {

    public StructureListSelectSetting(String name, SettingsHolder module, MCStructures[] selected, Predicate<Map<MCStructures, Boolean>> visible) {
        super(name, module, MCStructures.values(), selected,visible);
    }



    @Override
    public String getValueIdString(MCStructures t){
        return t.name()+"";
    }
    @Override
    public String getValueDisplayString(MCStructures t){
        return t.name();
    }
}