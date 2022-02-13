package me.wallhacks.spark.systems.module.modules.mics;

import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.StringSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "AutoLog", description = "Logs under certain scenarios")
public class AutoLog extends Module {

    IntSetting logHealth = new IntSetting("Health",this,4,1,19);
	IntSetting logTimer = new IntSetting("EnableWaitTimer",this,40,5,120);


	BooleanSetting larpMsg = new BooleanSetting("LarpMsg",this,false);
	StringSetting customMsg = new StringSetting("CustomMsg",this,"Internal server error", v -> larpMsg.isOn(),"General");

	//waits x amount of ticks on relog before kicking again
	int timer = 0;

	@SubscribeEvent
	public void onUpdate(PlayerUpdateEvent event) {
		if(timer < logTimer.getValue())
		{
			timer++;
			return;
		}

		if (mc.player.getHealth() <= logHealth.getValue()) {
			Log("Health was: " + ((int)Math.ceil(mc.player.getHealth())));
		}
	}

	void Log(String info) {
		if(larpMsg.isOn())
			info = customMsg.getValue();

		FMLClientHandler.instance().getClientToServerNetworkManager().closeChannel(new TextComponentString(info));
		timer = 0;
	}
}
