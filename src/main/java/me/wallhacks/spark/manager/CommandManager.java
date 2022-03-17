package me.wallhacks.spark.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.mojang.realmsclient.gui.ChatFormatting;

import me.wallhacks.spark.systems.command.Command;
import me.wallhacks.spark.systems.command.commands.*;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

public class CommandManager {

	public static final Map<String,Command> COMMANDSBYNAME = new HashMap<>();
	public static final Map<String, ArrayList<String>> COMMANDUSAGES = new HashMap<>();
	
	public CommandManager() {
		new HClipCommand();
		new VClipCommand();
		new KitCommand();
		new FriendCommand();
		new ConfigCommand();
		new WaypointManager();
		new NoteBotCommand();
		new DamageCommand();
		new SeedCommand();
		new FontCommand();
	}

	public static ChatFormatting ErrorColor = ChatFormatting.RED;
	public static ChatFormatting COLOR1 = ChatFormatting.GRAY;
	public static ChatFormatting COLOR2 = ChatFormatting.DARK_GRAY;
}
