package me.wallhacks.spark.systems.module.modules.combat;

import baritone.api.utils.BlockUtils;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.manager.BreakManager;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.module.modules.exploit.InstaMine;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.WorldUtils;
import me.wallhacks.spark.util.combat.AttackUtil;
import me.wallhacks.spark.util.combat.CrystalUtil;
import me.wallhacks.spark.util.objects.FadePos;
import me.wallhacks.spark.util.player.BlockInteractUtil;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.player.RaytraceUtil;
import me.wallhacks.spark.util.player.RotationUtil;
import me.wallhacks.spark.util.player.itemswitcher.ItemSwitcher;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.HardSolidBlockSwitchItem;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.SpecBlockSwitchItem;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.SpecItemSwitchItem;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Module.Registration(name = "CevBreaker", description = "Steals from chests")
public class CevBreaker extends Module {



    IntSetting delay = new IntSetting("Delay",this,1,0,10,"Time");
    IntSetting breakDelay = new IntSetting("BreakDelay",this,6,0,10,"Time");
    IntSetting placeDelay = new IntSetting("PlaceDelay",this,2,0,10,"Time");



    enum CBState {
        placeObi,placeCrystal,breakObi,breakCrystal,standBy
    }
    int cooldown = 0;


    BlockPos CevBlock;
    CBState lastState = CBState.standBy;


    @SubscribeEvent
    void OnUpdate(PlayerUpdateEvent event) {

        GetCevBreakerBlock();

        if(CevBlock != null)
        {
            GetState();

            if(cooldown <= 0)
            {
                if(lastState == CBState.placeObi)
                {
                    Vec3d CevBlockPos = new Vec3d(CevBlock).add(0.5,1,0.5);
                    Vec3i offset = new Vec3i(Math.floor(Math.max(-1,Math.min(CevBlockPos.x-mc.player.posX, 1))),0,Math.floor(Math.max(-1,Math.min(CevBlockPos.y-mc.player.posZ, 1))));
                    if(offset.getX() != 0 && offset.getZ() != 0)
                        offset = new Vec3i(offset.getX(),0,0);
                    BlockPos blockSetOff = CevBlock.add(offset);


                    BlockInteractUtil.BlockPlaceResult res = (BlockInteractUtil.tryPlaceBlock(CevBlock,new SpecBlockSwitchItem(Blocks.OBSIDIAN),true,true,4));
                    if(res == BlockInteractUtil.BlockPlaceResult.FAILED)
                        res = (BlockInteractUtil.tryPlaceBlock(blockSetOff, new SpecBlockSwitchItem(Blocks.OBSIDIAN),true,true,4));
                    if(res == BlockInteractUtil.BlockPlaceResult.FAILED)
                        res = (BlockInteractUtil.tryPlaceBlock(blockSetOff.add(0,-1,0),new SpecBlockSwitchItem(Blocks.OBSIDIAN),true,true,4));


                    if(placeDelay.getValue() == 0)
                        GetState();
                }
                if(lastState == CBState.placeCrystal)
                {
                    PlaceCrystalOnBlock(CevBlock);
                    cooldown = 20;
                }
                if(lastState == CBState.breakObi)
                {
                    Spark.breakManager.setCurrentBlock(CevBlock);
                    cooldown = 20;
                }
                if(lastState == CBState.breakCrystal)
                {
                    BreakCrystal(crystal,CevBlock);
                    cooldown = 2;
                }
            }
            cooldown--;
        }



    }

