package me.wallhacks.spark.gui.panels;


import me.wallhacks.spark.Spark;
import me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.GuiEditModuleSettings;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.settings.GuiKeySettingPanel;
import me.wallhacks.spark.manager.FontManager;

import java.io.IOException;

public class GuiPanelScreen extends GuiScreen {

    protected final GuiScreen lastScreen;

    protected final FontManager fontManager;

    @SubscribeEvent
    public void onCrosshairRender(RenderGameOverlayEvent.Pre e) {
        if (e.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            e.setCanceled(true);
        }
    }

    @Override
    public void initGui() {
        Spark.eventBus.register(this);
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        Spark.eventBus.unregister(this);
        super.onGuiClosed();
    }

    public GuiPanelScreen() {
        this(null);

    }
    public GuiPanelScreen(final GuiScreen prev) {
        this.lastScreen = prev;

        GuiPanelBase.SelectedMouse = null;
        GuiPanelBase.TopMouseOn = null;
        GuiPanelBase.FocusedMouse = null;

        this.fontManager = Spark.fontManager;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {

        if (keyCode == 1) {
            if((GuiPanelBase.FocusedMouse instanceof GuiEditModuleSettings && ((GuiEditModuleSettings)GuiPanelBase.FocusedMouse).isWaitingForKey()) || GuiPanelBase.FocusedMouse instanceof GuiKeySettingPanel)
            {
                GuiPanelBase.FocusedMouse = null;
                GuiPanelBase.SelectedMouse = null;
                return;
            }
            close();
        }
        else {
            if(GuiPanelBase.FocusedMouse != null)
                GuiPanelBase.FocusedMouse.onKey(keyCode,typedChar);
        }
    }
    public void close() {
        this.mc.displayGuiScreen(lastScreen);
        if (this.mc.currentScreen == null) {
            this.mc.setIngameFocus();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }



    public void renderContent(int MouseX, int MouseY, float deltaTime) {

    }

    boolean wasClicked = false;

    public void handleMouse(int MouseX, int MouseY) {


        if((Mouse.isButtonDown(0) || Mouse.isButtonDown(1) || Mouse.isButtonDown(2) || Mouse.isButtonDown(3) || Mouse.isButtonDown(4)))
        {

            if(GuiPanelBase.SelectedMouse != null || GuiPanelBase.TopMouseOn != null){

                int button = 0;
                if(Mouse.isButtonDown(0))
                    button = 0;
                else if(Mouse.isButtonDown(1))
                    button = 1;
                else if(Mouse.isButtonDown(2))
                    button = 2;
                else if(Mouse.isButtonDown(3))
                    button = 3;
                else if(Mouse.isButtonDown(4))
                    button = 4;

                if(!wasClicked)
                {
                    GuiPanelBase.SelectedMouse = GuiPanelBase.TopMouseOn;
                    GuiPanelBase.SelectedMouse.onClickDown(button);
                }
                if(GuiPanelBase.SelectedMouse != null)
                    GuiPanelBase.SelectedMouse.onClick(button);



                GuiPanelBase.FocusedMouse = GuiPanelBase.SelectedMouse;
            }
            else
                GuiPanelBase.FocusedMouse = null;

            wasClicked = true;
        }
        else
        {
            wasClicked = false;
            GuiPanelBase.SelectedMouse = null;
        }

        setFocused(GuiPanelBase.FocusedMouse != null);


        GuiPanelBase.TopMouseOn = null;

    }


    private long Time = System.nanoTime();
    @Override
    public void drawScreen(int MouseX, int MouseY, float PartialTicks) {
        super.drawScreen(MouseX,MouseY, PartialTicks);


        float deltaTime = (System.nanoTime()-Time)/1000000f;

        renderContent(MouseX, MouseY, deltaTime);
        Time = System.nanoTime();

        handleMouse(MouseX,MouseY);
    }

    protected int getCenterX() {
        return width/2;
    }
    protected int getCenterY() {
        return height/2;
    }
}