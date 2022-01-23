package me.wallhacks.spark.systems.command.commands;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.manager.CommandManager;
import me.wallhacks.spark.manager.SystemManager;
import me.wallhacks.spark.systems.command.Command;
import me.wallhacks.spark.systems.module.modules.mics.InventoryManager;

public class KitCommand extends Command {

	public KitCommand() {
		super();
		addOption("put", arg -> {
			if(arg != null){
				SystemManager.getModule(InventoryManager.class).setKitFromInventory(arg,false);
				Spark.sendInfo(""+ CommandManager.COLOR1+"Kit "+CommandManager.COLOR2+arg+ ""+CommandManager.COLOR1+" has been copied from inventory!");


			}
		}, "<kitname>");

		addOption("select", arg -> {
			if(arg != null){
				SystemManager.getModule(InventoryManager.class).selectKit(arg);
				Spark.sendInfo(""+ CommandManager.COLOR1+"Kit "+CommandManager.COLOR2+arg+ ""+CommandManager.COLOR1+" has been selected!");

			}
		}, "<kitname>");

		addOption("list", arg -> {

			Spark.sendInfo(""+CommandManager.COLOR1+"List:");
			for (String kit : SystemManager.getModule(InventoryManager.class).kits.keySet()) {
				Spark.sendInfo(""+CommandManager.COLOR1+" - "+CommandManager.COLOR2+kit);
			}
		});

	}

	@Override
	public String getName() {
		return "kit";
	}

}
