package me.wallhacks.spark.gui.clickGui.panels.mainScreen.moduleslist;

import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.systems.module.Module;

import java.util.ArrayList;

public class GuiCategoryListPanel extends GuiPanelBase {
    public GuiCategoryListPanel(GuiClientModulePanel guiModuleListPanel) {
        super(0, 0, 200, 200);

        for (Module.Category category : Module.Category.values()) {
            categoryPanels.add(new GuiCategoryPanel(this,category));
        }

        this.guiModuleListPanel = guiModuleListPanel;
    }

    public final GuiClientModulePanel guiModuleListPanel;
    int categoryspacing = 8;


    public void setSelected(Module.Category selected){
        int y = 0;


        for (GuiCategoryPanel category : categoryPanels) {
            if(selected == category.category)
            {
                guiModuleListPanel.guiPanelScroll.setScroll(y);
                return;
            }

            y+=categoryspacing;
            y += category.height;
        }
    }

    public Module.Category getSelected() {
        int scroll = (int) guiModuleListPanel.guiPanelScroll.getScroll();

        int y = 0;

        Module.Category selected = null;
        for (GuiCategoryPanel category : categoryPanels) {
            y += category.height;

            selected = category.category;
            if(y > scroll)
                break;

            y+=categoryspacing;
        }
        return selected;
    }



    ArrayList<GuiCategoryPanel> categoryPanels = new ArrayList<GuiCategoryPanel>();

    public void renderContent(int MouseX, int MouseY, float deltaTime)
    {

        if(true) {



            int y = posY;



            for (GuiCategoryPanel category: categoryPanels) {


                y+=categoryspacing;

                category.posX = posX;
                category.posY = y;

                category.width = width;

                category.renderContent(MouseX,MouseY,deltaTime);

                y += category.height;



            }

            height = y+5;

        }


    }


}
