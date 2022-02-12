package me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting;

import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.gui.panels.GuiPanelScroll;
import me.wallhacks.spark.util.GuiUtil;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.StringUtil;

public class GuiEditModuleSettings extends GuiPanelBase {

    public GuiEditModuleSettings(GuiEditSettingPanel guiEditSettingPanel) {
        this.guiEditSettingPanel = guiEditSettingPanel;
    }
    final GuiEditSettingPanel guiEditSettingPanel;

    final static ResourceLocation mutedIcon = new ResourceLocation("textures/icons/mutedicon.png");
    final static ResourceLocation unmutedIcon = new ResourceLocation("textures/icons/unmutedicon.png");

    final static ResourceLocation visibleIcon = new ResourceLocation("textures/icons/visibleicon.png");
    final static ResourceLocation notisibleIconIcon = new ResourceLocation("textures/icons/notvisibleicon.png");


    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        if(guiEditSettingPanel.currentSettingsHolder instanceof Module){
            Module m = (Module)guiEditSettingPanel.currentSettingsHolder;
            super.renderContent(MouseX, MouseY, deltaTime);




            drawBackGround(guiSettings.getGuiSubPanelBackgroundColor().getRGB());

            int StringcenterY = posY+height/2-fontManager.getTextHeight()/2;

            //key
            String s = "Bind: "+ (waitingForKey ? "..." : StringUtil.getNameForKey(m.getBind()));
            int x = fontManager.drawString(s,posX+6,StringcenterY, guiSettings.getContrastColor().getRGB());

            if(m.getBind() != 0 && !waitingForKey)
                fontManager.drawString(m.isHold() ? "Hold" : "Toggle",posX+80,StringcenterY, guiSettings.getContrastColor().getRGB());


            int ImageSize = 10;
            int ImagecenterY = posY+height/2-ImageSize/2;

            GuiUtil.drawCompleteImage(posX+width-4-ImageSize,ImagecenterY,ImageSize,ImageSize,m.isMuted() ? mutedIcon : unmutedIcon, guiSettings.getContrastColor());

            GuiUtil.drawCompleteImage(posX+width-4-ImageSize-ImageSize-4,ImagecenterY,ImageSize,ImageSize,m.isVisible() ? visibleIcon : notisibleIconIcon, guiSettings.getContrastColor());


            if(!isFocused())
                waitingForKey = false;
            else if(!waitingForKey)
                setFocused(false);

        }
    }

    public boolean isWaitingForKey() {
        return waitingForKey;
    }
    boolean waitingForKey = false;

    @Override
    public void onClickDown(int MouseButton, int MouseX, int MouseY) {
        super.onClickDown(MouseButton, MouseX, MouseY);

        if(guiEditSettingPanel.currentSettingsHolder instanceof Module) {
            Module m = (Module)guiEditSettingPanel.currentSettingsHolder;

            if(waitingForKey)
            {
                key(-2-MouseButton);
                return;
            }

            if(posX+75 > MouseX)
            {
                waitingForKey = true;
            }
            else if(posX + width - 16 < MouseX){
                m.setMuted(!m.isMuted());
            }
            else if(posX + width - 30 < MouseX){
                m.setVisible(!m.isVisible());
            }
            else {
                m.setHold(!m.isHold());
            }

        }
    }

    @Override
    public void onKey(int KeyCode, char TypedChar) {
        super.onKey(KeyCode, TypedChar);

        if(waitingForKey)
            key(KeyCode);
    }

    void key(int k){
        if(guiEditSettingPanel.currentSettingsHolder instanceof Module) {
            Module m = (Module) guiEditSettingPanel.currentSettingsHolder;

            waitingForKey = false;
            if(k == Keyboard.KEY_DELETE)
                m.setBind(Keyboard.KEY_NONE);
            else if(k != Keyboard.KEY_ESCAPE)
                m.setBind(k);



        }

    }
}
