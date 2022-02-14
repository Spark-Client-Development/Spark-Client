package me.wallhacks.spark.systems.module.modules.combat;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.manager.RotationManager;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.module.modules.exploit.InstaMine;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.util.combat.AttackUtil;
import me.wallhacks.spark.util.combat.CrystalUtil;
import me.wallhacks.spark.util.objects.FadePos;
import me.wallhacks.spark.util.player.BlockInteractUtil;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.player.itemswitcher.ItemSwitcher;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.SpecBlockSwitchItem;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.SpecItemSwitchItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

@Module.Registration(name = "CevBreaker", description = "Steals from chests")
public class CevBreaker extends Module {


    IntSetting delay = new IntSetting("Delay",this,1,0,10);
    IntSetting breakDelay = new IntSetting("BreakDelay",this,6,0,10);
    IntSetting placeDelay = new IntSetting("PlaceDelay",this,2,0,10);

    BooleanSetting insta = new BooleanSetting("InstaMine",this,true);

    BooleanSetting render = new BooleanSetting("Render", this, true, "Render");
    ColorSetting fill = new ColorSetting("Fill", this, new Color(0x385EDC7B, true), "Render");
    ColorSetting outline = new ColorSetting("Outline", this, new Color(0x912097C4, true), "Render");


    public static CevBreaker INSTANCE;

    public CevBreaker() {
        INSTANCE = this;
    }

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

                    BlockInteractUtil.BlockPlaceResult res = place(CevBlock);
                    if(res == BlockInteractUtil.BlockPlaceResult.FAILED)
                    {
                        res = place(blockSetOff);
                        if(res == BlockInteractUtil.BlockPlaceResult.FAILED)
                        {
                            res = place(blockSetOff.add(0,-1,0));
                            if(res == BlockInteractUtil.BlockPlaceResult.FAILED)
                                res = place(blockSetOff.add(0,-2,0));
                        }

                    }



                    if(placeDelay.getValue() == 0)
                        GetState();
                }
                if(lastState == CBState.placeCrystal)
                {
                    placeCrystalOnBlock(CevBlock);
                    cooldown = 20;
                }
                if(lastState == CBState.breakObi)
                {
                    Spark.breakManager.setCurrentBlock(CevBlock,insta.isOn());
                    cooldown = 20;
                }
                if(lastState == CBState.breakCrystal)
                {
                    CrystalUtil.breakCrystal(crystal,CevBlock);
                    cooldown = 2;
                }
            }
            cooldown--;
        }



    }


    BlockInteractUtil.BlockPlaceResult place(BlockPos p) {
        BlockInteractUtil.BlockPlaceResult res = (BlockInteractUtil.tryPlaceBlock(p,new SpecBlockSwitchItem(Blocks.OBSIDIAN),true,true,4));
        if(res == BlockInteractUtil.BlockPlaceResult.PLACED)
            if (render.getValue())
                new FadePos(p, outline, fill, true);
        return res;
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
                if(e.isEntityAlive() && PlayerUtil.getPlayerPosFloored(e).add(0,-1,0).equals(CevBlock)){
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
    

    public void placeCrystalOnBlock(BlockPos bestPos){

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
            return;


        //rotate if needed
        if (!Spark.rotationManager.rotate(Spark.rotationManager.getLegitRotations(pos), AntiCheatConfig.getInstance().getCrystalRotStep(), 4, false, true))
            return;


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
            new FadePos(bestPos, outline, fill, true);

    }

    void GetCevBreakerBlock(){
        BlockPos NewPos = null;
        for(Object o : mc.world.loadedEntityList){
            Entity entity = (Entity)o;
            if(entity instanceof EntityPlayer){
                EntityPlayer e = (EntityPlayer)entity;
                // && e.onGround
                if(AttackUtil.CanAttackPlayer(e,10)) {

                    if(!mc.world.getBlockState(PlayerUtil.getPlayerPosFloored(e)).getBlock().material.isSolid()){
                        NewPos = PlayerUtil.getPlayerPosFloored(e).add(0,2,0);

                        if(NewPos.equals(CevBlock))
                            break;

                    }


                }
            }

        }
        CevBlock = NewPos;
    }



}
