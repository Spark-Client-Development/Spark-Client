package me.wallhacks.spark.manager;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.player.RaytraceUtil;
import me.wallhacks.spark.util.player.itemswitcher.ItemSwitcher;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.ItemForMineSwitchItem;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BreakManager implements MC {
    public BreakManager() {
        Spark.eventBus.register(this);
    }

    public BlockPos block = null;
    int ticks = 0;
    boolean instMine;


    public void setCurrentBlock(BlockPos pos,boolean instMine,int keepTicks) {




        block = pos;
        this.instMine = instMine;
        ticks = keepTicks;





    }

    @SubscribeEvent
    void OnUpdate(PlayerUpdateEvent event) {

        if (block == null)
            return;


        IBlockState state = mc.world.getBlockState(block);


        if(ticks <= 0 || isDone(state))
        {
            mc.playerController.resetBlockRemoving();
            block = null;
            return;
        }
        ticks--;

        ItemSwitcher.Switch(new ItemForMineSwitchItem(mc.world.getBlockState(block)), ItemSwitcher.switchType.Mainhand);


        Vec3d pos = PlayerUtil.getClosestPoint(RaytraceUtil.getPointToLookAtBlock(block));
        EnumFacing facing = EnumFacing.UP;
        if(pos == null)
            pos = new Vec3d(block.getX()+0.5,block.getY()+0.5,block.getZ()+0.5);
        else
            facing = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ),pos,false).sideHit;

        if(AntiCheatConfig.getInstance().getBlockRotate())
        {

            if(!Spark.rotationManager.rotate(Spark.rotationManager.getLegitRotations(pos), AntiCheatConfig.getInstance().getBlockRotStep(), 6, false, true))
                return;


        }



        if(mc.playerController.onPlayerDamageBlock(block, facing))
        {
            mc.effectRenderer.addBlockHitEffects(block, facing);

            mc.player.swingArm(EnumHand.MAIN_HAND);
        }






    }

    private boolean isDone(IBlockState state) {
        return state.getBlock() == Blocks.BEDROCK || state.getBlock() == Blocks.AIR || state.getBlock() instanceof BlockLiquid;
    }


    public boolean doInstaMine() {
        return block != null && instMine;
    }
}
