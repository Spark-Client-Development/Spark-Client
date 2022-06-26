package me.wallhacks.spark.systems.module.modules.combat;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.module.modules.exploit.PacketMine;
import me.wallhacks.spark.systems.setting.SettingGroup;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
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

    public ModeSetting switchingMode = new ModeSetting("Switch", this, "Normal", ItemSwitcher.modes);

    IntSetting breakBlockDelay = new IntSetting("breakBlockDelay",this,1,0,10);
    IntSetting placeBlockDelay = new IntSetting("placeBlockDelay",this,1,0,10);
    IntSetting breakCrystalDelay = new IntSetting("breakCrystalDelay",this,1,0,10);
    IntSetting placeCrystalDelay = new IntSetting("placeCrystalDelay",this,1,0,10);



    BooleanSetting insta = new BooleanSetting("InstaMine",this,true);
    BooleanSetting smartCrystalPlayer = new BooleanSetting("SmartCrystal",this,true,aBoolean -> insta.isOn());
    BooleanSetting predictBreak = new BooleanSetting("PredictBlockBreak",this,false,aBoolean -> insta.isOn());

    SettingGroup renderG = new SettingGroup("Render", this);
    BooleanSetting render = new BooleanSetting("Render", renderG, true);
    ColorSetting fill = new ColorSetting("Color", renderG, new Color(0x385EDC7B, true));


    public static CevBreaker INSTANCE;

    public CevBreaker() {
        INSTANCE = this;
    }


    int cooldown = 0;


    BlockPos CevBlock;



    @SubscribeEvent
    void OnUpdate(PlayerUpdateEvent event) {

        BlockPos pos = GetCevBreakerBlock();

        if(pos == null || (CevBlock != null && !CevBlock.equals(pos)))
        {

            if(CevBlock == null)
                Spark.sendInfo("Cevbreaker has no target!");
            else
                Spark.sendInfo("Cevbreaker has lost target!");
            CevBlock = null;

            disable();
            return;
        }
        CevBlock = pos;


        if(cooldown > 0)
            cooldown--;
        else
            doCev();




    }
    boolean isMiningBlock = false;

    void doCev() {
        EntityEnderCrystal crystal = null;

        for(Object o : mc.world.loadedEntityList){
            Entity entity = (Entity)o;
            if(entity instanceof EntityEnderCrystal){
                EntityEnderCrystal e = (EntityEnderCrystal)entity;
                if(e.isEntityAlive() && PlayerUtil.getPlayerPosFloored(e).add(0,-1,0).equals(CevBlock)){
                    crystal = e;
                }
            }
        }
        boolean isBlockThere = (mc.world.getBlockState(CevBlock).getBlock().material.isSolid());
        boolean isCrystalThere = (crystal != null);




        if(!isCrystalThere && !isBlockThere)
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
            else if(res == BlockInteractUtil.BlockPlaceResult.PLACED)
                cooldown = placeCrystalDelay.getValue();
            //place obi
        }

        if(!isCrystalThere && (!smartCrystalPlayer.isOn() || !insta.isOn() || PacketMine.instance.ticksFromDone() < 4) && isBlockThere)
        {

            if(placeCrystalOnBlock(CevBlock))
                cooldown = breakBlockDelay.getValue();
            //place crystal

        }
        else if(isBlockThere)
        {
            isMiningBlock = true;
            if(Spark.breakManager.setCurrentBlock(CevBlock,insta.isOn(),switchingMode.is("Const"),3))
            {
                isMiningBlock = false;
                cooldown = breakCrystalDelay.getValue();
                if(cooldown > 0)
                    return;
            }
            //break obi

        }

        if(isCrystalThere && (!isBlockThere || (predictBreak.isOn() && CevBlock.equals(PacketMine.instance.pos) && PacketMine.instance.shouldBeGone)))
        {
            if(isMiningBlock)
            {
                isMiningBlock = false;
                cooldown = breakCrystalDelay.getValue();
                if(cooldown > 0)
                    return;
            }

            if(CrystalUtil.breakCrystal(crystal,CevBlock))
                cooldown = placeBlockDelay.getValue();

            //break crystal
        }



    }


    BlockInteractUtil.BlockPlaceResult place(BlockPos p) {
        BlockInteractUtil.BlockPlaceResult res = (BlockInteractUtil.tryPlaceBlock(p,new SpecBlockSwitchItem(Blocks.OBSIDIAN),Spark.switchManager.getModeFromString(switchingMode.getValue()), true));
        if(res == BlockInteractUtil.BlockPlaceResult.PLACED)
            if (render.getValue())
                new FadePos(p, fill,true);
        return res;
    }




    public boolean placeCrystalOnBlock(BlockPos bestPos){

        Vec3d pos = CrystalUtil.getRotationPos(false,bestPos,null);
        final RayTraceResult result = mc.world.rayTraceBlocks(PlayerUtil.getEyePos(), pos, false, true, false);
        EnumFacing facing = (result == null || !bestPos.equals(result.getBlockPos()) || result.sideHit == null) ? EnumFacing.UP : result.sideHit;

        Vec3d v = new Vec3d(bestPos).add(0.5, 0.5, 0.5).add(new Vec3d(facing.getDirectionVec()).scale(0.5));

        if (result != null && bestPos.equals(result.getBlockPos()) && result.hitVec != null)
            v = result.hitVec;
        if (bestPos.getY() >= 254)
            facing = EnumFacing.EAST;

        //offhand
        EnumHand hand = Spark.switchManager.Switch(new SpecItemSwitchItem(Items.END_CRYSTAL), ItemSwitcher.usedHand.Both, switchingMode.getValue());
        if (hand == null)
            return false;


        //rotate if needed
        if (!Spark.rotationManager.rotate(Spark.rotationManager.getLegitRotations(pos),true))
            return false;

        v = v.add(-bestPos.getX(),-bestPos.getY(),-bestPos.getZ());

        //send packet
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(bestPos, facing, hand, (float) v.x, (float) v.y, (float) v.z));

        //swing
        switch (AntiCheatConfig.getInstance().placeSwing.getValue()) {
            case "Normal":
                mc.player.swingArm(hand);
                break;
            case "Packet":
                mc.player.connection.sendPacket(new CPacketAnimation(hand));
                break;
        }

        if (render.getValue())
            new FadePos(bestPos, fill, true);

        return true;
    }

    BlockPos getTarget() {
        return isEnabled() ? CevBlock : null;
    }

    BlockPos GetCevBreakerBlock(){
        BlockPos NewPos = null;
        double bestDistance = Double.MAX_VALUE;
        for(Object o : mc.world.loadedEntityList){
            Entity entity = (Entity)o;
            if(entity instanceof EntityPlayer){
                EntityPlayer e = (EntityPlayer)entity;
                // && e.onGround
                if(AttackUtil.canAttackPlayer(e,10)) {

                    BlockPos floored = PlayerUtil.getPlayerPosFloored(e);

                    if(!mc.world.getBlockState(floored).getBlock().material.isSolid()){
                        if(!mc.world.getBlockState(floored.add(0,1,0)).getBlock().material.isSolid()){


                            if(mc.world.getBlockState(floored.add(0,2,0)).getBlock() != Blocks.BEDROCK){

                                if(mc.world.getBlockState(floored.add(0,3,0)).getBlock() == Blocks.AIR){
                                    if(mc.world.getBlockState(floored.add(0,4,0)).getBlock() == Blocks.AIR){
                                        BlockPos p = PlayerUtil.getPlayerPosFloored(e).add(0,2,0);


                                        if(p.equals(CevBlock))
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

                    }


                }
            }

        }
        return NewPos;
    }



    public boolean isInAttackZone(EntityPlayer player) {
        if(isEnabled() && CevBlock != null)
        {
            BlockPos floored = PlayerUtil.getPlayerPosFloored(player);
            if(floored.add(0,2,0).equals(CevBlock))
                return true;
        }
        return false;
    }
}
