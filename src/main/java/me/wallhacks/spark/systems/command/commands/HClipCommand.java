package me.wallhacks.spark.systems.command.commands;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.manager.CommandManager;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.systems.command.Command;

public class HClipCommand extends Command implements MC {

	@Override
	public String getName() {
		return "hclip";
	}
	
	public HClipCommand() {
		super();
		addUsage("<number>");
	}

	@Override
	public void run(String[] args) {
		if(args.length >= 1) {
			try {
				double i = Double.parseDouble(args[0]);
				mc.player.setPosition(posX() + i * -Math.sin(Math.toRadians(rotationYaw())), posY(), posZ() + i * Math.cos(Math.toRadians(rotationYaw())));
				Spark.sendInfo("Teleported " + CommandManager.COLOR2 + i + CommandManager.COLOR1 + " blocks forward.");
			} catch(NumberFormatException e) {
				Spark.sendInfo(""+CommandManager.ErrorColor+"Invalid argument! Argument is not a number!");
			}
		} else {
			noArgInfo();
		}
	}
}
