package me.wallhacks.spark.gui.clickGui.settingScreens.listScreen;

import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.gui.panels.GuiPanelInputField;
import me.wallhacks.spark.systems.setting.settings.ListSelectSetting;
import me.wallhacks.spark.util.GuiUtil;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class ListSettingGuiList extends GuiPanelBase {

    final ListSelectSetting listSelectSetting;
    final GuiPanelInputField guiPanelInputField;
    public ListSettingGuiList(ListSelectSetting listSelectSetting, GuiPanelInputField guiPanelInputField) {

        this.listSelectSetting = listSelectSetting;
        this.guiPanelInputField = guiPanelInputField;

        for (Object sel : listSelectSetting.getValues()) {
            guiSettingTabSelectFromLists.add(new ListSettingGuiItem(listSelectSetting, sel));
        }
    }
    ArrayList<ListSettingGuiItem> guiSettingTabSelectFromLists = new ArrayList<ListSettingGuiItem>();




    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        super.renderContent(MouseX, MouseY, deltaTime);


        int h = 0;

        String l1 = guiPanelInputField.getText().toLowerCase();
        for (ListSettingGuiItem sels : guiSettingTabSelectFromLists) {
            String l = listSelectSetting.getValueDisplayString(sels.selection).toLowerCase();



            if(l.contains(l1) && (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || listSelectSetting.isValueSelected(sels.selection))){

                //this is a bad way of reducing lag but works well!!!
                int realY = (int) (posY+h+ GuiUtil.getGlTransformOffset().y);
                if(realY > posY-20 && realY < posY+height+20){
                    sels.posY = posY+h;
                    sels.width = width;
                    sels.posX = posX;
                    sels.renderContent(MouseX, MouseY, deltaTime);
                }


                h += sels.height;
            }



        }

        height = h + 20;

    }
}
