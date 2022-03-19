package me.wallhacks.spark.gui.clickGui.panels.mainScreen.moduleslist;

import me.wallhacks.spark.gui.dvdpanels.GuiPanelBase;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelInputField;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelScroll;
import me.wallhacks.spark.util.GuiUtil;
import net.minecraft.util.ResourceLocation;
import me.wallhacks.spark.systems.module.Module;

public class GuiClientModulePanel extends GuiPanelBase {

    public GuiClientModulePanel(int posX, int posY, int width, int height) {
        super(posX, posY, width, height);

        categories = new GuiCategoryIconPanel[Module.Category.values().length];
        for (int i = 0; i < categories.length; i++) {
            categories[i] = new GuiCategoryIconPanel(this,Module.Category.values()[i]);
        }


    }
    GuiCategoryIconPanel[] categories;

    public final GuiPanelInputField moduleSearchField = new GuiPanelInputField(0,0,0,0,0);


    final ResourceLocation searchIcon = new ResourceLocation("textures/icons/searchicon.png");

    public final GuiCategoryListPanel guiCategoryListPanel = new GuiCategoryListPanel(this);
    public final GuiPanelScroll guiPanelScroll = new GuiPanelScroll(posX, posY, width, height,guiCategoryListPanel);


    public void renderContent(int MouseX, int MouseY, float deltaTime) {

        super.renderContent(MouseX,MouseY,deltaTime);


        int categotyIconWidth = 34;

        int h = 0;
        for (GuiCategoryIconPanel c : categories) {
            c.setPositionAndSize(posX,posY+h,categotyIconWidth,categotyIconWidth);
            c.renderContent(MouseX,MouseY,deltaTime);
            h+=categotyIconWidth;
        }

        int x = categotyIconWidth+ guiSettings.spacing;



        int searchFieldHeight = 18;

        moduleSearchField.setBackGroundColor(guiSettings.getGuiSubPanelBackgroundColor().getRGB());
        moduleSearchField.setTextOffsetX(searchFieldHeight);
        moduleSearchField.setPositionAndSize(posX+x,posY,width-x,searchFieldHeight);
        moduleSearchField.renderContent(MouseX,MouseY,deltaTime);


        GuiUtil.drawCompleteImage(posX+3+x,posY+3, searchFieldHeight-6, searchFieldHeight-6,searchIcon, guiSettings.getContrastColor());



        int y = 18+ guiSettings.spacing;

        guiPanelScroll.setPositionAndSize(posX+x,posY+y,width-x,height-y);
        guiPanelScroll.drawBackGround(guiSettings.getGuiSubPanelBackgroundColor().getRGB());
        guiPanelScroll.renderContent(MouseX,MouseY,deltaTime);

    }





}
