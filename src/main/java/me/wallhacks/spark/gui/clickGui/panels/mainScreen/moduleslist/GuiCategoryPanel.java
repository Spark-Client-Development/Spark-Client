package me.wallhacks.spark.gui.clickGui.panels.mainScreen.moduleslist;

import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.manager.SystemManager;
import me.wallhacks.spark.systems.module.Module;

import java.util.ArrayList;

public class GuiCategoryPanel extends GuiPanelBase {
    public GuiCategoryPanel(GuiCategoryListPanel guiCategoryListPanel, Module.Category category) {
        super(0, 0, 200, 200);

        this.category = category;

        for (Module m: SystemManager.getModules()) {
            if(category == m.getCategory())
                modules.add(new GuiModulePanel(m));

        }

        this.guiCategoryListPanel = guiCategoryListPanel;
    }
    GuiCategoryListPanel guiCategoryListPanel;

    final Module.Category category;

    ArrayList<GuiModulePanel> modules = new ArrayList<GuiModulePanel>();

    boolean isExtended = true;

    public void renderContent(int MouseX, int MouseY, float deltaTime)
    {
        super.renderContent(MouseX,MouseY,deltaTime);

        if(true) {

            int spacing = guiSettings.spacing;



            int moduleWidth = (width - spacing - spacing * colums) / colums;


            int y = posY + 4;


            int colum = 0;



            fontManager.drawString(category.getName(),posX+width/2-fontManager.getTextWidth(category.getName())/2,y, guiSettings.getContrastColor().getRGB());
            y += fontManager.getTextHeight();

            drawHorizontalLine(posX+spacing,posX+width-spacing-1,y, guiSettings.getContrastColor().getRGB());
            y+=spacing;

            if(isExtended)
            {
                for (GuiModulePanel m: modules) {
                    if(m.module.getName().toLowerCase().contains(guiCategoryListPanel.guiModuleListPanel.moduleSearchField.getText().toLowerCase())) {

                        m.setPositionAndSize(posX+spacing+(moduleWidth+spacing)*colum,y,moduleWidth, moduleHeight);
                        m.renderContent(MouseX,MouseY,deltaTime);

                        colum++;
                        if(colum >= colums){
                            colum = 0;
                            y+= moduleHeight + spacing;
                        }
                    }

                }
                if(colum != 0)
                    y+= moduleHeight + spacing;
            }


            height = y-posY + spacing;

        }


    }

    int colums = 2;
    int moduleHeight = 15;


}
