package me.wallhacks.spark.systems.command.commands;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.manager.AutoConfigManager;
import me.wallhacks.spark.manager.CommandManager;
import me.wallhacks.spark.systems.autoconfigs.AutoConfig;
import me.wallhacks.spark.systems.command.Command;

public class AutoConfigCommand extends Command {

	public AutoConfigCommand() {
		super();
		addOption("apply", arg -> {
			for (AutoConfig f : AutoConfigManager.configs) {
				if(f.getName().equals(arg))
				{
					f.config();
					Spark.sendInfo(CommandManager.COLOR1+"Applied "+CommandManager.COLOR2+f.getName());
				}
			}

		}, AutoConfigManager.confignames);



		addOption("list", arg -> {

			Spark.sendInfo(""+CommandManager.COLOR1+"Autoconfigs:");
			for (AutoConfig f : AutoConfigManager.configs) {
				Spark.sendInfo(""+CommandManager.COLOR1+" - "+CommandManager.COLOR2+f.getName());
			}


		});

	}

	@Override
	public String getName() {
		return "autoConfig";
	}

}
