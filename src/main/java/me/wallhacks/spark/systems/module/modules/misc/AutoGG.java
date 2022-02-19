package me.wallhacks.spark.systems.module.modules.misc;


import ibxm.Player;
import me.wallhacks.spark.manager.CombatManager;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import net.minecraft.entity.player.EntityPlayer;

@Module.Registration(name = "AutoGG", description = "")
public class AutoGG extends Module implements MC {

	public static AutoGG instance;
	public AutoGG(){
		instance = this;
	}


	public void onKilledPlayer(CombatManager.Kill kill, EntityPlayer entityPlayer) {
		mc.player.sendChatMessage("GG! "+entityPlayer.getName()+(kill.getKilledWith() != null ? " Thanks to Spark " + kill.getKilledWith().getName() : ""));
	}


}
