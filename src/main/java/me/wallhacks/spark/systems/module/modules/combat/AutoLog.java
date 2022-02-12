package me.wallhacks.spark.systems.module.modules.combat;

import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "AutoLog", description = "Logs under certain scenarios")
public class AutoLog extends Module {
    IntSetting logHealth = new IntSetting("Health",this,4,1,19);
    boolean allowLog = true; // to be able to log back in

	@SubscribeEvent
	public void onUpdate(PlayerUpdateEvent event) {
		double health = Minecraft.getMinecraft().player.getHealth();
	
		if (health <= logHealth.getValue()) {
			if (!allowLog)
				FMLClientHandler.instance().getClientToServerNetworkManager().closeChannel(new TextComponentString("Health was: " + String.valueOf((int)Math.ceil(health))));
			allowLog = true;
		} else {
			allowLog = false;
		}
	}
}
