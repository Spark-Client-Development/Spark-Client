package me.wallhacks.spark.systems.command.commands;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.manager.CommandManager;
import me.wallhacks.spark.systems.command.Command;

public class FontCommand extends Command {

	public FontCommand() {
		super();
		addOption("set", arg -> {
			if(arg != null){
				Spark.sendInfo(arg);
				Spark.fontManager.setFont(arg);
				Spark.sendInfo(""+ CommandManager.COLOR1+"Font is now "+CommandManager.COLOR2+Spark.fontManager.fontName);


			}
		}, Spark.fontManager.getFonts());

		addOption("reset", arg -> {

			Spark.fontManager.reset();
			Spark.sendInfo(""+ CommandManager.COLOR1+"Font is now "+CommandManager.COLOR2+Spark.fontManager.fontName);

		});

	}

	@Override
	public String getName() {
		return "font";
	}

}
