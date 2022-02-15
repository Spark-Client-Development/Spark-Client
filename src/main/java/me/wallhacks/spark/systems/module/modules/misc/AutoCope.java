package me.wallhacks.spark.systems.module.modules.misc;

import java.util.Arrays;

import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketCombatEvent.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;

@Module.Registration(name = "AutoCope", description = "")
public class AutoCope extends Module implements MC {

	ModeSetting mode = new ModeSetting("Mode", this, "AutoDox", Arrays.asList("AutoDox","AutoExcuse"));

	String[] excuses = new String[]{
		"my mom walked in!",
		"my keyboard fell on the floor",
		"i desynced!",
		"my internet cutoff",
	};

	@SubscribeEvent
	public void onPacket(PacketReceiveEvent e) {
		if(e.getPacket() instanceof SPacketCombatEvent) {
			SPacketCombatEvent packet = e.getPacket();
			if(packet.eventType == Event.ENTITY_DIED) {

				try	{
					if(mode.isValueName("AutoDox"))
						Dox();
					if(mode.isValueName("AutoExcuse"))
						Excuse();

				}
				catch (Exception ex)
				{

				}
			}
		}

	}

	void Dox() {
		EntityPlayer target = killer();
		if(target != null)
			mc.player.sendChatMessage("https://doxbin.org/upload/" + target.getName());

	}
	void Excuse() {
		mc.player.sendChatMessage(excuses[(int)(Math.random()*(excuses.length-0.1f))]);
	}


	EntityPlayer killer () {
		EntityPlayer target = null;
		float minD = Float.MAX_VALUE;
		for(EntityPlayer player : mc.world.playerEntities) {
			if(player == mc.player) continue;
			float d = player.getDistance(mc.player);
			if(minD >= d) {
				minD = d;
				target = player;
			}
		}
		return target;

	}
}
