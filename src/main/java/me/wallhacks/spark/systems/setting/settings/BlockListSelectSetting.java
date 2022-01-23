package me.wallhacks.spark.systems.setting.settings;

import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.util.StringUtil;
import me.wallhacks.spark.util.WorldUtils;
import net.minecraft.block.Block;

public class BlockListSelectSetting extends ListSelectSetting<Block> {

    public BlockListSelectSetting(String name, SettingsHolder module, Block[] selected, String settingCategory) {
        super(name, module, WorldUtils.getListOfBlocks(), selected,settingCategory);


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