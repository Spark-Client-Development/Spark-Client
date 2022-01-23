package me.wallhacks.spark.systems.module.modules.combat;

import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;

@Module.Registration(name = "BowSpam", description = "Makes you shoot arrows quicker")
public class BowSpam extends Module implements MC {

	BooleanSetting far = new BooleanSetting("Far", this, false);
	
	@SubscribeEvent
	public void onUpdate(PlayerUpdateEvent e) {
		if(mc.player.getActiveItemStack().getItem() == Items.BOW) {
			if(mc.player.getItemInUseMaxCount() > 2) {
				if(far.getValue()) {
					mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
					mc.getConnection().sendPacket(new CPacketPlayer.PositionRotation(posX(), posY(), posZ(), rotationYaw(), rotationPitch(), true));
					mc.getConnection().sendPacket(new CPacketPlayer.PositionRotation(posX(), posY() + 1e-10, posZ(), rotationYaw(), rotationPitch(), false));
				}
				mc.getConnection().sendPacket(new CPacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
				mc.player.stopActiveHand();
				mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
			}
		}
	}
}
