package me.wallhacks.spark.systems.module.modules.mics;

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

	ModeSetting mode = new ModeSetting("Mode", this, "AutoDox", Arrays.asList("AutoDox"));
	
	@SubscribeEvent
	public void onPacket(PacketReceiveEvent e) {
		if(mode.isValueName("AutoDox")) {
			if(e.getPacket() instanceof SPacketCombatEvent) {
				SPacketCombatEvent packet = e.getPacket();
				if(packet.eventType == Event.ENTITY_DIED) {
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
					if(target != null) {
						mc.player.sendChatMessage("https://doxbin.org/upload/" + target.getName());
					}
				}
			}
		}
	}
}
