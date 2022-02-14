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
				Spark.sendInfo(""+ CommandManager.COLOR1+"Kit "+CommandManager.COLOR2+arg+ ""+CommandManager.COLOR1+" has been seleted!");

			}
		}, "<kitname>");

		addOption("delete", arg -> {
			if(arg != null){
				SystemManager.getModule(InventoryManager.class).deleteKit(arg);
				Spark.sendInfo(""+ CommandManager.COLOR1+"Kit "+CommandManager.COLOR2+arg+ ""+CommandManager.COLOR1+" has been deleted!");

			}
		}, "<kitname>");

		addOption("list", arg -> {

			String currentKit = SystemManager.getModule(InventoryManager.class).currentKit;

			Spark.sendInfo(""+CommandManager.COLOR1+"Kit List:");
			for (String kit : SystemManager.getModule(InventoryManager.class).getKits().keySet()) {
				Spark.sendInfo(""+CommandManager.COLOR1+" - "+(kit.equals(currentKit) ? "*" : "")+CommandManager.COLOR2+kit);
			}
		});

	}

	@Override
	public String getName() {
		return "kit";
	}

}
