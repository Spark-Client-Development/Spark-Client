package me.wallhacks.spark.systems.module.modules.misc;

import org.lwjgl.input.Keyboard;

import me.wallhacks.spark.systems.module.Module;
import net.minecraft.client.gui.GuiChat;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "PersistentChat", description = "Keeps the chat message your started typing but didn't send")
public class PersistentChat extends Module {

	String saved = "";
	
	@SubscribeEvent
	public void onOpenChat(GuiScreenEvent.InitGuiEvent.Post e) throws IllegalArgumentException, IllegalAccessException {
		if(e.getGui() instanceof GuiChat) {
			GuiChat chat = (GuiChat) e.getGui();
			chat.inputField.setText(saved);
		}
	}
	
	@SubscribeEvent
	public void onOpenChat(GuiScreenEvent.KeyboardInputEvent.Pre e) {
		if(e.getGui() instanceof GuiChat) {
			if(Keyboard.isKeyDown(Keyboard.KEY_RETURN)) saved = "";
		}
	}
	
	@SubscribeEvent
	public void onOpenChat(GuiScreenEvent.KeyboardInputEvent.Post e) throws IllegalArgumentException, IllegalAccessException {
		if(e.getGui() instanceof GuiChat) {
			GuiChat chat = (GuiChat) e.getGui();
			saved = chat.inputField.getText();
		}
	}
}
