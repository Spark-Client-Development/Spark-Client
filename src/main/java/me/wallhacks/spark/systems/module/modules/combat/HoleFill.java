package me.wallhacks.spark.systems.module.modules.combat;

import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.MathUtil;
import me.wallhacks.spark.util.WorldUtils;
import me.wallhacks.spark.util.combat.AttackUtil;
import me.wallhacks.spark.util.combat.PredictionUtil;
import me.wallhacks.spark.util.objects.FadePos;
import me.wallhacks.spark.util.player.BlockInteractUtil;
import me.wallhacks.spark.util.player.PlayerUtil;
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
import java.util.List;

@Module.Registration(name = "HoleFill", description = "Steals from chests")
public class HoleFill extends Module {

    BooleanSetting smart = new BooleanSetting("Smart", this, true, "General");
    IntSetting blocksPerTick = new IntSetting("BlocksPerTick",this,1,1,8,"General");
    BooleanSetting Silent = new BooleanSetting("SilentSwitch",this,false,"General");

    BooleanSetting FillDoubles = new BooleanSetting("FillDoubles",this,true,"General");


    BooleanSetting render = new BooleanSetting("Render", this, true, "Render");
    ColorSetting fill = new ColorSetting("Fill", this, new Color(0x389F5EDC, true), "Render");
    ColorSetting outline = new ColorSetting("Outline", this, new Color(0x919F0AF6, true), "Render");
    /*
    @Todo: add support for double holes
    */

    @SubscribeEvent
    void OnUpdate(PlayerUpdateEvent event) {

        BlockPos pos = PlayerUtil.GetPlayerPosHighFloored(MC.mc.player);

        if (MC.mc.playerController.getIsHittingBlock())
            return;
        if (MC.mc.player.isHandActive() && MC.mc.player.getHeldItemMainhand().getItem() instanceof ItemFood)
            return;

        ArrayList<EntityPlayer> EnemyList = new ArrayList<EntityPlayer>();
        for(Object o : MC.mc.world.loadedEntityList.toArray()){
            if(o instanceof EntityPlayer){
                {
                    EntityPlayer e = (EntityPlayer)o;
                    if(AttackUtil.CanAttackPlayer(e,15)) {
                        EnemyList.add(e);
                    }
                }

            }
        }

        BlockPos floored = PlayerUtil.getPlayerPosFloored(MC.mc.player);
        List<BlockPos> poses = WorldUtils.getSphere(floored, 5, 3, 1);
        int placed = 0;
        loopBlocks:
        for(BlockPos posToCheck : poses){
            if (MC.mc.world.getBlockState(posToCheck).getBlock().material.isReplaceable())
            {
                //can player get in
                if(!MC.mc.world.getBlockState(posToCheck.add(0,1,0)).getBlock().material.isReplaceable()) continue;
                if(!MC.mc.world.getBlockState(posToCheck.add(0,2,0)).getBlock().material.isReplaceable()) continue;

                //is hole
                if(!MC.mc.world.getBlockState(posToCheck.add(0,-1,0)).getBlock().material.isSolid()) continue;

                Vec3i[] walls = new Vec3i[]{
                        new Vec3i(1, 0, 0),
                        new Vec3i(-1, 0, 0),
                        new Vec3i(0, 0, 1),
                        new Vec3i(0, 0, -1)
                };

                BlockPos doubleHolePos = null;
                for (Vec3i vec : walls) {
                    BlockPos bp = posToCheck.add(vec);
                    Block x = MC.mc.world.getBlockState(bp).getBlock();
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
                        Block wx = MC.mc.world.getBlockState(wall).getBlock();
                        if (wx != Blocks.OBSIDIAN && wx != Blocks.BEDROCK)
                            continue loopBlocks;


                    }
                }



                smartChecks:
                if(smart.isOn()) {
                    if(isPlayerTryingToGetInHole(MC.mc.player,posToCheck))
                        continue loopBlocks;
                    for(EntityPlayer e : EnemyList)
                        if(isPlayerTryingToGetInHole(e,posToCheck)) break smartChecks;
                    continue loopBlocks;
                }





                BlockInteractUtil.BlockPlaceResult res = Place(posToCheck);
                if(res == BlockInteractUtil.BlockPlaceResult.PLACED) {
                    placed++;
                    if (render.getValue())
                        new FadePos(posToCheck, outline, fill, true);
                }
                else if(res == BlockInteractUtil.BlockPlaceResult.WAIT)
                    return;

                if(placed >= blocksPerTick.getValue())
                    return;
            }
        }
    }



    BlockInteractUtil.BlockPlaceResult Place(BlockPos x ){

        int lastItem = MC.mc.player.inventory.currentItem;


        BlockInteractUtil.BlockPlaceResult res = (BlockInteractUtil.tryPlaceBlock(x,new HardSolidBlockSwitchItem(),true,true,4));


        if(Silent.isOn())
            MC.mc.player.inventory.currentItem = lastItem;

        return res;


    }



    public boolean isPlayerTryingToGetInHole(EntityPlayer player,BlockPos hole){
        //if player is already in hole no point in filling it
        if(PlayerUtil.getPlayerPosFloored(player).equals(hole))
            return false;


        for (AxisAlignedBB bb : PredictionUtil.PredictedTargetBoxes(player,5)) {
            if(isBBCloseToHole(bb,hole))
                return true;
        }


        return false;

    }
    boolean isBBCloseToHole (AxisAlignedBB box,BlockPos hole){
        double posX = (box.minX + box.maxX) / 2.0D;
        double posY = box.minY;
        double posZ = (box.minZ + box.maxZ) / 2.0D;

        return MathUtil.getDistanceFromTo(
                new Vec3d(hole.getX()+0.5,hole.getY()+0.5,hole.getZ()+0.5),
                new Vec3d(posX,posY,posZ)) < 2;
    }

}
