package me.wallhacks.spark.systems.module.modules.combat;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PacketSendEvent;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.module.modules.exploit.PacketMine;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.util.WorldUtils;
import me.wallhacks.spark.util.combat.AttackUtil;
import me.wallhacks.spark.util.combat.CrystalUtil;
import me.wallhacks.spark.util.objects.FadePos;
import me.wallhacks.spark.util.player.BlockInteractUtil;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.player.itemswitcher.ItemSwitcher;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.SpecBlockSwitchItem;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.SpecItemSwitchItem;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;

@Module.Registration(name = "AutoCity", description = "Steals from chests")
public class AutoCity extends Module {

    public static AutoCity INSTANCE;

    public AutoCity() {
        INSTANCE = this;
    }


    BlockPos target;
    BlockPos crystalTarget;


    BooleanSetting insta = new BooleanSetting("Insta", this, true);

    BooleanSetting render = new BooleanSetting("Render", this, true, "Render");
    ColorSetting fill = new ColorSetting("Color", this, new Color(0xABE50F36, true), "Render");



    @SubscribeEvent
    void OnUpdate(PlayerUpdateEvent event) {

        GetBreakeBlock();

        if (target == null) {
            disable();
            return;
        }

        Spark.breakManager.setCurrentBlock(target, insta.isOn(), 2);

    }

    @SubscribeEvent
    void OnUpdate(PacketSendEvent.Post event) {

        if(event.getPacket() instanceof CPacketPlayerDigging)
        {
            CPacketPlayerDigging digging = event.getPacket();
            if(digging.getAction() == CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK)
                if(digging.getPosition().equals(target))
                    placeCrystalOnBlock(crystalTarget);
        }
    }


    public boolean placeCrystalOnBlock(BlockPos bestPos){

        Vec3d pos = CrystalUtil.getRotationPos(false,bestPos,null);
        final RayTraceResult result = mc.world.rayTraceBlocks(PlayerUtil.getEyePos(), pos, false, true, false);
        EnumFacing facing = (result == null || !bestPos.equals(result.getBlockPos()) || result.sideHit == null) ? EnumFacing.UP : result.sideHit;

        Vec3d v = new Vec3d(bestPos).add(0.5, 0.5, 0.5).add(new Vec3d(facing.getDirectionVec()).scale(0.5));

        if (result != null && bestPos.equals(result.getBlockPos()) && result.hitVec != null)
            v = result.hitVec;
        if (bestPos.getY() >= 254)
            facing = EnumFacing.EAST;

        //offhand
        EnumHand hand = ItemSwitcher.Switch(new SpecItemSwitchItem(Items.END_CRYSTAL), ItemSwitcher.switchType.Both);
        if (hand == null)
            return false;


        //rotate if needed
        if (!Spark.rotationManager.rotate(Spark.rotationManager.getLegitRotations(pos), AntiCheatConfig.getInstance().getCrystalRotStep(), 4, false, true))
            return false;


        //send packet
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(bestPos, facing, hand, (float) v.x, (float) v.y, (float) v.z));

        //swing
        switch (AntiCheatConfig.getInstance().crystalPlaceSwing.getValue()) {
            case "Normal":
                mc.player.swingArm(hand);
                break;
            case "Packet":
                mc.player.connection.sendPacket(new CPacketAnimation(hand));
                break;
        }

        if (render.getValue())
            new FadePos(bestPos, fill, true);

        return true;
    }



    void GetBreakeBlock(){
        BlockPos bestPos = null;
        BlockPos bestPosForCrystal = null;
        float biggestDis = Float.MAX_VALUE;

        entityLoop:
        for(Entity entity : mc.world.loadedEntityList){

            if(entity instanceof EntityPlayer){
                EntityPlayer e = (EntityPlayer)entity;
                // && e.onGround
                if(AttackUtil.canAttackPlayer(e,10)) {

                    BlockPos floored = PlayerUtil.getPlayerPosFloored(e);

                    ArrayList<BlockPos> surround = new ArrayList<BlockPos>();
                    surround.add(floored.add(0,0,1));
                    surround.add(floored.add(0,0,-1));
                    surround.add(floored.add(1,0,0));
                    surround.add(floored.add(-1,0,0));

                    for(BlockPos p : surround){
                        if (!mc.world.getBlockState(p).getBlock().material.isSolid())
                            continue entityLoop;
                    }

                    for(BlockPos p : surround) {

                        final Block b = mc.world.getBlockState(p).getBlock();

                        if (b.material.isSolid() && Spark.breakManager.canBreak(p)) {

                            final Block top = mc.world.getBlockState(p.add(0, 1, 0)).getBlock();
                            final Block bottom = mc.world.getBlockState(p.add(0, -1, 0)).getBlock();
                            final Block away = mc.world.getBlockState(p.add(p.getX() - floored.getX(), 0, p.getZ() - floored.getZ())).getBlock();
                            final Block awayTop = mc.world.getBlockState(p.add(p.getX() - floored.getX(), 1, p.getZ() - floored.getZ())).getBlock();
                            final Block awayBottom = mc.world.getBlockState(p.add(p.getX() - floored.getX(), -1, p.getZ() - floored.getZ())).getBlock();




                            boolean awayPlaceAble = (away == Blocks.AIR && awayTop == Blocks.AIR && (awayBottom == Blocks.BEDROCK || awayBottom == Blocks.OBSIDIAN));

                            if (awayPlaceAble) {

                                if(target != null)
                                {
                                    if(p.equals(target))
                                    {
                                        bestPos = p;
                                        bestPosForCrystal = p.add(p.getX() - floored.getX(), -1, p.getZ() - floored.getZ());
                                        break entityLoop;
                                    }
                                    else
                                        continue entityLoop;
                                }

                                float dis = PlayerUtil.getDistance(p);

                                if (dis < biggestDis) {
                                    biggestDis = dis;
                                    bestPos = p;
                                    bestPosForCrystal = p.add(p.getX() - floored.getX(), -1, p.getZ() - floored.getZ());
                                }

                            }


                        }
                    }
                }
            }

        }
        target = bestPos;
        crystalTarget = bestPosForCrystal;

    }


}
