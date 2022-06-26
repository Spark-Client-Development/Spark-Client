package me.wallhacks.spark.systems.module.modules.combat;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.SettingGroup;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.MathUtil;
import me.wallhacks.spark.util.WorldUtils;
import me.wallhacks.spark.util.combat.AttackUtil;
import me.wallhacks.spark.util.combat.PredictionUtil;
import me.wallhacks.spark.util.objects.FadePos;
import me.wallhacks.spark.util.player.BlockInteractUtil;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.player.itemswitcher.ItemSwitcher;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.HardSolidBlockSwitchItem;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemFood;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Module.Registration(name = "HoleFill", description = "Steals from chests")
public class HoleFill extends Module {

    BooleanSetting smart = new BooleanSetting("Smart", this, true);
    IntSetting blocksPerTick = new IntSetting("BlocksPerTick",this,1,1,8);
    ModeSetting switchingMode = new ModeSetting("Switch", this, "Silent",  Arrays.asList("Normal","Silent","Const"));

    BooleanSetting FillDoubles = new BooleanSetting("FillDoubles",this,true);


    SettingGroup renderG = new SettingGroup("Render", this);
    BooleanSetting render = new BooleanSetting("Render", renderG, true);
    ColorSetting fill = new ColorSetting("Fill", renderG, new Color(0x389F5EDC, true));
    /*
    @Todo: add support for double holes
    */

    @SubscribeEvent
    void OnUpdate(PlayerUpdateEvent event) {

        BlockPos pos = PlayerUtil.GetPlayerPosHighFloored(mc.player);

        if (mc.playerController.getIsHittingBlock())
            return;
        if (mc.player.isHandActive() && mc.player.getHeldItemMainhand().getItem() instanceof ItemFood)
            return;

        ArrayList<EntityPlayer> EnemyList = new ArrayList<EntityPlayer>();
        for(Object o : mc.world.loadedEntityList.toArray()){
            if(o instanceof EntityPlayer){
                {
                    EntityPlayer e = (EntityPlayer)o;
                    if(AttackUtil.canAttackPlayer(e,15)) {
                        EnemyList.add(e);
                    }
                }

            }
        }

        BlockPos floored = PlayerUtil.getPlayerPosFloored(mc.player);
        List<BlockPos> poses = WorldUtils.getSphere(floored, 5, 3, 1);
        int placed = 0;
        loopBlocks:
        for(BlockPos posToCheck : poses){
            if (mc.world.getBlockState(posToCheck).getBlock().material.isReplaceable())
            {
                //can player get in
                if(!mc.world.getBlockState(posToCheck.add(0,1,0)).getBlock().material.isReplaceable()) continue;
                if(!mc.world.getBlockState(posToCheck.add(0,2,0)).getBlock().material.isReplaceable()) continue;

                //is hole
                if(!mc.world.getBlockState(posToCheck.add(0,-1,0)).getBlock().material.isSolid()) continue;

                Vec3i[] walls = new Vec3i[]{
                        new Vec3i(1, 0, 0),
                        new Vec3i(-1, 0, 0),
                        new Vec3i(0, 0, 1),
                        new Vec3i(0, 0, -1)
                };

                BlockPos doubleHolePos = null;
                for (Vec3i vec : walls) {
                    BlockPos bp = posToCheck.add(vec);
                    Block x = mc.world.getBlockState(bp).getBlock();
                    if (x != Blocks.OBSIDIAN && x != Blocks.BEDROCK) {
                        if(doubleHolePos == null && FillDoubles.isOn()) {

                            doubleHolePos = bp;
                        }
                        else
                            continue loopBlocks;
                    }
                }

                if(doubleHolePos != null)
                for (Vec3i vec1 : walls) {
                    BlockPos wall = doubleHolePos.add(vec1);
                    if(!wall.equals(posToCheck)) {
                        Block wx = mc.world.getBlockState(wall).getBlock();
                        if (wx != Blocks.OBSIDIAN && wx != Blocks.BEDROCK)
                            continue loopBlocks;


                    }
                }



                smartChecks:
                if(smart.isOn()) {
                    if(PredictionUtil.isPlayerTryingToGetInHole(mc.player,posToCheck))
                        continue loopBlocks;
                    for(EntityPlayer e : EnemyList)
                        if(PredictionUtil.isPlayerTryingToGetInHole(e,posToCheck)) break smartChecks;
                    continue loopBlocks;
                }





                BlockInteractUtil.BlockPlaceResult res = Place(posToCheck);
                if(res == BlockInteractUtil.BlockPlaceResult.PLACED) {
                    placed++;
                    if (render.getValue())
                        new FadePos(posToCheck, fill, true);
                }
                else if(res == BlockInteractUtil.BlockPlaceResult.WAIT)
                    return;

                if(placed >= blocksPerTick.getValue())
                    return;
            }
        }
    }



    BlockInteractUtil.BlockPlaceResult Place(BlockPos x ){


        BlockInteractUtil.BlockPlaceResult res = (BlockInteractUtil.tryPlaceBlock(x,new HardSolidBlockSwitchItem(), Spark.switchManager.getModeFromString(switchingMode.getValue()), true));


        return res;


    }






}
