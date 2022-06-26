package me.wallhacks.spark.systems.module.modules.world;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.event.player.SafeWalkEvent;
import me.wallhacks.spark.event.player.SneakEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.SettingGroup;
import me.wallhacks.spark.systems.setting.settings.*;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.objects.FadePos;
import me.wallhacks.spark.util.objects.Timer;
import me.wallhacks.spark.util.player.BlockInteractUtil;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.player.itemswitcher.ItemSwitcher;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.SolidBlockSwitchItem;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Arrays;

@Module.Registration(name = "Scaffold", description = "Steals from chests")
public class Scaffold extends Module {

    BooleanSetting Strict = new BooleanSetting("Strict",this,false);
    IntSetting Extended = new IntSetting("Extended",this,2,0,4,v -> Strict.isOn());
    BooleanSetting Tower = new BooleanSetting("FastTower",this,true);
    IntSetting TowerPause = new IntSetting("TowerPause",this,15,10,50,v -> !Tower.isOn());
    BooleanSetting TowerCenter = new BooleanSetting("TowerCenter",this,false,v -> !Tower.isOn());
    KeySetting down = new KeySetting("Downwards",this,-1);
    ModeSetting switchingMode = new ModeSetting("Switch", this, "Normal", ItemSwitcher.modes);

    SettingGroup renderG = new SettingGroup("Render", this);
    BooleanSetting render = new BooleanSetting("Render", renderG, true);
    ColorSetting fill = new ColorSetting("Color", renderG, new Color(0x385EDC5E, true));





    Timer scaffoldPauseTimer = new Timer();
    Timer lastTowerPlaced = new Timer();
    Timer towerStart = new Timer();


    @Override
    public void onEnable() {
        super.onEnable();


    }

    @SubscribeEvent
    void OnUpdate(PlayerUpdateEvent event) {
        doScaffold();


    }

    boolean doTowerCenter() {
        return (mc.gameSettings.keyBindJump.isKeyDown() && TowerCenter.isOn());
    }

    void doScaffold() {

        if(down.isDown()) {


            final BlockPos floorPos = PlayerUtil.getPlayerPosFloored(mc.player,-0.1).add(0, -1, 0);

            final Block floor = mc.world.getBlockState(floorPos).getBlock();

            if (floor.material.isReplaceable()) {

                if(Strict.isOn())
                {
                    if(mc.player.onGround && !mc.gameSettings.keyBindJump.isKeyDown())
                    {
                        if(getDisToEdge() < 0.2)
                            return;
                    }

                }

                BlockInteractUtil.BlockPlaceResult res = Place(floorPos);

                if(res == BlockInteractUtil.BlockPlaceResult.FAILED)
                    res = Place(floorPos.add(1,0,0));
                if(res == BlockInteractUtil.BlockPlaceResult.FAILED)
                    res = Place(floorPos.add(-1,0,0));
                if(res == BlockInteractUtil.BlockPlaceResult.FAILED)
                    res = Place(floorPos.add(0,0,-1));
                if(res == BlockInteractUtil.BlockPlaceResult.FAILED)
                    res = Place(floorPos.add(0,0,1));

                if (res == BlockInteractUtil.BlockPlaceResult.PLACED) {
                    if (render.getValue())
                        new FadePos(floorPos, fill, true);
                }
            }
            return;
        }


        final BlockPos floorPos = PlayerUtil.getPlayerPosFloored(mc.player,0.2).add(0, -1, 0);


        final Block floor = mc.world.getBlockState(floorPos).getBlock();

        if(doTowerCenter()){
            if(towerStart.passedMs(500) && mc.player.onGround && PlayerUtil.MoveCenter(floorPos,false)){
                mc.player.motionY = -0.28f;
                return;
            }
        }

        if (floor.material.isReplaceable())
        {
            if(Place(floorPos) == BlockInteractUtil.BlockPlaceResult.PLACED) {
                if (render.getValue())
                    new FadePos(floorPos, fill, true);
                if(mc.gameSettings.keyBindJump.isKeyDown() && mc.player.movementInput.moveStrafe == 0 && mc.player.movementInput.moveForward == 0)
                {
                    lastTowerPlaced.reset();

                    if(towerStart.passedMs(500))
                        if(Tower.isOn()) {
                            if(scaffoldPauseTimer.passedMs(TowerPause.getValue()*100)) {
                                mc.player.motionY = -0.28f;
                                scaffoldPauseTimer.reset();
                            }
                            else {
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

        if(Strict.isOn())
            return;

        //extended
        int x = (int) Math.round(Math.max(-1,Math.min(1, (mc.player.posX - mc.player.lastReportedPosX)*20)));
        int y = (int) Math.round(Math.max(-1,Math.min(1, (mc.player.posZ - mc.player.lastReportedPosZ)*20)));



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


    BlockInteractUtil.BlockPlaceResult Place(BlockPos x ){

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

        BlockInteractUtil.BlockPlaceResult res = (BlockInteractUtil.tryPlaceBlock(x,new SolidBlockSwitchItem(),Spark.switchManager.getModeFromString(switchingMode.getValue()), true));



        return res;


    }

    @SubscribeEvent
    public void onSneakEvent(SneakEvent event) {
        if (down.isDown()) {
            event.setCanceled(true);
        }
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
