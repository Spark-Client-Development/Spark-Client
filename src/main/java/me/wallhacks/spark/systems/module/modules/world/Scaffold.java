package me.wallhacks.spark.systems.module.modules.world;

import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.objects.FadePos;
import me.wallhacks.spark.util.objects.Timer;
import me.wallhacks.spark.util.player.BlockInteractUtil;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.SolidBlockSwitchItem;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;

import java.awt.*;

@Module.Registration(name = "Scaffold", description = "Steals from chests")
public class Scaffold extends Module {

    IntSetting Extended = new IntSetting("Extended",this,2,0,4,"General");



    BooleanSetting Tower = new BooleanSetting("FastTower",this,true,"General");
    IntSetting TowerPause = new IntSetting("TowerPause",this,15,10,50,v -> Tower.isOn(),"General");
    BooleanSetting TowerCenter = new BooleanSetting("TowerCenter",this,false,"General");

    BooleanSetting Silent = new BooleanSetting("SilentSwitch",this,false,"General");



    BooleanSetting render = new BooleanSetting("Render", this, true, "Render");
    ColorSetting fill = new ColorSetting("Color", this, new Color(0x385EDC5E, true), "Render");




    Timer scaffoldPauseTimer = new Timer();
    Timer lastTowerPlaced = new Timer();
    Timer towerStart = new Timer();



    @SubscribeEvent
    void OnUpdate(PlayerUpdateEvent event) {
        doScaffold();

        lastPosX = mc.player.posX;
        lastPosZ = mc.player.posZ;
    }

    boolean doTowerCenter() {
        return (mc.gameSettings.keyBindJump.isKeyDown() && TowerCenter.isOn());
    }

    void doScaffold() {

        final BlockPos floorPos = PlayerUtil.getPlayerPosFloored(mc.player,0.2).add(0, -1, 0);
        final Block floor = mc.world.getBlockState(floorPos).getBlock();


        if(doTowerCenter()){
            if(mc.player.onGround && PlayerUtil.MoveCenter(floorPos,false)){
                mc.player.motionY = -0.28f;
                return;
            }
        }


        if (floor.material.isReplaceable())
        {
            if(Place(floorPos) == BlockInteractUtil.BlockPlaceResult.PLACED) {
                if (render.getValue())
                    new FadePos(floorPos, fill, true);
                if(mc.gameSettings.keyBindJump.isKeyDown())
                {
                    lastTowerPlaced.reset();

                    if(towerStart.passedMs(500))
                        if(Tower.isOn())
                        {

                            if(scaffoldPauseTimer.passedMs(TowerPause.getValue()*100))
                            {
                                mc.player.motionY = -0.28f;
                                scaffoldPauseTimer.reset();
                            }
                            else
                            {
                                this.mc.player.jump();
                            }
                        }
                        else
                            scaffoldPauseTimer.reset();
                }
            }

        }
        if(!lastTowerPlaced.passedMs(500))
            return;
        towerStart.reset();

        if(doTowerCenter())
            return;


        //extended
        int x = (int) Math.round(Math.max(-1,Math.min(1, (mc.player.posX - lastPosX)*20)));
        int y = (int) Math.round(Math.max(-1,Math.min(1, (mc.player.posZ - lastPosZ)*20)));



        for (int i = 1; i <= Extended.getValue()+1; i++) {
            BlockPos headPos = floorPos.add(x*i, 0, y*i);

            if(mc.player.getDistance(headPos.getX()+0.5,headPos.getY()+1,headPos.getZ()+0.5) < 0.8+Extended.getValue()) {
                final Block head = mc.world.getBlockState(headPos).getBlock();
                if (head.material.isReplaceable())	{
                    if(Place(headPos) != BlockInteractUtil.BlockPlaceResult.FAILED) {
                        if (render.getValue())
                            new FadePos(headPos, fill, true);
                        return;
                    }
                }
            }
        }

    }
    double lastPosX;
    double lastPosZ;


    BlockInteractUtil.BlockPlaceResult Place(BlockPos x ){
        int lastItem = mc.player.inventory.currentItem;

        //prevent falling down
        if(BlockInteractUtil.getDirForPlacingBlockAtPos(x) == null){
            BlockPos[] poses = new BlockPos[]{x.add(0,0,1),x.add(0,0,-1),x.add(1,0,0),x.add(-1,0,0)};
            for (BlockPos pos : poses) {
                if(BlockInteractUtil.getDirForPlacingBlockAtPos(pos) != null){
                    x = pos;
                    break;
                }
            }
        }

        BlockInteractUtil.BlockPlaceResult res = (BlockInteractUtil.tryPlaceBlock(x,new SolidBlockSwitchItem(),true,true,18));


        if(Silent.isOn())
            mc.player.inventory.currentItem = lastItem;

        return res;


    }

    double getDisToEdge(){
        double dis = 0;

        BlockPos pos = PlayerUtil.GetPlayerPosHighFloored(mc.player);

        if(!mc.world.getBlockState(pos.add(1, -1, 0)).getBlock().material.isReplaceable())
            dis = Math.max(dis, Math.abs(mc.player.posX-(pos.getX()+1)));
        if(!mc.world.getBlockState(pos.add(-1, -1, 0)).getBlock().material.isReplaceable())
            dis = Math.max(dis, Math.abs(mc.player.posX-(pos.getX())));
        if(!mc.world.getBlockState(pos.add(0, -1, 1)).getBlock().material.isReplaceable())
            dis = Math.max(dis, Math.abs(mc.player.posZ-(pos.getZ()+1)));
        if(!mc.world.getBlockState(pos.add(0, -1, -1)).getBlock().material.isReplaceable())
            dis = Math.max(dis, Math.abs(mc.player.posZ-(pos.getZ())));

        return dis;
    }
}
