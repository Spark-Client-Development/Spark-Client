package me.wallhacks.spark.systems.command.commands;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.manager.CommandManager;
import me.wallhacks.spark.manager.SystemManager;
import me.wallhacks.spark.systems.command.Command;
import me.wallhacks.spark.systems.module.modules.mics.InventoryManager;

public class ConfigCommand extends Command {

	public ConfigCommand() {
		super();
		addOption("create", arg -> {
			if(arg != null){
				Spark.configManager.saveToConfig(arg);
				Spark.sendInfo(""+ CommandManager.COLOR1+"Config "+CommandManager.COLOR2+arg+ ""+CommandManager.COLOR1+" has been created!");


			}
		}, "<configname>");

		addOption("select", arg -> {
			if(arg != null){
				if(Spark.configManager.loadConfig(arg))
					Spark.sendInfo(""+ CommandManager.COLOR1+"Config "+CommandManager.COLOR2+arg+ ""+CommandManager.COLOR1+" has been selected!");
				else
					Spark.sendInfo(""+ CommandManager.ErrorColor+"Config "+CommandManager.COLOR2+arg+ ""+CommandManager.ErrorColor+" can't be found!");

			}
		}, "<configname>");

		addOption("delete", arg -> {
			if(arg != null){
				if(Spark.configManager.deleteConfig(arg))
					Spark.sendInfo(""+ CommandManager.COLOR1+"Config "+CommandManager.COLOR2+arg+ ""+CommandManager.COLOR1+" has been selected!");
				else
					Spark.sendInfo(""+ CommandManager.ErrorColor+"Config "+CommandManager.COLOR2+arg+ ""+CommandManager.ErrorColor+" can't be found!");

			}
		}, "<configname>");

		addOption("list", arg -> {

			String current = Spark.configManager.getCurrentConfigName();

			Spark.sendInfo(""+CommandManager.COLOR1+"Config List:");
			for (String c : Spark.configManager.getList()) {
				Spark.sendInfo(""+CommandManager.COLOR1+" - "+(current.equals(c) ? "*" : "")+CommandManager.COLOR2+c);
			}
		});

	}

	@Override
	public String getName() {
		return "config";
	}

}
