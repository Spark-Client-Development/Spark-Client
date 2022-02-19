package me.wallhacks.spark.systems.command;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PacketSendEvent;
import me.wallhacks.spark.manager.CommandManager;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;
import me.wallhacks.spark.systems.command.Command.Option;

public class CommandHandler {
	
	@SubscribeEvent
	public void onPacketOut(PacketSendEvent e) {
		if(e.getPacket() instanceof CPacketChatMessage) {
			CPacketChatMessage packet = (CPacketChatMessage) e.getPacket();
			String prefix = ClientConfig.getInstance().getChatPrefix();
			if(packet.getMessage().length() >= 1 && packet.getMessage().startsWith(prefix)) {
				String commandLine = packet.getMessage().substring(prefix.length());
				String[] args = commandLine.split(" ");
				Command command = CommandManager.COMMANDSBYNAME.get(args[0]);
				if(command != null) {
					List<String> argsList = new ArrayList<>();
					for(String a : args) {
						argsList.add(a);
					}
					argsList.remove(0);
					command.run(argsList.toArray(new String[0]));
				} else {
					Spark.sendError("Invalid command!");
				}
				e.setCanceled(true);
			}
		}
	}
	
	List<String> possibilities = new ArrayList<>();
	int selected = 0;
	
	@SubscribeEvent
	public void onGuiChat(GuiScreenEvent.DrawScreenEvent.Post e) {
		if(e.getGui() instanceof GuiChat) {
			GuiChat chat = (GuiChat) e.getGui();
			String message = chat.inputField.getText();
			if(!message.isEmpty()) {
				String prefix = ClientConfig.getInstance().getChatPrefix();
				if(message.startsWith(prefix)) {
					String commandLine = message.substring(prefix.length()).toLowerCase();
					List<String> possibilities = getAutoCompleteForLine(commandLine);
					String command = commandLine.substring(0, Math.max(commandLine.lastIndexOf(" "),0));
					if(!possibilities.isEmpty()) {
						Collections.sort(possibilities);
						int width = chat.fontRenderer.getStringWidth(Collections.max(possibilities.subList(0, MathHelper.clamp(10, 0, possibilities.size())), (o1,o2) -> {return chat.fontRenderer.getStringWidth(o1) - chat.fontRenderer.getStringWidth(o2);})) + 1; 
						int y = chat.inputField.y - 12;
						for(int i = 0; i < MathHelper.clamp(possibilities.size(), 0, 10); i++) {
							int actualY = y - i * 10;
							
							int xOffset = chat.fontRenderer.getStringWidth(prefix);
							if(!command.isEmpty()) {
								xOffset += chat.fontRenderer.getStringWidth(command + " ");
							}
							Gui.drawRect(chat.inputField.x+xOffset-2, actualY, chat.inputField.x+xOffset+width, actualY+10, (selected == i && possibilities.size() > 1 ? new Color(50, 50, 50, 240).getRGB() : new Color(0, 0, 0, 220).getRGB()));
							chat.fontRenderer.drawStringWithShadow(possibilities.get(i), chat.inputField.x+xOffset, actualY + 1, new Color(100, 100, 100).getRGB());
							chat.fontRenderer.drawStringWithShadow(commandLine.substring(Math.max(message.lastIndexOf(" "),0)), chat.inputField.x+xOffset, actualY + 1, Color.WHITE.getRGB());
						}
					}
					this.possibilities = possibilities;
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onGuiInput(GuiScreenEvent.KeyboardInputEvent.Pre e) {
		if(e.getGui() instanceof GuiChat && !possibilities.isEmpty()) {
			GuiChat chat = (GuiChat) e.getGui();
			String message = chat.inputField.getText();
			if(!message.isEmpty()) {
				String prefix = ClientConfig.getInstance().getChatPrefix();
				if(message.startsWith(prefix)) {
					String commandLine = message.substring(prefix.length());
					String command = commandLine.substring(0, Math.max(commandLine.lastIndexOf(" "),0));
					if(Keyboard.isKeyDown(Keyboard.KEY_TAB)) {
						String autoComplete;
						if(command.isEmpty()) {
							autoComplete = prefix + possibilities.get(selected) + " ";
						} else {
							autoComplete = prefix + command + " " + possibilities.get(selected);
							if(!getAutoCompleteForLine(command + " " + possibilities.get(selected) + " ").isEmpty()) {
								autoComplete += " ";
							}
							if(autoComplete.contains("<")) {
								autoComplete = autoComplete.substring(0, autoComplete.indexOf("<"));
							}
						}
						if(chat.inputField.getText().startsWith(autoComplete))
							return;
						chat.inputField.setText(autoComplete);
						selected = 0;
						e.setCanceled(true);
					}
					if(Keyboard.isKeyDown(Keyboard.KEY_UP)) {
						selected++;
						selected = MathHelper.clamp(selected, 0, MathHelper.clamp(possibilities.size()-1, 0, 9));
						e.setCanceled(true);
					} else if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
						selected--;
						selected = MathHelper.clamp(selected, 0, MathHelper.clamp(possibilities.size()-1, 0, 9));
						e.setCanceled(true);
					} else if((int) Keyboard.getEventCharacter() != 0){
						selected = 0;
					}
				}
			}
		}


	}

	//Sorry, not motivated enough to make the code cleaner...
	//Have fun with these bunch of if-else statements!
	private List<String> getAutoCompleteForLine(String commandLine) {
		String[] commandArgs = commandLine.trim().split(" ");
		List<String> possibilities = new ArrayList<>();
		if(commandArgs.length > 0 && commandArgs.length < 3 || (commandArgs.length < 4 && !commandLine.endsWith(" "))) {
			Command command = CommandManager.COMMANDSBYNAME.get(commandArgs[0]);
			if(command != null) {
				if(commandLine.endsWith(" ") || commandArgs.length > 1) {
					List<Option> options = command.getOptions();
					for(Option option : options) {
						String oname = option.getName();
						if(oname != null) {
							if(commandArgs.length > 1) {
								if(oname.equals(commandArgs[1])) {
									List<String> usages = option.getUsages();
									if(!usages.isEmpty() && (commandLine.endsWith(" ") || commandArgs.length > 2)) {
										if(commandArgs.length > 2) {
											for(String usage : usages) {
												if(!usage.startsWith("<")) {
													if(usage.startsWith(commandArgs[2])) {
														possibilities.add(usage);
													}
												}
											}
										} else {
											for(String usage : usages) {
												possibilities.add(usage);
											}
										}
									}
								} else if(oname.startsWith(commandArgs[1])){
									possibilities.add(oname);
								}
							} else {
								possibilities.add(oname);
							}
						} else {
							List<String> usages = option.getUsages();
							if(commandArgs.length > 2) {
								for(String usage : usages) {
									if(usage.startsWith(commandArgs[2])) {
										possibilities.add(usage);
									}
								}
							} else {
								for(String usage : usages) {
									possibilities.add(usage);
								}
							}
						}
					}
				} else {
					possibilities.add(command.getName());
				}
			} else if(commandArgs.length == 1){
				for(String c : CommandManager.COMMANDSBYNAME.keySet()) {
					if(c.startsWith(commandLine)) {
						possibilities.add(c);
					}
				}
			}
		}
		return possibilities;
	}

}

