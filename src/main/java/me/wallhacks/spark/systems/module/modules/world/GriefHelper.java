package me.wallhacks.spark.systems.module.modules.world;

import com.github.lunatrius.core.util.math.BlockPosHelper;
import com.github.lunatrius.core.util.math.MBlockPos;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.clientsetting.clientsettings.AntiCheatConfig;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.WorldUtils;
import me.wallhacks.spark.util.objects.FadePos;
import me.wallhacks.spark.util.player.BlockInteractUtil;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.SpecBlockSwitchItem;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Arrays;

@Module.Registration(name = "GriefHelper", description = "Places tnt")
public class GriefHelper extends Module {

    IntSetting spacing = new IntSetting("Spacing",this,4,2,5);
    IntSetting delay = new IntSetting("Delay",this,0,0,8);
    BooleanSetting render = new BooleanSetting("Render", this, true);
    ColorSetting fill = new ColorSetting("Fill", this, new Color(0x389F5EDC, true));



    int cooldown = 0;

    @SubscribeEvent
    void OnUpdate(PlayerUpdateEvent event) {


        if(cooldown > 0)
        {
            cooldown--;
            return;
        }

        BlockPos floored = PlayerUtil.getPlayerPosFloored(mc.player);

        for (int x = floored.getX()-5; x <= floored.getX()+5; x++) {
            if(x % spacing.getValue() == 0)
            {
                for (int z = floored.getZ()-5; z <= floored.getZ()+5; z++) {
                    if(z % spacing.getValue() == 0)
                    {

                        boolean lastCanPlaceOn = false;
                        for (int h = floored.getY()-5; h <= floored.getY()+5; h++) {
                            Block b = mc.world.getBlockState(new BlockPos(x,h,z)).getBlock();
                            if(b.material.isReplaceable() || b == Blocks.TNT)
                            {
                                if(lastCanPlaceOn)
                                {
                                    boolean shouldPlaceNext = false;
                                    for (int y = 0; y+h <= floored.getY()+5; y++)
                                    {
                                        if(y % spacing.getValue() == 0 || shouldPlaceNext)
                                        {
                                            shouldPlaceNext = false;

                                            BlockPos place = new BlockPos(x,y+h,z);

                                            if(mc.world.getBlockState(place).getBlock().material.isReplaceable())
                                            {
                                                if(RedStoneCheck(place))
                                                    continue;

                                                BlockInteractUtil.BlockPlaceResult res = BlockInteractUtil.tryPlaceBlock(place, new SpecBlockSwitchItem(Blocks.TNT),true);
                                                if(res != BlockInteractUtil.BlockPlaceResult.FAILED)
                                                {
                                                    if (res == BlockInteractUtil.BlockPlaceResult.PLACED)
                                                    {
                                                        if(render.getValue())
                                                            new FadePos(place, fill, true);
                                                        cooldown = delay.getValue();
                                                    }


                                                    return;
                                                }
                                                else
                                                    shouldPlaceNext = mc.world.getBlockState(place.add(0,1,0)).getBlock() != Blocks.TNT;
                                            }
                                        }
                                    }

                                    break;
                                }
                            }
                            else{
                                lastCanPlaceOn = true;
                            }

                        }
                    }
                }
            }
        }



    }

    ///check if redstone is powering this
    boolean RedStoneCheck(BlockPos p) {
        for (BlockPos s : WorldUtils.getNeightboursBlocks(p)) {
            Block b = mc.world.getBlockState(s).getBlock();
            if(isRedstone(b))
            {
                return true;
            }
            else if(b.material.isSolid()) {
                for (BlockPos sr : WorldUtils.getNeightboursBlocks(s))
                    if(isRedstone(mc.world.getBlockState(sr).getBlock()))
                        return true;
            }

        }
        return false;
    }

    boolean isRedstone(Block b) {

        if(b.material.isReplaceable())
            return false;
        return b == Blocks.REDSTONE_WIRE || b == Blocks.REDSTONE_BLOCK || b == Blocks.REDSTONE_TORCH || b == Blocks.POWERED_REPEATER || b == Blocks.STONE_PRESSURE_PLATE || b == Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE || b == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE || b == Blocks.WOODEN_PRESSURE_PLATE || b == Blocks.LEVER;
    }



}
