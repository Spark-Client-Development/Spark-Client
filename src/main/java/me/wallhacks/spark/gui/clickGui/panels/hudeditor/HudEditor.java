package me.wallhacks.spark.gui.clickGui.panels.hudeditor;

import me.wallhacks.spark.gui.clickGui.ClickGuiMenuBase;
import me.wallhacks.spark.gui.clickGui.ClickGuiPanel;
import me.wallhacks.spark.gui.clickGui.panels.hudeditor.hudsList.GuiHudSettingTab;
import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.gui.panels.GuiPanelButton;
import me.wallhacks.spark.manager.SystemManager;
import me.wallhacks.spark.util.GuiUtil;
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
    HudElement selectedHudElement = null;

    public HudElement getSelected() {

        return selectedHudElement;

    }

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {
        int i = 0;
        boolean isMoving = false;

        for (HudElement hud : SystemManager.getHudModules()) {
            if(hud.isEnabled())
            {



                if(moveHudElements[i] == GuiPanelBase.SelectedMouse) {
                    isMoving = true;
                    if(lastSelected == null) {
                        if(Mouse.isButtonDown(1))
                            guiHudSettingTab.guiEditSettingPanel.setCurrentSettingsHolder(hud);
                        lastSelected = GuiPanelBase.SelectedMouse;
                        selectedHudElement = hud;


                    }
                    else {
                        lastSelected.posX = MouseX -MouseXOffset;
                        lastSelected.posY = MouseY -MouseYOffset;

                        if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                            lastSelected.posX = MathHelper.clamp(lastSelected.posX,0,getWidth()-hud.getWidth()+1);
                            lastSelected.posY = MathHelper.clamp(lastSelected.posY,0,getHeight()-hud.getHeight()+1);


                        }


                        hud.setSnappedElement(-1);



                        hud.setRenderPosX(lastSelected.posX);
                        hud.setRenderPosY(lastSelected.posY);

                        if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
                        {
                            moduleLoop:
                            for (HudElement b : SystemManager.getHudModules()) {
                                if(b.isEnabled())
                                    if(hud != b && hud.isIn(b,-10) && hud.getId() != b.getSnappedElement()){

                                        HudElement check = b;
                                        while(check != null)
                                        {
                                            check = check.getSnappedElementObject();
                                            if(check == hud)
                                                continue moduleLoop;
                                        }



                                        //snap
                                        if(hud.isIn(b))
                                        {
                                            int yDis = Math.abs(hud.getCenterRenderPosY() < b.getCenterRenderPosY() ? hud.getEndRenderPosY()-b.getRenderPosY() : b.getEndRenderPosY()-hud.getRenderPosY());
                                            int xDis = Math.abs(hud.getCenterRenderPosX() < b.getCenterRenderPosX() ? hud.getEndRenderPosX()-b.getRenderPosX() : b.getEndRenderPosX()-hud.getRenderPosX());

                                            if(yDis < xDis)
                                            {
                                                lastSelected.posY = (hud.getCenterRenderPosY() < b.getCenterRenderPosY() ? b.getRenderPosY()-hud.getHeight() : b.getEndRenderPosY());
                                            }
                                            else
                                            {
                                                lastSelected.posX = (hud.getCenterRenderPosX() < b.getCenterRenderPosX() ? b.getRenderPosX()-hud.getWidth() : b.getEndRenderPosX());
                                            }

                                        }

                                        //attach
                                        hud.setSnappedElement(b.getId());


                                        //update relative pos
                                        hud.setRenderPosX(lastSelected.posX);
                                        hud.setRenderPosY(lastSelected.posY);



                                        break;
                                    }
                            }

                        }




                    }

                    MouseXOffset = MathHelper.clamp(MouseX - lastSelected.posX,0,lastSelected.width);
                    MouseYOffset = MathHelper.clamp(MouseY - lastSelected.posY,0,lastSelected.height);


                }
                moveHudElements[i].setPositionAndSize(hud.getRenderPosX(),hud.getRenderPosY(),hud.getWidth(),hud.getHeight());
                moveHudElements[i].renderContent(MouseX,MouseY,deltaTime);

            }
            i++;
        }

        if(GuiPanelBase.SelectedMouse == null)
        {
            selectedHudElement = null;
            lastSelected = null;
        }



        showMenuBar = !isMoving;

        guiHudSettingTab.renderContent(MouseX,MouseY,deltaTime);


        super.renderContent(MouseX,MouseY,deltaTime);
    }



}
