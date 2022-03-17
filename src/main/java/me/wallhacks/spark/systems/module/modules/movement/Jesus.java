package me.wallhacks.spark.systems.module.modules.movement;

import me.wallhacks.spark.event.block.LiquidCollisionBBEvent;
import me.wallhacks.spark.event.entity.LiquidPushEvent;
import me.wallhacks.spark.event.player.PacketSendEvent;
import me.wallhacks.spark.event.player.PlayerLivingTickEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.util.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "Jesus", description = "be like jeses and walk over water")
public class Jesus extends Module {

	DoubleSetting upspeed = new DoubleSetting("UpSpeed", this, 1, 0, 5);
	DoubleSetting downspeed = new DoubleSetting("DownSpeed", this, 1, 0, 5);
	
    boolean solidWater = false;
	
    @SubscribeEvent
    public void onUpdate(LiquidCollisionBBEvent event){
        if(solidWater && PlayerUtil.getDistance(event.getBlockPos()) < 6)
        {
            IBlockState state1 = mc.world.getBlockState(event.getBlockPos());

            if(state1.getBlock() instanceof BlockLiquid) {
                event.setBoundingBox(Block.FULL_BLOCK_AABB);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onLiquidPush(LiquidPushEvent event) {
        if (event.getEntity() == mc.player) event.setCanceled(true);
    }
    
    @SubscribeEvent
    public void onUpdate(PlayerLivingTickEvent event) {
        solidWater = false;
        if(!mc.player.isElytraFlying() && mc.player.fallDistance < 2f && !(mc.player.getRidingEntity() instanceof EntityBoat)) {
        	if(mc.gameSettings.keyBindSneak.isKeyDown() && (PlayerUtil.isInLiquid() || PlayerUtil.isOnLiquid())) {
        		mc.player.motionY = -downspeed.getValue()/10d;
        	} else {
	        	if(!PlayerUtil.isInLiquid()) {
		            if(PlayerUtil.isOnLiquid())
		            {
		                solidWater = true;
		            }
	        	} else {
	        		mc.player.motionY = upspeed.getValue()/10d;
	        	}
        	}
        }


    }


    @SubscribeEvent
    public void onPacket(PacketSendEvent event) {

        if (event.getPacket() instanceof CPacketPlayer)
        {

            if (solidWater)
            {
                final CPacketPlayer packet = (CPacketPlayer) event.getPacket();

                if (mc.player.ticksExisted % 3 == 0)
                {
                    packet.y -= 0.15f;
                }
            }
        }
    }

}
