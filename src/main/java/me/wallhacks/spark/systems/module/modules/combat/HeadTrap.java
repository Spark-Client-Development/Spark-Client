package me.wallhacks.spark.systems.module.modules.combat;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.module.modules.exploit.PacketMine;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
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

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Module.Registration(name = "AutoHeadTrap", description = "Places Block on your Head to trap enemies")
public class HeadTrap extends Module {

    BooleanSetting smart = new BooleanSetting("Smart", this, true, "General");
    IntSetting blocksPerTick = new IntSetting("BlocksPerTick",this,1,1,8,"General");
    ModeSetting switchingMode = new ModeSetting("Switch", this, "Silent",  Arrays.asList("Normal","Silent","Const"));

    ModeSetting disable = new ModeSetting("Disable", this, "Off", Arrays.asList("Off", "Done", "OffGround"), "General");


    BooleanSetting render = new BooleanSetting("Render", this, true, "Render");
    ColorSetting fill = new ColorSetting("Fill", this, new Color(0x38B928AD, true), "Render");


    @SubscribeEvent
    void OnUpdate(PlayerUpdateEvent event) {




        BlockPos blockUnderPlayer = PlayerUtil.getPlayerPosFloored(mc.player, 0.2);

        if(!mc.player.onGround)
        {
            if(disable.is("OffGround"))
                disable();
            return;
        }

        smartChecks:
        if(smart.isOn())
        {
            for(Object o : mc.world.loadedEntityList.toArray()){
                if(o instanceof EntityPlayer){
                    {
                        EntityPlayer e = (EntityPlayer)o;
                        if(AttackUtil.canAttackPlayer(e,15)) {
                            if(PredictionUtil.isPlayerTryingToGetInHole(e,blockUnderPlayer)) break smartChecks;
                        }
                    }

                }
            }
            return;
        }

        ArrayList<BlockPos> onPlayer = new ArrayList<BlockPos>();


        List<BlockPos> occupiedByPlayer = WorldUtils.getBlocksOccupiedByBox(mc.player.boundingBox);


        for (BlockPos o : occupiedByPlayer) {
            BlockPos p = o.add(0,2,0);
            if (!occupiedByPlayer.contains(p) && !onPlayer.contains(p))
                onPlayer.add(p);


        }

        int placed = 0;
        boolean done = true;
        for (BlockPos x : onPlayer) {
            if (mc.world.getBlockState(x).getBlock().material.isReplaceable()) {
                BlockPos p = getBlockPosToPlaceAtBlock(x);
                if (p != null) {

                    done = false;
                    BlockInteractUtil.BlockPlaceResult res = Place(p);
                    if (p.equals(PacketMine.instance.pos)) PacketMine.instance.pos = null;


                    if (res == BlockInteractUtil.BlockPlaceResult.PLACED) {
                        if (render.getValue())
                            new FadePos(p, fill, true);
                        placed++;
                    } else if (res == BlockInteractUtil.BlockPlaceResult.WAIT)
                        return;

                    if (placed >= blocksPerTick.getValue())
                        return;
                }
            }
        }

        if (done && disable.is("Done")) this.disable();
    }


    BlockPos getBlockPosToPlaceAtBlock(BlockPos pos) {
        if (BlockInteractUtil.canPlaceBlockAtPos(pos, true))
            return pos;
        for (BlockPos x : new BlockPos[]{pos.add(0, 1, 0), pos.add(0, 0, 1), pos.add(0, 0, -1), pos.add(1, 0, 0), pos.add(-1, 0, 0)}) {
            if (BlockInteractUtil.canPlaceBlockAtPos(x, true))
                return x;
        }
        return null;
    }



    BlockInteractUtil.BlockPlaceResult Place(BlockPos x ){


        BlockInteractUtil.BlockPlaceResult res = (BlockInteractUtil.tryPlaceBlock(x,new HardSolidBlockSwitchItem(), Spark.switchManager.getModeFromString(switchingMode.getValue()), true));


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
