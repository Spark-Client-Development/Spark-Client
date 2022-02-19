package me.wallhacks.spark.gui.clickGui.settingScreens.kitSetting.guiKitEditor.itemList;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.clickGui.settingScreens.kitSetting.guiKitEditor.GuiKitEditor;
import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.gui.panels.GuiPanelInputField;
import me.wallhacks.spark.systems.setting.settings.ListSelectSetting;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.player.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class ItemListGui extends GuiPanelBase {

    final GuiKitEditor guiKitEditor;

    public ItemListGui(GuiKitEditor guiKitEditor) {

        this.guiKitEditor = guiKitEditor;

        for (Item sel : InventoryUtil.getListOfItems()) {
            if(sel != Items.AIR)
            guiSettingTabSelectFromLists.add(new ItemListGuiItem(sel));
        }
    }
    ArrayList<ItemListGuiItem> guiSettingTabSelectFromLists = new ArrayList<ItemListGuiItem>();

    public ItemListGuiItem getSelectedFromGuiSettingTabSelectFromLists() {
        for (ItemListGuiItem i : guiSettingTabSelectFromLists) {
            if(i.isSelected())
                return i;
        }
        return null;
    }

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);


        int x = 0;
        int y = 0;

        String l1 = guiKitEditor.guiPanelInputField.getText().toLowerCase();
        for (ItemListGuiItem sels : guiSettingTabSelectFromLists) {
            if(sels.getItem() != null)
            {

                int xof = (width - 16*(int) (width/16f)) / 2;



                if(I18n.translateToLocal(sels.getItem().getTranslationKey() + ".name").toLowerCase().contains(l1)){

                    //this is a bad way of reducing lag but works well!!!
                    int realY = (int) (posY+y+ GuiUtil.getGlTransformOffset().y);


                    if(realY > posY-20 && realY < posY+height+20){



                        sels.posY = posY+y;
                        sels.posX = posX+x+xof;
                        sels.renderContent(MouseX, MouseY, deltaTime);
                    }


                    if(x + 16*2 > width){
                        y += 16;
                        x = 0;
                    }
                    else
                        x += 16;
                }
            }

        }

        height = y + 20;

    }
}
