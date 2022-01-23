package me.wallhacks.spark.gui.clickGui.panels.hudeditor;

import me.wallhacks.spark.gui.clickGui.ClickGuiMenuBase;
import me.wallhacks.spark.gui.clickGui.ClickGuiPanel;
import me.wallhacks.spark.gui.clickGui.panels.hudeditor.hudsList.GuiHudSettingTab;
import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.gui.panels.GuiPanelButton;
import me.wallhacks.spark.manager.SystemManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import me.wallhacks.spark.systems.clientsetting.clientsettings.HudSettings;
import me.wallhacks.spark.systems.hud.HudElement;

public class HudEditor extends ClickGuiPanel {


    @Override
    public boolean renderBackground() {
        return false;
    }

    @Override
    public String getName() {
        return "HudEditor";
    }


    @Override
    public void init() {
        super.init();

        guiHudSettingTab.guiHudPanel.moduleSearchField.setText("");

        guiHudSettingTab.posX = getCenterX()-guiHudSettingTab.width/2;
        guiHudSettingTab.posY = getCenterY()-guiHudSettingTab.height/2;
    }

    final GuiPanelBase[] moveHudElements;
    public GuiHudSettingTab guiHudSettingTab = new GuiHudSettingTab();

    public HudEditor(ClickGuiMenuBase clickGuiMenuBase){
        super(clickGuiMenuBase);

        moveHudElements = new GuiPanelBase[SystemManager.getHudModules().size()];
        for (int i = 0; i < moveHudElements.length; i++)
            moveHudElements[i] = new GuiPanelBase();



    }


    int MouseXOffset = 0;
    int MouseYOffset = 0;
    GuiPanelBase lastSelected = null;

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        int i = 0;
        boolean isMoving = false;

        for (HudElement hud : SystemManager.getHudModules()) {
            if(hud.isEnabled())
            {
                moveHudElements[i].setPositionAndSize(hud.getRenderPosX(),hud.getRenderPosY(),hud.getWidth(),hud.getHeight());


                moveHudElements[i].renderContent(MouseX,MouseY,deltaTime);


                if(moveHudElements[i] == GuiPanelBase.SelectedMouse) {
                    isMoving = true;
                    if(lastSelected == null) {
                        if(Mouse.isButtonDown(1))
                            guiHudSettingTab.guiEditSettingPanel.setCurrentSettingsHolder(hud);
                        lastSelected = GuiPanelBase.SelectedMouse;
                        MouseXOffset = MouseX - lastSelected.posX;
                        MouseYOffset = MouseY - lastSelected.posY;


                    }
                    else {
                        int moveToX = MouseX -MouseXOffset;
                        int moveToY = MouseY -MouseYOffset;

                        if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                            moveToX = MathHelper.clamp(moveToX,0,getWidth()-hud.getWidth());
                            moveToY = MathHelper.clamp(moveToY,0,getHeight()-hud.getHeight());

                        }

                        hud.setRenderPosX(moveToX);
                        hud.setRenderPosY(moveToY);

                        hud.setSnappedElement(-1);



                        if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
                        for (HudElement b : SystemManager.getHudModules()) {
                            if(hud != b && hud.isIn(b,-10) && hud.getId() != b.getSnappedElement()){
                                hud.setSnappedElement(b.getId());




                                double x = ((1.0 / b.getWidth()) * (moveToX-b.getRenderPosX()));
                                double y = ((1.0 / b.getHeight()) * (moveToY-b.getRenderPosY()));

                                hud.setPercentPosX(x);
                                hud.setPercentPosY(y);



                                break;
                            }
                        }


                    }
                }

            }
            i++;
        }

        if(GuiPanelBase.SelectedMouse == null)
            lastSelected = null;

        showMenuBar = !isMoving;

        guiHudSettingTab.renderContent(MouseX,MouseY,deltaTime);


        super.renderContent(MouseX,MouseY,deltaTime);
    }


    @Override
    public void preformAction(GuiPanelButton button) {
        if(button.getId() == 0)
            guiHudSettingTab.guiEditSettingPanel.setCurrentSettingsHolder(HudSettings.getInstance());
    }
}
