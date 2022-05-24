package me.wallhacks.spark.systems.command.commands;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.manager.CommandManager;
import me.wallhacks.spark.manager.ConfigManager;
import me.wallhacks.spark.systems.command.Command;

public class ConfigCommand extends Command {

	public ConfigCommand() {
		super();
		addOption("save", arg -> {
			Spark.configManager.Save();
			Spark.sendInfo(CommandManager.COLOR1+"Config has been saved!");

		});

		addOption("load", arg -> {
			Spark.configManager.Load();
			Spark.sendInfo(CommandManager.COLOR1+"Config has been loaded!");

		});



	}

	@Override
	public String getName() {
		return "config";
	}

}
