package me.wallhacks.spark.systems.module.modules.movement;

import me.wallhacks.spark.event.player.PlayerLivingTickEvent;
import me.wallhacks.spark.event.player.UpdateEntityAction;
import me.wallhacks.spark.gui.clickGui.ClickGuiMenuBase;
import me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.settings.GuiDoubleSettingPanel;
import me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.settings.GuiIntSettingPanel;
import me.wallhacks.spark.gui.clickGui.panels.mainScreen.setting.settings.GuiKeySettingPanel;
import me.wallhacks.spark.gui.panels.GuiPanelBase;
import me.wallhacks.spark.gui.panels.GuiPanelInputField;
import me.wallhacks.spark.gui.panels.GuiPanelScreen;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

@Module.Registration(name = "InventoryMove", description = "Sprint when we can")
public class InventoryMove extends Module {


    @SubscribeEvent
    public void onUpdateEntityAction(UpdateEntityAction event){

        if(useInventoryMove() && mc.player.movementInput.moveForward == 0 && mc.player.movementInput.moveStrafe == 0) {



            if(Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()))
                mc.player.movementInput.moveForward += 1;
            if(Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()))
                mc.player.movementInput.moveForward  -= 1;

            if(Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()))
                mc.player.movementInput.moveStrafe += 1;
            if(Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()))
                mc.player.movementInput.moveStrafe -= 1;

            mc.player.movementInput.jump = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());

        }

    }

    @SubscribeEvent
    public void onUpdate(PlayerLivingTickEvent event){

        if(useInventoryMove()) {
            if(Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_DOWN))
                mc.player.rotationPitch = Math.max(-90, Math.min(90, mc.player.rotationPitch+(Keyboard.isKeyDown(Keyboard.KEY_UP) ? -1 : 1)*4));
            if(Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
                mc.player.rotationYaw += (Keyboard.isKeyDown(Keyboard.KEY_LEFT) ? -1 : 1)*4;
        }




    }

    boolean useInventoryMove(){
        return (mc.currentScreen != null &&
                !(mc.currentScreen instanceof GuiChat) &&
                !(mc.currentScreen instanceof GuiEditSign) &&
                !(mc.currentScreen instanceof GuiScreenBook) &&
                !(mc.currentScreen instanceof GuiPanelScreen &&
                        (GuiPanelBase.FocusedMouse instanceof GuiPanelInputField || GuiPanelBase.FocusedMouse instanceof GuiIntSettingPanel ||
                                GuiPanelBase.FocusedMouse instanceof GuiDoubleSettingPanel || GuiPanelBase.FocusedMouse instanceof GuiKeySettingPanel)));
    }






}