    EntityEnderCrystal crystal;
    void GetState(){

        boolean isBlockThere = (mc.world.getBlockState(CevBlock).getBlock().material.isSolid());
        crystal = null;
        double d0 = (double)CevBlock.getX();
        double d1 = (double)CevBlock.getY();
        double d2 = (double)CevBlock.getZ();
        for(Object o : mc.world.loadedEntityList){
            Entity entity = (Entity)o;
            if(entity instanceof EntityEnderCrystal){
                EntityEnderCrystal e = (EntityEnderCrystal)entity;
                if(e.isEntityAlive() && PlayerUtil.GetPlayerPosFloored(e).add(0,-1,0).equals(CevBlock)){
                    crystal = e;
                }
            }
        }
        boolean isCrystalThere = (crystal != null);
        CBState state = CBState.standBy;
        if(isCrystalThere && !isBlockThere)
            state = CBState.breakCrystal;
        else if(isCrystalThere && isBlockThere)
            state = CBState.breakObi;
        else if(!isCrystalThere && isBlockThere)
            state = CBState.placeCrystal;
        else if(!isCrystalThere && !isBlockThere)
            state = CBState.placeObi;

        if(isCrystalThere && isBlockThere && InstaMine.instance.isEnabled() && InstaMine.instance.pos == CevBlock && delay.getValue() == 0)
            state = CBState.breakCrystal;

        if(lastState != state){
            cooldown = delay.getValue();
            if(lastState == CBState.placeObi)
                cooldown = placeDelay.getValue();
            if(lastState == CBState.breakObi)
                cooldown = breakDelay.getValue();

        }
        lastState = state;

    }



    public void BreakCrystal(EntityEnderCrystal _Target,BlockPos bestPos){

        Vec3d pos = CrystalUtil.getRotationPos(false,bestPos,null);

        //rotate if needed
        if (!Spark.rotationManager.Rotate(Spark.rotationManager.getLegitRotations(pos), AntiCheatConfig.getInstance().getCrystalRotStep(), 4, false, true))
            return;

        mc.player.connection.sendPacket(new CPacketUseEntity(_Target));


        mc.player.swingArm(EnumHand.MAIN_HAND);

    }

    public void PlaceCrystalOnBlock(BlockPos bestPos){

        Vec3d pos = CrystalUtil.getRotationPos(false,bestPos,null);
        final RayTraceResult result = MC.mc.world.rayTraceBlocks(PlayerUtil.getEyePos(), pos, false, true, false);
        EnumFacing facing = (result == null || !bestPos.equals(result.getBlockPos()) || result.sideHit == null) ? EnumFacing.UP : result.sideHit;

        Vec3d v = new Vec3d(bestPos).add(0.5, 0.5, 0.5).add(new Vec3d(facing.getDirectionVec()).scale(0.5));

        if (result != null && bestPos.equals(result.getBlockPos()) && result.hitVec != null)
            v = result.hitVec;
        if (bestPos.getY() >= 254)
            facing = EnumFacing.EAST;

        //offhand
        EnumHand hand = ItemSwitcher.Switch(new SpecItemSwitchItem(Items.END_CRYSTAL), ItemSwitcher.switchType.Both);
        if (hand == null)
            return;


        //rotate if needed
        if (!Spark.rotationManager.Rotate(Spark.rotationManager.getLegitRotations(pos), AntiCheatConfig.getInstance().getCrystalRotStep(), 4, false, true))
            return;


        //send packet
        MC.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(bestPos, facing, hand, (float) v.x, (float) v.y, (float) v.z));

        //swing
        switch (AntiCheatConfig.getInstance().CrystalPlaceSwing.getValue()) {
            case "Normal":
                MC.mc.player.swingArm(hand);
                break;
            case "Packet":
                MC.mc.player.connection.sendPacket(new CPacketAnimation(hand));
                break;
        }

    }

    void GetCevBreakerBlock(){
        BlockPos NewPos = null;
        for(Object o : mc.world.loadedEntityList){
            Entity entity = (Entity)o;
            if(entity instanceof EntityPlayer){
                EntityPlayer e = (EntityPlayer)entity;
                // && e.onGround
                if(AttackUtil.CanAttackPlayer(e,10)) {

                    if(!mc.world.getBlockState(PlayerUtil.GetPlayerPosFloored(e)).getBlock().material.isSolid()){
                        NewPos = PlayerUtil.GetPlayerPosFloored(e).add(0,2,0);

                        if(NewPos.equals(CevBlock))
                            break;

                    }


                }
            }

        }
        CevBlock = NewPos;
    }



}
