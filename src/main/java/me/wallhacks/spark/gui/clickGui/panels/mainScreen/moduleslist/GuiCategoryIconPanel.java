package me.wallhacks.spark.gui.clickGui.panels.mainScreen.moduleslist;

import me.wallhacks.spark.gui.dvdpanels.GuiPanelBase;
import me.wallhacks.spark.util.GuiUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import me.wallhacks.spark.systems.module.Module;

import java.awt.*;

public class GuiCategoryIconPanel extends GuiPanelBase {
    public GuiCategoryIconPanel(GuiClientModulePanel guiModuleListPanel, Module.Category category) {
        super(0, 0, 200, 200);

        this.category = category;

        image = new ResourceLocation("textures/categoryicons/"+category.getName().toLowerCase()+".png");

        this.guiModuleListPanel = guiModuleListPanel;
    }

    final GuiClientModulePanel guiModuleListPanel;

    final Module.Category category;

    final ResourceLocation image;

    boolean isCategorySelected(){
        return (category == guiModuleListPanel.guiCategoryListPanel.getSelected());
    }

    public void renderContent(int MouseX, int MouseY, float deltaTime)
    {
        super.renderContent(MouseX,MouseY,deltaTime);


        drawBackGround(guiSettings.getGuiSubPanelBackgroundColor().getRGB());


        mc.getTextureManager().bindTexture(this.image);

        Color c = guiSettings.getContrastColor();


        if(isCategorySelected())
            c = c.brighter();


        GlStateManager.color(c.getRed()/255f,c.getGreen()/255f,c.getBlue()/255f,c.getAlpha()/255f);


        GuiUtil.drawCompleteImage(posX, posY, width, height);

        GlStateManager.color(1,1,1,1);


    }

    @Override
    public void onClickDown(int Mouse, int MouseX, int MouseY) {
        super.onClickDown(Mouse, MouseX, MouseY);

        guiModuleListPanel.guiCategoryListPanel.setSelected(category);
    }
}
