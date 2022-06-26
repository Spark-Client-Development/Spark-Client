package me.wallhacks.spark.systems.module.modules.misc;


import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.entity.DeathEvent;
import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.event.player.PacketSendEvent;
import me.wallhacks.spark.manager.CombatManager;
import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;
import me.wallhacks.spark.systems.command.commands.Post;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "AntiCoordLeak", description = "")
public class AntiCoordLeak extends Module implements MC {

	public static AntiCoordLeak instance;
	public AntiCoordLeak(){
		instance = this;
	}



	@SubscribeEvent
	public void onPacketSent(PacketSendEvent event) {
		if(nullCheck())
			return;
		if (event.getPacket() instanceof CPacketChatMessage) {
			CPacketChatMessage packet = event.getPacket();

			if(containsCoordinates(packet.getMessage()))
			{
				Spark.sendInfo("Are you sure you want to send that message?");

				TextComponentString warningMessage = new TextComponentString(ChatFormatting.GRAY+"It looks like that message contains coordinates.");

				mc.player.sendMessage(warningMessage);



				TextComponentString sendButton = new TextComponentString(ChatFormatting.RED + "[Send anyways]");
				Style s = new Style();
				s.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ClientConfig.getInstance().getChatPrefix()+ Post.instance.getName()+" " + packet.getMessage()));
				sendButton.setStyle(s);


				sendButton.appendSibling(new TextComponentString(ChatFormatting.GRAY+" "+packet.getMessage()));

				mc.player.sendMessage(sendButton);


				event.setCanceled(true);
			}
		}

	}

	private boolean containsCoordinates(String message) {
		return message.matches(".*(?<x>-?\\d{3,}(?:\\.\\d*)?)(?:\\s+(?<y>\\d{1,3}(?:\\.\\d*)?))?\\s+(?<z>-?\\d{3,}(?:\\.\\d*)?).*");
	}

}
