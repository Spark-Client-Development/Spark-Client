package me.wallhacks.spark.systems.command.commands;


import io.netty.util.concurrent.GenericFutureListener;
import me.wallhacks.spark.systems.command.Command;
import me.wallhacks.spark.util.MC;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.CPacketChatMessage;

public class Post extends Command implements MC {

	@Override
	public String getName() {
		return "post";
	}

	public static Post instance;

	public Post() {
		super();
		instance = this;

		addOption(null, arg -> task1(arg), "<message>");

	}

	public void task1(String arg) {

		CPacketChatMessage c = new CPacketChatMessage(arg);



		//bypass mixin
		if (mc.player.connection.netManager.isChannelOpen()) {
			mc.player.connection.netManager.flushOutboundQueue();
			mc.player.connection.netManager.dispatchPacket(c, (GenericFutureListener[])null);
		}

	}


}
