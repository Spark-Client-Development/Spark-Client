package me.wallhacks.spark.systems.module.modules.misc;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.StringSetting;
import net.minecraft.init.Items;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "AutoLog", description = "Logs under certain scenarios")
public class AutoLog extends Module {

	BooleanSetting useHealth = new BooleanSetting("UseHealth",this,true);
	IntSetting logHealth = new IntSetting("Health",this,4,1,19, v -> useHealth.isOn());
	BooleanSetting useTotems = new BooleanSetting("UseTotems",this,false);
	IntSetting logTotems = new IntSetting("Totems",this,4,1,19, v -> useTotems.isOn());

	IntSetting logTimer = new IntSetting("EnableWaitTimer",this,40,0,120);


	BooleanSetting larpMsg = new BooleanSetting("LarpMsg",this,false);
	StringSetting customMsg = new StringSetting("CustomMsg",this,"Internal server error", v -> larpMsg.isOn());

	//waits x amount of ticks on relog before kicking again
	int timer = 0;
	static boolean allowAutoReconnect = true;

	@Override
	public void onEnable() {
		super.onEnable();
		if(mc.isSingleplayer())
			Spark.sendInfo("Auto log will not kick in singleplayer");
	}

	@SubscribeEvent
	public void onUpdate(PlayerUpdateEvent event) {
		if(mc.isSingleplayer())
			return;
		if(timer < logTimer.getValue())
		{
			timer++;
			return;
		}
		allowAutoReconnect = true;

		if(useHealth.isOn())
			if (mc.player.getHealth() <= logHealth.getValue()) {
				Log("Health was: " + ((int)Math.ceil(mc.player.getHealth())));
			}
		if(useTotems.isOn())
			if (Spark.dataTrackingManager.getAmountOfItem(Items.TOTEM_OF_UNDYING) <= logTotems.getValue()) {
				Log("Totem count was: " + (Spark.dataTrackingManager.getAmountOfItem(Items.TOTEM_OF_UNDYING)));
			}

	}

	void Log(String info) {
		if(larpMsg.isOn())
			info = customMsg.getValue();

		FMLClientHandler.instance().getClientToServerNetworkManager().closeChannel(new TextComponentString(info));
		timer = 0;
		allowAutoReconnect = false;
	}
}
