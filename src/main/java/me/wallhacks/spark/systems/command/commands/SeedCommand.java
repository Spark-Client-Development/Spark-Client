package me.wallhacks.spark.systems.command.commands;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.manager.CommandManager;
import me.wallhacks.spark.manager.SeedManager;
import me.wallhacks.spark.manager.SystemManager;
import me.wallhacks.spark.systems.command.Command;
import me.wallhacks.spark.systems.module.modules.misc.InventoryManager;
import net.minecraft.client.Minecraft;
import sun.security.ec.ECDSAOperations;

public class SeedCommand extends Command {

	public SeedCommand() {
		super();
		addOption("set", arg -> {
			if(arg != null){
				SystemManager.getModule(InventoryManager.class).setKitFromInventory(arg,false);

				if(!Minecraft.getMinecraft().isSingleplayer()) {
					Spark.seedManager.setSeed(arg);
					Spark.sendInfo("" + CommandManager.COLOR1 + "Set seed for current server");
				}
				else
					Spark.sendInfo("" + CommandManager.ErrorColor + "You need to be on a server!");


			}
		}, "<seed>");

		addOption("get", arg -> {


				if(!Minecraft.getMinecraft().isSingleplayer()) {
					String seed = Spark.seedManager.seedForServer(Minecraft.getMinecraft().getCurrentServerData().serverIP);
					if(seed == null)
						Spark.sendInfo("" + CommandManager.ErrorColor + "No seed set for this server :(");
					Spark.sendInfo("" + CommandManager.COLOR1 + "This server has the seed set to " + CommandManager.COLOR2+" "+seed);
				}
				else
					Spark.sendInfo("" + CommandManager.COLOR1 + "This world uses the seed " + CommandManager.COLOR2+" "+Minecraft.getMinecraft().integratedServer.getEntityWorld().getSeed());

			
		});

		addOption("setForServer", arg -> {
			if(arg != null && arg.split(" ").length > 1){
				String[] list = arg.split(" ");
				Spark.seedManager.setSeed(list[0],arg.substring(list[0].length()));
				Spark.sendInfo(""+ CommandManager.COLOR1+"Set seed for "+CommandManager.COLOR2+list[0]+ ""+CommandManager.COLOR1);

			}
		}, "<server> <seed>");

		addOption("list", arg -> {


			Spark.sendInfo(""+CommandManager.COLOR1+"Seeds:");
			for (String kit : SeedManager.instance.servers()) {
				Spark.sendInfo(""+CommandManager.COLOR1+" - "+CommandManager.COLOR2+kit+": " + SeedManager.instance.seedForServer(kit));
			}
		});


	}

	@Override
	public String getName() {
		return "seed";
	}

}







