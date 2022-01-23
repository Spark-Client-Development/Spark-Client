package me.wallhacks.spark.systems.command.commands;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.manager.CommandManager;
import me.wallhacks.spark.systems.command.Command;
import me.wallhacks.spark.util.MC;
import net.minecraft.network.play.client.CPacketPlayer;

public class VClipCommand extends Command implements MC {

	@Override
	public String getName() {
		return "vclip";
	}
	
	public VClipCommand() {
		super();
		addOption(null, arg -> task1(arg), "<number>");
		addOption("bypass", arg -> {
			if(mc.player.onGround) {
				mc.getConnection().sendPacket(new CPacketPlayer.Position(posX(), posY()-1e-10, posZ(), true));
			}
			task1(arg);
		}, "<number>");
	}

	public void task1(String arg) {
		try {
			double i = Double.parseDouble(arg);
			mc.player.setPosition(posX(), posY() + i, posZ());
			Spark.sendInfo("Teleported " + CommandManager.COLOR2 + Math.abs(i) + CommandManager.COLOR1 + " blocks " + (i >= 0 ? "upwards." : "downwards."));
		} catch(NumberFormatException e) {
			Spark.sendInfo(""+CommandManager.ErrorColor+"Invalid argument! Argument is not a number!");
		}
	}
}
