package me.wallhacks.spark.systems.command.commands;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.manager.CommandManager;
import me.wallhacks.spark.systems.command.Command;

public class FontCommand extends Command {

    public FontCommand() {
        super();
        addOption("set", arg -> {
            if (arg != null) {
                Spark.sendInfo(arg);
                Spark.fontManager.setFont(arg);
                Spark.sendInfo("" + CommandManager.COLOR1 + "Font is now " + CommandManager.COLOR2 + Spark.fontManager.fontName);


            }
        }, Spark.fontManager.getFonts());

        addOption("size", arg -> {
            if (arg != null) {
                Spark.sendInfo(arg);
                try {
                    Spark.fontManager.setFontSize(Integer.parseInt(arg));
                } catch (Exception e) {
                    Spark.sendInfo("It needs to be an actual number retard");

                }
            }
        }, "<size>");

        addOption("reset", arg -> {

            Spark.fontManager.reset();
            Spark.sendInfo("" + CommandManager.COLOR1 + "Font is now " + CommandManager.COLOR2 + Spark.fontManager.fontName);

        });

    }

    @Override
    public String getName() {
        return "font";
    }

}
