package me.wallhacks.spark.manager;

import baritone.api.utils.BlockUtils;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;
import me.wallhacks.spark.systems.module.modules.exploit.PacketMine;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.WorldUtils;
import me.wallhacks.spark.util.player.BlockInteractUtil;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.player.RaytraceUtil;
import me.wallhacks.spark.util.player.itemswitcher.ItemSwitcher;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.ItemForMineSwitchItem;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
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
    boolean constSwitch;

    public boolean setCurrentBlock(BlockPos pos,boolean instMin,int keepTicks) {
        return setCurrentBlock(pos,instMine,false,keepTicks);
    }
    public boolean setCurrentBlock(BlockPos pos,boolean instMine,boolean constSwitch,int keepTicks) {



        this.constSwitch = constSwitch;
        block = pos;
        this.instMine = instMine;
        ticks = keepTicks;


        if(instMine || PacketMine.instance.isEnabled())
        {
            if(pos.equals(PacketMine.instance.pos))
            {
                if(PacketMine.instance.ticksFromDone() < 3)
                {

                    Spark.switchManager.Switch(new ItemForMineSwitchItem(mc.world.getBlockState(block)), ItemSwitcher.usedHand.Mainhand, constSwitch ? ItemSwitcher.switchType.Const : ItemSwitcher.switchType.Normal);
                    return PacketMine.instance.tryMine();
                }

            }
        }



        return false;

    }
    @SubscribeEvent
    public void onPacketGet(PacketReceiveEvent event) {
        if (event.getPacket() instanceof SPacketMultiBlockChange) {
            for (SPacketMultiBlockChange.BlockUpdateData pos : ((SPacketMultiBlockChange) event.getPacket()).getChangedBlocks()) {

                blockChanged(pos.getPos(),pos.getBlockState());
            }
        } else if (event.getPacket() instanceof SPacketBlockChange) {
            blockChanged(((SPacketBlockChange) event.getPacket()).getBlockPosition(),((SPacketBlockChange) event.getPacket()).blockState);
        }
    }
    protected void blockChanged(BlockPos p,IBlockState state) {
        if (block == null)
            return;
        if(block.equals(p) && isDone(state))
        {
            mc.playerController.resetBlockRemoving();
            block = null;
            return;
        }
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




        Vec3d pos = PlayerUtil.getClosestPoint(RaytraceUtil.getPointToLookAtBlock(block));
        EnumFacing facing = EnumFacing.UP;

        if(pos == null)
            pos = new Vec3d(block.getX()+0.5,block.getY()+0.5,block.getZ()+0.5);
        else
            facing = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ),pos,false).sideHit;



        if(!(instMine || PacketMine.instance.isEnabled()) || PacketMine.instance.ticksFromDone() < 3)
        {

            if(AntiCheatConfig.getInstance().getBlockRotate())
            {
                if(!Spark.rotationManager.rotate(Spark.rotationManager.getLegitRotations(pos), AntiCheatConfig.getInstance().getBlockRotStep(), 6, false, true))
                    return;
            }
            try {
                Spark.switchManager.Switch(new ItemForMineSwitchItem(mc.world.getBlockState(block)), ItemSwitcher.usedHand.Mainhand, constSwitch ? ItemSwitcher.switchType.Const : ItemSwitcher.switchType.Normal);
            } catch (NullPointerException problem) {
                //we ignore problems
            }
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

    public boolean canBreak(BlockPos p) {
        if(!WorldUtils.canBreak(p))
            return false;
        Vec3d vec = PlayerUtil.getClosestPoint(RaytraceUtil.getPointToLookAtBlock(p));
        return PlayerUtil.getDistance(p) < (vec == null ? AntiCheatConfig.getInstance().getBlockPlaceWallRange() : AntiCheatConfig.getInstance().getBlockPlaceRange());
    }
}
