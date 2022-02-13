package me.wallhacks.spark.systems.module.modules.movement;

import me.wallhacks.spark.event.block.LiquidCollisionBBEvent;
import me.wallhacks.spark.event.entity.LiquidPushEvent;
import me.wallhacks.spark.event.player.PacketSendEvent;
import me.wallhacks.spark.event.player.PlayerLivingTickEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.player.PlayerUtil;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "Jesus", description = "be like jeses and walk over water")
public class Jesus extends Module {


    @SubscribeEvent
    public void onUpdate(LiquidCollisionBBEvent event){

        if(solidWater)
        if(PlayerUtil.getDistance(event.getBlockPos()) < 6)
        {
            IBlockState state1 = mc.world.getBlockState(event.getBlockPos());

            if(state1.getBlock() instanceof BlockLiquid)
            {
                //thanks geza for epic water height thing - searched for this for ages. Have it now :D ez skid
                double h = 1.125 - (double) (state1.getValue(BlockLiquid.LEVEL) + 1) / 8d;
                event.setBoundingBox(new AxisAlignedBB(0, 0, 0, 1, MathHelper.clamp(h + 0.125, 0.125, 1), 1));
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



            if(PlayerUtil.isOnLiquid())
            {


                solidWater = true;

                BlockPos p = new BlockPos(mc.player.posX,mc.player.posY,mc.player.posZ);
                IBlockState state = mc.world.getBlockState(p);
                if(state.getBlock() instanceof BlockLiquid)
                {
                    BlockPos p1 = new BlockPos(mc.player.posX,mc.player.posY+1,mc.player.posZ);
                    if(mc.world.getBlockState(p1).getBlock() instanceof BlockLiquid)
                    {
                        p = p1;
                        state = mc.world.getBlockState(p1);
                    }
                    double h = 1.125 - (double) (state.getValue(BlockLiquid.LEVEL) + 1) / 8d;

                    if(p.getY() + h + 0.125 > mc.player.posY)
                        solidWater = false;
                }


                if(mc.gameSettings.keyBindSneak.isKeyDown())
                {
                    solidWater = false;
                }
                else if(!solidWater)
                {
                    if(!mc.gameSettings.keyBindJump.isKeyDown() || mc.world.getBlockState(PlayerUtil.getPlayerPosFloored(mc.player,0.5)).getBlock() == Blocks.AIR)
                        mc.player.setVelocity(mc.player.motionX, 0.11, mc.player.motionZ);

                }





            }

        }


    }
    boolean solidWater = false;

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
