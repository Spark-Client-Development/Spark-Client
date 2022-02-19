package me.wallhacks.spark.gui.clickGui.settingScreens.kitSetting.guiKitEditor;

import me.wallhacks.spark.gui.clickGui.settingScreens.kitSetting.guiKitEditor.itemList.ItemListGui;
import me.wallhacks.spark.gui.clickGui.settingScreens.kitSetting.guiKitEditor.itemList.ItemListGuiItem;
import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.gui.panels.GuiPanelInputField;
import me.wallhacks.spark.gui.panels.GuiPanelScroll;
import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;
import me.wallhacks.spark.systems.module.modules.misc.InventoryManager;
import me.wallhacks.spark.util.GuiUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class GuiKitEditor extends GuiPanelBase {

    public GuiPanelInputField guiPanelInputField = new GuiPanelInputField(38,0,0,0,0);

    ItemListGui itemListGui = new ItemListGui(this);
    GuiPanelScroll guiPanelScroll = new GuiPanelScroll(itemListGui);

    Item holdingItem = null;



    String kit;

    public void setKit(String kit) {
        this.kit = kit;
    }

    final static ResourceLocation settingIcon = new ResourceLocation("textures/icons/settingsicon.png");
    final static ResourceLocation searchIcon = new ResourceLocation("textures/icons/searchicon.png");


    boolean usedMouse = false;

    boolean isMouseDown()
    {
        return Mouse.isButtonDown(0) && !usedMouse;
    }

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {

        if(!Mouse.isButtonDown(0))
            usedMouse = false;
        
        super.renderContent(MouseX, MouseY, deltaTime);

        int nameBarHeight = 18;
        ClientConfig guiSettings =  ClientConfig.getInstance();



        drawQuad(posX,posY,width,nameBarHeight, guiSettings.getGuiSubPanelBackgroundColor().getRGB());
        int y = posY + nameBarHeight + guiSettings.spacing;
        GuiUtil.drawCompleteImage(posX+3,posY+3, nameBarHeight-6, nameBarHeight-6,settingIcon, guiSettings.getContrastColor());


        if(InventoryManager.instance.getKits().containsKey(kit))
        {
            fontManager.drawString(kit,posX+2+nameBarHeight,posY+nameBarHeight/2-fontManager.getTextHeight()/2, guiSettings.getContrastColor().getRGB());

            Item[] kitItems = InventoryManager.instance.getKits().get(kit);
            //render current kit

            drawQuad(posX,y,width,18*4 + 2, guiSettings.getGuiSubPanelBackgroundColor().getRGB());


            for (int i = 0; i < kitItems.length; i++)
            {
                int offsetX = (i % 9) * 18;
                int offsetY = (i / 9) * 18;

                if(i < 9)
                    offsetY += 1 + 18*3;
                else
                    offsetY -= 1 + 18;

                if(isMouseIn(offsetX + posX, offsetY + y,18,18,MouseX,MouseY) && isMouseDown())
                {

                    usedMouse = true;
                    if(holdingItem != null)
                    {
                        Item l = kitItems[i];
                        kitItems[i] = holdingItem;
                        holdingItem = l;
                    }
                    else
                    {
                        holdingItem = kitItems[i];
                        kitItems[i] = null;
                    }
                    InventoryManager.instance.getKits().put(kit,kitItems);
                }

                drawQuad(offsetX+1 + posX, offsetY+2 + y,16,16,guiSettings.getGuiSettingFieldColor().getRGB());


                if(kitItems[0] != null)
                {
                    RenderHelper.enableStandardItemLighting();
                    mc.getRenderItem().zLevel = 200.0f;
                    ItemStack itemStack = new ItemStack(kitItems[i]);
                    mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, offsetX+1 + posX, offsetY+2 + y);
                    GlStateManager.disableLighting();
                    GlStateManager.enableAlpha();
                    mc.getRenderItem().zLevel = 0.0f;
                    RenderHelper.disableStandardItemLighting();
                }


            }


            y += 18*4 + 2 + guiSettings.spacing;

            guiPanelInputField.setTextOffsetX(nameBarHeight);
            guiPanelInputField.setBackGroundColor(guiSettings.getGuiSubPanelBackgroundColor().getRGB());
            guiPanelInputField.setPositionAndSize(posX,y,width,nameBarHeight);
            guiPanelInputField.renderContent(MouseX, MouseY, deltaTime);
            GuiUtil.drawCompleteImage(posX+3,y+3, nameBarHeight-6, nameBarHeight-6,searchIcon, guiSettings.getContrastColor());


            y += guiSettings.spacing+nameBarHeight;

            guiPanelScroll.setPositionAndSize(posX,y,width,posY+height-y);
            guiPanelScroll.drawBackGround(guiSettings.getGuiSubPanelBackgroundColor().getRGB());
            guiPanelScroll.renderContent(MouseX, MouseY, deltaTime);


            if(holdingItem != null)
            {
                RenderHelper.enableStandardItemLighting();
                mc.getRenderItem().zLevel = 400.0f;
                mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(holdingItem), MouseX-8, MouseY-8);
                GlStateManager.disableLighting();
                GlStateManager.enableAlpha();
                mc.getRenderItem().zLevel = 0.0f;
                RenderHelper.disableStandardItemLighting();
            }


            if(isMouseDown())
            {
                if(!guiPanelScroll.isMouseOn || holdingItem != null)
                {
                    holdingItem = null;
                    usedMouse = true;
                }
                else
                {

                    ItemListGuiItem i = (itemListGui.getSelectedFromGuiSettingTabSelectFromLists());
                    if(i != null) {
                        holdingItem = i.getItem();
                        usedMouse = true;
                    }

                }





            }



        }
        else
            drawQuad(posX,y,width,height-(y-posY), guiSettings.getGuiSubPanelBackgroundColor().getRGB());





    }
}
