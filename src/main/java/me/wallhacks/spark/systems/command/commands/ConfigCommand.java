package me.wallhacks.spark.systems.command.commands;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.manager.CommandManager;
import me.wallhacks.spark.manager.ConfigManager;
import me.wallhacks.spark.systems.command.Command;

public class ConfigCommand extends Command {

	public ConfigCommand() {
		super();
		addOption("create", arg -> {
			if(arg != null){
				if(Spark.configManager.getConfigFromName(arg) == null)
				{
					Spark.configManager.createConfig(new ConfigManager.Config(arg));
					Spark.sendInfo(""+ CommandManager.COLOR1+"Config "+CommandManager.COLOR2+arg+ ""+CommandManager.COLOR1+" has been created!");

				}


			}
		}, "<configname>");

		addOption("select", arg -> {
			if(arg != null){
				if(Spark.configManager.loadConfig(Spark.configManager.getConfigFromName(arg),true))
					Spark.sendInfo(""+ CommandManager.COLOR1+"Config "+CommandManager.COLOR2+arg+ ""+CommandManager.COLOR1+" has been selected!");
				else
					Spark.sendInfo(""+ CommandManager.ErrorColor+"Config "+CommandManager.COLOR2+arg+ ""+CommandManager.ErrorColor+" can't be found!");

			}
		}, "<configname>");

		addOption("delete", arg -> {
			if(arg != null){
				if(Spark.configManager.deleteConfig(Spark.configManager.getConfigFromName(arg)))
					Spark.sendInfo(""+ CommandManager.COLOR1+"Config "+CommandManager.COLOR2+arg+ ""+CommandManager.COLOR1+" has been selected!");
				else
					Spark.sendInfo(""+ CommandManager.ErrorColor+"Config "+CommandManager.COLOR2+arg+ ""+CommandManager.ErrorColor+" can't be found!");

			}
		}, "<configname>");

		addOption("list", arg -> {

			ConfigManager.Config current = Spark.configManager.getCurrentConfig();

			Spark.sendInfo(""+CommandManager.COLOR1+"Config List:");
			for (ConfigManager.Config c : Spark.configManager.getConfigs()) {
				Spark.sendInfo(""+CommandManager.COLOR1+" - "+(current.equals(c) ? "*" : "")+CommandManager.COLOR2+c.getConfigName());
			}
		});

	}

	@Override
	public String getName() {
		return "config";
	}

}
