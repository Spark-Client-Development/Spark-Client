package me.wallhacks.spark.systems.module.modules.combat;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.util.combat.AttackUtil;
import me.wallhacks.spark.util.combat.CrystalUtil;
import me.wallhacks.spark.util.combat.HoleUtil;
import me.wallhacks.spark.util.objects.FadePos;
import me.wallhacks.spark.util.player.BlockInteractUtil;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.player.RaytraceUtil;
import me.wallhacks.spark.util.player.RotationUtil;
import me.wallhacks.spark.util.player.itemswitcher.ItemSwitcher;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scala.collection.generic.BitOperations;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Module.Registration(name = "TntAura", description = "uses tnt to blow up your enemy")
public class TntAura extends Module {
    public static TntAura INSTANCE;

    BlockPos targetFloorPos;

    BooleanSetting closeTop = new BooleanSetting("CloseTop", this, true);
    BooleanSetting extraHeight = new BooleanSetting("ExtraHeight", this, false);

    BooleanSetting render = new BooleanSetting("Render", this, true, "Render");
    ColorSetting fill = new ColorSetting("Color", this, new Color(0x385EDC7B, true), "Render");




    public TntAura() {
        INSTANCE = this;
    }


    int getH() {
        return extraHeight.isOn() ? 3 : 2;
    }


    @SubscribeEvent
    void OnUpdate(PlayerUpdateEvent event) {

        BlockPos newPos = GetTntBlock();

        if(newPos == null || (targetFloorPos != null && !targetFloorPos.equals(newPos)))
        {

            if(targetFloorPos == null)
                Spark.sendInfo("TntAura has no target!");
            else
                Spark.sendInfo("TntAura has lost target!");
            targetFloorPos = null;

            disable();
            return;
        }
        targetFloorPos = newPos;


        BlockPos targetPos = targetFloorPos.add(0,getH(),0);

        ArrayList<BlockPos> surround = new ArrayList<BlockPos>();
        surround.add(targetPos.add(0,0,1));
        surround.add(targetPos.add(0,0,-1));
        surround.add(targetPos.add(1,0,0));
        surround.add(targetPos.add(-1,0,0));

        Collections.sort(surround, new Comparator<BlockPos>() {
            @Override
            public int compare(BlockPos fruit2, BlockPos fruit1)
            {
                return  (int)((PlayerUtil.getDistance(fruit1)-PlayerUtil.getDistance(fruit2))*5);
            }
        });

        for (BlockPos pos : surround) {
            if(mc.world.getBlockState(pos).getBlock().material.isReplaceable())
            {
                BlockInteractUtil.BlockPlaceResult res = place(pos,new HardSolidBlockSwitchItem());
                if(res == BlockInteractUtil.BlockPlaceResult.FAILED)
                {
                    res = place(pos.add(0,-1,0),new HardSolidBlockSwitchItem());
                    if(res == BlockInteractUtil.BlockPlaceResult.FAILED)
                    {
                        res = place(pos.add(0,-2,0),new HardSolidBlockSwitchItem());
                        if(res == BlockInteractUtil.BlockPlaceResult.FAILED)
                            res = place(pos.add(0,-3,0),new HardSolidBlockSwitchItem());
                    }

                }
                return;
            }

        }


        if(mc.world.getBlockState(targetPos).getBlock() == Blocks.TNT)
        {
            if(closeTop.isOn())
            {
                if(mc.world.getBlockState(targetPos.add(0,1,0)).getBlock().material.isReplaceable())
                {
                    place(targetPos.add(0,1,0),new HardSolidBlockSwitchItem());
                    return;
                }
            }


            Vec3d pos = PlayerUtil.getClosestPoint(RaytraceUtil.getPointToLookAtBlock(targetPos));
            EnumFacing facing = EnumFacing.UP;

            if(pos == null)
                pos = new Vec3d(targetPos.getX()+0.5,targetPos.getY()+0.5,targetPos.getZ()+0.5);
            else
                facing = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ),pos,false).sideHit;


            if(AntiCheatConfig.INSTANCE.getBlockRotate())
            if(!Spark.rotationManager.rotate(Spark.rotationManager.getLegitRotations(pos), AntiCheatConfig.getInstance().getBlockRotStep(), 6, false, true))
                return;

            EnumHand hand = ItemSwitcher.Switch(new SpecItemSwitchItem(Items.FLINT_AND_STEEL), ItemSwitcher.switchType.Both);

            if(hand == null)
                return;

            BlockInteractUtil.processRightClickBlock(targetPos,facing,true,hand,pos);
        }
        else
            place(targetPos,new SpecBlockSwitchItem(Blocks.TNT));








    }



    BlockInteractUtil.BlockPlaceResult place(BlockPos p, BlockSwitchItem item) {

        BlockInteractUtil.BlockPlaceResult res = (BlockInteractUtil.tryPlaceBlock(p,item,true,true,4));
        if(res == BlockInteractUtil.BlockPlaceResult.PLACED)
            if (render.getValue())
                new FadePos(p, fill,true);
        return res;
    }



    BlockPos GetTntBlock(){
        BlockPos NewPos = null;
        double bestDistance = Double.MAX_VALUE;

        entityLoop:
        for(Entity entity : mc.world.loadedEntityList){

            if(entity instanceof EntityPlayer){
                EntityPlayer e = (EntityPlayer)entity;
                // && e.onGround
                if(AttackUtil.canAttackPlayer(e,10)) {

                    BlockPos floored = PlayerUtil.getPlayerPosFloored(e);

                    if(!mc.world.getBlockState(floored).getBlock().material.isSolid()){

                        for (int i = 0; i < getH(); i++) {
                            if(mc.world.getBlockState(floored.add(0,1,0)).getBlock().material.isSolid()){
                                continue entityLoop;
                            }
                        }

                        BlockPos p = PlayerUtil.getPlayerPosFloored(e);

                        if(p.equals(targetFloorPos))
                            return p;

                        float dis = PlayerUtil.getDistance(p);
                        if(dis < bestDistance)
                        {
                            bestDistance = dis;
                            NewPos = p;
                        }

                    }
                }
            }

        }
        return NewPos;
    }


    public boolean isInAttackZone(EntityPlayer player) {
        if(isEnabled() && targetFloorPos != null)
        {
            BlockPos floored = PlayerUtil.getPlayerPosFloored(player);
            if(floored.equals(targetFloorPos))
                return true;
        }
        return false;
    }
}