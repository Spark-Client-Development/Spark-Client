package me.wallhacks.spark.systems.setting.settings;

import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.util.player.InventoryUtil;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

public class ItemListSelectSetting extends ListSelectSetting<Item> {

    public ItemListSelectSetting(String name, SettingsHolder holder, Item[] selected) {
        super(name, holder, InventoryUtil.getListOfItems(), selected);



    }


    @Override
    public String getValueIdString(Item t){
        return Item.getIdFromItem(t)+"";

    }
    @Override
    public String getValueDisplayString(Item t){

        return I18n.translateToLocal(t.getTranslationKey() + ".name");
    }






}