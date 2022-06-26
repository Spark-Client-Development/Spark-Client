package me.wallhacks.spark.systems.module.modules.world;

import com.github.lunatrius.core.util.math.BlockPosHelper;
import com.github.lunatrius.core.util.math.MBlockPos;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.event.player.RightClickEvent;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.SettingGroup;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.objects.FadePos;
import me.wallhacks.spark.util.player.BlockInteractUtil;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.player.RaytraceUtil;
import me.wallhacks.spark.util.player.itemswitcher.ItemSwitcher;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.SpecBlockSwitchItem;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.SpecItemSwitchItem;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

@Module.Registration(name = "AutoWither", description = "Steals from chests")
public class AutoWither extends Module {

    ModeSetting witherPlaceMode = new ModeSetting("Place", this, "Toggle", Arrays.asList("Toggle","ClickSkull","Walk"));
    IntSetting blocksPerTick = new IntSetting("BlocksPerTick",this,4,1,10);
    BooleanSetting autoName = new BooleanSetting("AutoName", this, true);

    SettingGroup renderG = new SettingGroup("Render", this);
    BooleanSetting render = new BooleanSetting("Render", renderG, true);
    ColorSetting fill = new ColorSetting("Color", renderG, new Color(0x38DC865E, true));






    int isDone = 0;

    @SubscribeEvent
    void onUpdate(PlayerUpdateEvent event) {

        if(autoName.isOn())
        {
            for(Entity entity : mc.world.loadedEntityList){
                if(entity instanceof EntityWither && mc.player.getDistance(entity) < 4){
                    EntityWither w = (EntityWither)entity;
                    if(w.getDisplayName().getUnformattedComponentText().equalsIgnoreCase("Wither"))
                    {
                        EnumHand hand = Spark.switchManager.Switch(new SpecItemSwitchItem(Items.NAME_TAG), ItemSwitcher.usedHand.Both,AntiCheatConfig.getInstance().getBlockPlaceSwitchType());

                        if(hand != null)
                        {
                            Vec3d lookAt = RaytraceUtil.getPointToLookAtEntity(w);
                            if (lookAt == null) lookAt = w.boundingBox.getCenter();

                            if(AntiCheatConfig.getInstance().attackRotate.getValue())
                                if (!Spark.rotationManager.rotate(Spark.rotationManager.getLegitRotations(lookAt), true))
                                    return;

                            mc.playerController.interactWithEntity(mc.player, w, hand);

                            return;
                        }


                    }
                }

            }
        }





        if(placeWither != null) {


            int placed = 0;

            for (int i = 0; i < placeWither.size(); i++) {

                BlockPos p = placeWither.get(i);

                if(PlayerUtil.getDistance(p) > 5)
                {
                    placeWither = null;
                    return;
                }

                if(mc.world.getBlockState(p).getBlock().material.isReplaceable()){
                    isDone = 20;

                    if(placed >= blocksPerTick.getValue())
                        return;


                    BlockInteractUtil.BlockPlaceResult res = BlockInteractUtil.tryPlaceBlock(p, new SpecBlockSwitchItem(i < 4 ? Blocks.SOUL_SAND : Blocks.SKULL), true);



                    if (res == BlockInteractUtil.BlockPlaceResult.PLACED)
                    {
                        if(render.getValue())
                            new FadePos(p, fill, true);
                        placed++;


                    }
                    else if(res == BlockInteractUtil.BlockPlaceResult.WAIT)
                        return;



                }


            }
            if(isDone <= 0){
                placeWither = null;

                if(witherPlaceMode.isValueName("ClickSkull"))
                    Spark.switchManager.Switch(new SpecItemSwitchItem(Items.SKULL), ItemSwitcher.usedHand.Both);
            }
            else
                isDone--;

        }
        else if(placeWither == null)
        {
            if(witherPlaceMode.isValueName("Toggle"))
                setEnabled(false);
            else if(witherPlaceMode.isValueName("Walk") && mc.player.onGround)
            {
                int x = (int) Math.round(Math.max(-1,Math.min(1, (mc.player.posX - mc.player.lastTickPosX)*22)));
                int y = (int) Math.round(Math.max(-1,Math.min(1, (mc.player.posZ - mc.player.lastTickPosZ)*22)));

                if(x != 0 || y != 0){
                    BlockPos pos = PlayerUtil.getPlayerPosFloored(mc.player).add(-x*2, -1, -y*2);
                    PlaceWitherAtPos(pos);
                }
            }

        }

    }

    public void onEnable() {

        super.onEnable();

        placeWither = null;

        if(nullCheck())
            return;

        if(witherPlaceMode.isValueName("Toggle"))
        {
            if(mc.objectMouseOver != null && mc.objectMouseOver.getBlockPos() != null)
                PlaceWitherAtPos(mc.objectMouseOver.getBlockPos());
            else{
                Spark.sendInfo("No wither spawned! You are not looking at a block!");
                this.setEnabled(false);
            }
        }

    }
    @SubscribeEvent
    void onClick(RightClickEvent event) {

        if(witherPlaceMode.isValueName("ClickSkull")) {
            if (mc.player.getHeldItemMainhand().isEmpty() || !mc.player.getHeldItemMainhand().getItem().getTranslationKey().toLowerCase().contains("skull"))
                return;
            if(mc.objectMouseOver != null && mc.objectMouseOver.getBlockPos() != null)
            {
                PlaceWitherAtPos(mc.objectMouseOver.getBlockPos());
                event.setCanceled(true);
            }


        }
    }



    public boolean PlaceWitherAtPos(BlockPos pos){

        placeWither = new ArrayList<>();

        pos = pos.add(0,1,0);


        placeWither = new ArrayList<BlockPos>();
        placeWither.add(pos.add(0,0,0));
        placeWither.add(pos.add(0,1,0));

        Vec3d v = new Vec3d(mc.player.posX-(pos.getX()+0.5f),mc.player.posY-(pos.getY()+0.5f),mc.player.posZ-(pos.getZ()+0.5f));
        v = new Vec3d(Math.round(v.x/3f)*3f, 0, Math.round(v.y/3f)*3f);

        if(v.x == 0){
            placeWither.add(pos.add(1,1,0));
            placeWither.add(pos.add(-1,1,0));
        }else{
            placeWither.add(pos.add(0,1,1));
            placeWither.add(pos.add(0,1,-1));
        }

        if(v.x == 0){
            placeWither.add(pos.add(1,2,0));
            placeWither.add(pos.add(-1,2,0));
        }else{
            placeWither.add(pos.add(0,2,1));
            placeWither.add(pos.add(0,2,-1));
        }

        placeWither.add(pos.add(0,2,0));

        for(BlockPos p : placeWither)
        {
            final Block block = mc.world.getBlockState(p).getBlock();

            if (block != Blocks.AIR){
                placeWither = null;
                return false;
            }

            double d0 = (double)p.getX();
            double d1 = (double)p.getY()+1;
            double d2 = (double)p.getZ();
            double d0b = d0 + 1;
            double d1b = d1 + 1;
            double d2b = d2 + 1;
            for(Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(d0, d1, d2, d0b, d1b, d2b).grow(1)))
            {
                if(!entity.isDead && !(entity instanceof EntityItem)){
                    placeWither = null;
                    return false;
                }
            }



        }


        return true;
    }



    public ArrayList<BlockPos> placeWither;




}
