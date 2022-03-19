package me.wallhacks.spark.gui.clickGui.panels.hudeditor.hudsList;

import me.wallhacks.spark.gui.dvdpanels.GuiPanelBase;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelInputField;
import me.wallhacks.spark.gui.dvdpanels.GuiPanelScroll;
import me.wallhacks.spark.util.GuiUtil;
import net.minecraft.util.ResourceLocation;

public class GuiHudPanel extends GuiPanelBase {

    public GuiHudPanel(int posX, int posY, int width, int height) {
        super(posX, posY, width, height);




    }


    public final GuiPanelInputField moduleSearchField = new GuiPanelInputField(0,0,0,0,0);


    final ResourceLocation searchIcon = new ResourceLocation("textures/icons/searchicon.png");

    public final GuiHudListPanel guiHudListPanel = new GuiHudListPanel(this);
    public final GuiPanelScroll guiPanelScroll = new GuiPanelScroll(posX, posY, width, height,guiHudListPanel);


    public void renderContent(int MouseX, int MouseY, float deltaTime) {

        super.renderContent(MouseX,MouseY,deltaTime);







        int searchFieldHeight = 18;

        moduleSearchField.setBackGroundColor(guiSettings.getGuiSubPanelBackgroundColor().getRGB());
        moduleSearchField.setTextOffsetX(searchFieldHeight);
        moduleSearchField.setPositionAndSize(posX,posY,width,searchFieldHeight);
        moduleSearchField.renderContent(MouseX,MouseY,deltaTime);


        GuiUtil.drawCompleteImage(posX+3,posY+3, searchFieldHeight-6, searchFieldHeight-6,searchIcon, guiSettings.getContrastColor());



        int y = 18+ guiSettings.spacing;

        guiPanelScroll.setPositionAndSize(posX,posY+y,width,height-y);
        guiPanelScroll.drawBackGround(guiSettings.getGuiSubPanelBackgroundColor().getRGB());
        guiPanelScroll.renderContent(MouseX,MouseY,deltaTime);

    }





}
