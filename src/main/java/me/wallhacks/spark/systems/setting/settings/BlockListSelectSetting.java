package me.wallhacks.spark.systems.setting.settings;

import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.util.StringUtil;
import me.wallhacks.spark.util.WorldUtils;
import net.minecraft.block.Block;

import java.util.Map;
import java.util.function.Predicate;

public class BlockListSelectSetting extends ListSelectSetting<Block> {

    public BlockListSelectSetting(String name, SettingsHolder module, Block[] selected) {
        super(name, module, WorldUtils.getListOfBlocks(), selected);
    }
    public BlockListSelectSetting(String name, SettingsHolder module, Block[] selected, Predicate<Map<Block, Boolean>> visible) {
        super(name, module, WorldUtils.getListOfBlocks(), selected,visible);
    }

    @Override
    public String getValueIdString(Block t){
        return Block.getIdFromBlock(t)+"";
    }
    @Override
    public String getValueDisplayString(Block t){
        return StringUtil.BlockToText(t);
    }
}