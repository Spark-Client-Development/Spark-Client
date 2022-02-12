package me.wallhacks.spark.systems.module.modules.combat;

import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.MathUtil;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.IntSetting;

@Module.Registration(name = "CityGod", description = "Clips you into the corner of blocks so you take less crystal damage.")
public class CityGod extends Module implements MC {

	IntSetting pause = new IntSetting("PauseTime", this, 4, 1, 10);
	
	@SubscribeEvent
	public void onTick(LivingUpdateEvent e) {
		if(nullCheck())
			return;

		if(mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().grow(0.01, 0, 0.01)).size() < 2) {
				mc.player.setPosition(
						MathUtil.roundToClosest(posX(), Math.floor(posX()) + 0.301, Math.floor(posX()) + 0.699),
						posY(), 
						MathUtil.roundToClosest(posZ(), Math.floor(posZ()) + 0.301, Math.floor(posZ()) + 0.699));
		} else {
			if(mc.player.ticksExisted % pause.getValue() == 0) {
				mc.player.setPosition(
						posX() + MathHelper.clamp(MathUtil.roundToClosest(posX(), Math.floor(posX()) + 0.241, Math.floor(posX()) + 0.759) - posX(), -0.03, 0.03),
						posY(), 
						posZ() + MathHelper.clamp(MathUtil.roundToClosest(posZ(), Math.floor(posZ()) + 0.241, Math.floor(posZ()) + 0.759) - posZ(), -0.03, 0.03));
				mc.player.connection.sendPacket(new CPacketPlayer.Position(posX(), posY(), posZ(), true));
				mc.player.connection.sendPacket(new CPacketPlayer.Position(MathUtil.roundToClosest(posX(), Math.floor(posX()) + 0.23, Math.floor(posX()) + 0.77), posY(), MathUtil.roundToClosest(posZ(), Math.floor(posZ()) + 0.23, Math.floor(posZ()) + 0.77), true));
			}
		}
	}
}
