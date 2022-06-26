package me.wallhacks.spark.systems.module.modules.combat;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.module.modules.world.GriefHelper;
import me.wallhacks.spark.systems.module.modules.world.LogoutSpots;
import me.wallhacks.spark.systems.setting.SettingGroup;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.WorldUtils;
import me.wallhacks.spark.util.combat.AttackUtil;
import me.wallhacks.spark.util.combat.PredictionUtil;
import me.wallhacks.spark.util.objects.FadePos;
import me.wallhacks.spark.util.player.BlockInteractUtil;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.player.itemswitcher.ItemSwitcher;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.HardSolidBlockSwitchItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;

import java.awt.*;
import java.util.*;
import java.util.List;

@Module.Registration(name = "AutoTrap", description = "Traps players in boxes")
public class AutoTrap extends Module {


    IntSetting prediction = new IntSetting("Prediction",this,2,1,8);
    IntSetting blocksPerTick = new IntSetting("BlocksPerTick",this,4,1,8);
    BooleanSetting logoutSpots = new BooleanSetting("LogoutSpots", this, false);

    ModeSetting switchingMode = new ModeSetting("Switch", this, "Normal",  Arrays.asList("Normal","Silent","Const"));

    SettingGroup renderG = new SettingGroup("Render", this);
    BooleanSetting render = new BooleanSetting("Render", renderG, true);
    ColorSetting fill = new ColorSetting("Color", renderG, new Color(0xABE50F36, true));


    @SubscribeEvent
    void OnUpdate(PlayerUpdateEvent event) {
        ArrayList<EntityPlayer> EnemyList = new ArrayList<EntityPlayer>();
        for(Object o : mc.world.loadedEntityList.toArray()){
            if(o instanceof EntityPlayer){
                {
                    EntityPlayer e = (EntityPlayer)o;
                    if(AttackUtil.canAttackPlayer(e,15) && e != mc.player) {
                        EnemyList.add(e);
                    }
                }

            }


        }




        for(EntityPlayer e : EnemyList) {


            if(trap(PredictionUtil.PredictedTarget(e,prediction.getValue())))
                return;


        }

        if(logoutSpots.isOn())
            for(LogoutSpots.LogoutSpot e : LogoutSpots.getLogoutSpots()) {
                if(trap(e.getBox()))
                    return;
            }

    }

    boolean trap(AxisAlignedBB bb) {
        int placed = 0;
        ArrayList<BlockPos> needsPlacing = new ArrayList<>();

        List<BlockPos> occupiedByPlayer = WorldUtils.getBlocksOccupiedByBox(bb);


        for (BlockPos pos : occupiedByPlayer) {
            BlockPos[] poses = new BlockPos[]{pos.add(0,-1,0),pos.add(1,1,0),pos.add(0,1,1),pos.add(-1,1,0),pos.add(0,1,-1),pos.add(1,0,0),pos.add(0,0,1),pos.add(-1,0,0),pos.add(0,0,-1) };

            BlockPos top = pos.add(0,2,0);
            if(!needsPlacing.contains(top))
                needsPlacing.add(top);

            if(mc.world.getBlockState(top).getBlock().material.isReplaceable())
                if(!BlockInteractUtil.canPlaceBlockAtPos(top,true)){

                    BlockPos[] around = new BlockPos[]{top.add(0,0,1),top.add(0,0,-1),top.add(1,0,0),top.add(-1,0,0)};
                    for (BlockPos p : around) {
                        if(BlockInteractUtil.canPlaceBlockAtPos(p,true)){
                            if(!needsPlacing.contains(p))
                                needsPlacing.add(p);
                            break;
                        }
                    }
                }


            for (BlockPos p : poses) {
                if(!needsPlacing.contains(p))
                    if(mc.world.getBlockState(p).getBlock().material.isReplaceable())
                        needsPlacing.add(p);
            }



        }

        Collections.sort(needsPlacing, new Comparator<BlockPos>() {
            @Override
            public int compare(BlockPos fruit2, BlockPos fruit1)
            {
                return  (int)((PlayerUtil.getDistance(fruit1)-PlayerUtil.getDistance(fruit2))*5);
            }
        });

        for(BlockPos p : needsPlacing){
            if(p.equals(CevBreaker.INSTANCE.getTarget()))
                continue;
            if(TntAura.INSTANCE.blockNeedsToBeEmpty(p))
                continue;
            BlockInteractUtil.BlockPlaceResult res = Place(p);
            if(res == BlockInteractUtil.BlockPlaceResult.FAILED)
                res = Place(p.add(0,-1,0));
            if(res == BlockInteractUtil.BlockPlaceResult.PLACED) {
                if (render.getValue())
                    new FadePos(p, fill, true);
                placed++;
            }
            else if(res == BlockInteractUtil.BlockPlaceResult.WAIT)
                return true;

            if(placed >= blocksPerTick.getValue())
                return true;
        }
        return placed > 0;
    }




    BlockInteractUtil.BlockPlaceResult Place(BlockPos x ){

        BlockInteractUtil.BlockPlaceResult res = (BlockInteractUtil.tryPlaceBlock(x,new HardSolidBlockSwitchItem(), Spark.switchManager.getModeFromString(switchingMode.getValue()),true));

        return res;


    }



}
