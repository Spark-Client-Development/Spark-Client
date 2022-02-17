package me.wallhacks.spark.systems.module.modules.misc;


import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import net.minecraft.entity.player.EntityPlayer;

@Module.Registration(name = "AutoGG", description = "")
public class AutoGG extends Module implements MC {

	public static AutoGG instance;
	public AutoGG(){
		instance = this;
	}


	public void onKilledPlayer(EntityPlayer player, int pops) {
		mc.player.sendChatMessage("GG! "+player.getName());
	}


}
