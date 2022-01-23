package me.wallhacks.spark.systems.setting.settings;

import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.util.player.InventoryUtil;
import net.minecraft.item.Item;

public class ItemListSelectSetting extends ListSelectSetting<Item> {

    public ItemListSelectSetting(String name, SettingsHolder holder, Item[] selected, String settingCategory) {
        super(name, holder, InventoryUtil.getListOfItems(), selected,settingCategory);



    }


    @Override
    public String getValueIdString(Item t){
        return Item.getIdFromItem(t)+"";

    }
    @Override
    public String getValueDisplayString(Item t){
        String[] sl = t.getTranslationKey().split("\\.");
        return sl[1];
    }






}