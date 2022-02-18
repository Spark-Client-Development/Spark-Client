package me.wallhacks.spark.systems.module.modules.combat;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PacketSendEvent;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.module.modules.exploit.AntiCity;
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
import me.wallhacks.spark.util.player.RaytraceUtil;
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
import net.minecraft.util.math.*;
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




    @SubscribeEvent
    void OnUpdate(PlayerUpdateEvent event) {

        BlockPos pos = GetBreakeBlock();

        if(pos == null || (target != null && !target.equals(pos)))
        {

            if(target == null)
                Spark.sendInfo("Autocity has no target!");
            else
                Spark.sendInfo("Autocity has lost target!");
            target = null;

            disable();
            return;
        }
        target = pos;

        Spark.breakManager.setCurrentBlock(target, false, 2);


    }

    public BlockPos getTarget() {
        return isEnabled() ? target : null;
    }





    BlockPos GetBreakeBlock(){

        BlockPos bestPos = null;
        float floatBest = 0;

        loop:
        for(Entity entity : mc.world.loadedEntityList){
            if(entity instanceof EntityPlayer){
                {
                    EntityPlayer e = (EntityPlayer)entity;
                    if(AttackUtil.canAttackPlayer(e,10)) {

                        BlockPos pos = PlayerUtil.getPlayerPosFloored(e);


                        ArrayList<BlockPos> surround = new ArrayList<BlockPos>();
                        surround.add(pos.add(0,0,1));
                        surround.add(pos.add(0,0,-1));
                        surround.add(pos.add(1,0,0));
                        surround.add(pos.add(-1,0,0));


                        //check is player is in burrow
                        final Block feet = mc.world.getBlockState(pos).getBlock();
                        if(feet.material.isOpaque() && Spark.breakManager.canBreak(pos))
                        {
                            if(pos.equals(target))
                                return pos;
                            //burrow case
                            float value = 10 - PlayerUtil.getDistance(pos);
                            if(value > floatBest){
                                floatBest = value;
                                bestPos = pos;
                            }

                            continue loop;
                        }

                        //if player is exposed no need to city
                        for(BlockPos p : surround){
                            if (mc.world.getBlockState(p).getBlock() == Blocks.AIR)
                                continue loop;
                        }

                        for(BlockPos p : surround){

                            final Block pb = mc.world.getBlockState(p).getBlock();

                            if (pb != Blocks.AIR && pb != Blocks.BEDROCK && Spark.breakManager.canBreak(p))
                            {

                                final Block top = mc.world.getBlockState(p.add(0,1,0)).getBlock();
                                final Block bottom = mc.world.getBlockState(p.add(0,-1,0)).getBlock();
                                final Block away = mc.world.getBlockState(p.add(p.getX()-pos.getX(),0,p.getZ()-pos.getZ())).getBlock();
                                final Block awaytop = mc.world.getBlockState(p.add(p.getX()-pos.getX(),1,p.getZ()-pos.getZ())).getBlock();
                                final Block awaybottom = mc.world.getBlockState(p.add(p.getX()-pos.getX(),-1,p.getZ()-pos.getZ())).getBlock();


                                float value = 0;



                                boolean placeAble = (top == Blocks.AIR && (bottom == Blocks.BEDROCK || bottom == Blocks.OBSIDIAN));
                                boolean awayPlaceAble = (away == Blocks.AIR && awaytop == Blocks.AIR && (awaybottom == Blocks.BEDROCK || awaybottom == Blocks.OBSIDIAN));

                                if(placeAble){
                                    value += 10;
                                    if(awayPlaceAble)
                                        value += 10;
                                }
                                else if(awayPlaceAble)
                                    value += 25;



                                value -= PlayerUtil.getDistance(p);

                                if(p.equals(target))
                                    return p;

                                if(value > floatBest){
                                    floatBest = value;
                                    bestPos = p;
                                }


                            }

                        }
                    }
                }
            }
        }

        return bestPos;

    }



    boolean CanPlaceOnBlock(BlockPos p,boolean entityCheck) {

        final Block block = mc.world.getBlockState(p).getBlock();
        if (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) {
            final Block floor = mc.world.getBlockState(p.add(0, 1, 0)).getBlock();
            final Block ceil = mc.world.getBlockState(p.add(0, 2, 0)).getBlock();

            //in the end crystal have a fire block in them

            if ((floor == Blocks.AIR || (floor == Blocks.FIRE && mc.player.dimension == 1)) && ceil == Blocks.AIR) {
                double d0 = (double) p.getX();
                double d1 = (double) p.getY() + 1;
                double d2 = (double) p.getZ();
                double d0b = d0 + 1;
                double d1b = d1 + 2;
                double d2b = d2 + 1;

                AxisAlignedBB bb = new AxisAlignedBB(d0, d1, d2, d0b, d1b, d2b);

                if(entityCheck)
                    for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, bb)) {
                        if (entity.isDead) continue;

                        return false;


                    }


                Vec3d pos = PlayerUtil.getClosestPoint(RaytraceUtil.getPointToLookAtBlock(p));

                if (PlayerUtil.getDistance(p) > (pos != null ? AntiCheatConfig.getInstance().getCrystalPlaceRange() : AntiCheatConfig.getInstance().getCrystalWallRange()))
                    return false;

                return true;

            }
        }

        return false;


    }


}
