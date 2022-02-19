package me.wallhacks.spark.systems.module.modules.world;

import com.github.lunatrius.core.util.math.BlockPosHelper;
import com.github.lunatrius.core.util.math.MBlockPos;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.util.WorldUtils;
import me.wallhacks.spark.util.objects.FadePos;
import me.wallhacks.spark.util.player.BlockInteractUtil;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.SpecBlockSwitchItem;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

@Module.Registration(name = "GriefHelper", description = "Places tnt")
public class GriefHelper extends Module {

    IntSetting spacing = new IntSetting("Spacing",this,4,2,5,"General");
    IntSetting delay = new IntSetting("Delay",this,0,0,8,"General");
    BooleanSetting render = new BooleanSetting("Render", this, true, "General");
    ColorSetting fill = new ColorSetting("Fill", this, new Color(0x389F5EDC, true), "General");


    int cooldown = 0;

    @SubscribeEvent
    void OnUpdate(PlayerUpdateEvent event) {


        if(cooldown > 0)
        {
            cooldown--;
            return;
        }


        for (final BlockPos p : WorldUtils.getSphere(PlayerUtil.getPlayerPosFloored(mc.player), 5, 3, 1)) {

            if(p.getX() % spacing.getValue() == 0 && p.getZ() % spacing.getValue() == 0)
            if(mc.world.getBlockState(p).getBlock() == Blocks.AIR)
            {
                Block b = mc.world.getBlockState(p.add(0,-1,0)).getBlock();

                if(b.material.isSolid() && b != Blocks.TNT)
                {
                    BlockInteractUtil.BlockPlaceResult res = BlockInteractUtil.tryPlaceBlock(p, new SpecBlockSwitchItem(Blocks.TNT), true, true, 4, false);

                    if(res != BlockInteractUtil.BlockPlaceResult.FAILED)
                    {
                        if (res == BlockInteractUtil.BlockPlaceResult.PLACED)
                        {
                            if(render.getValue())
                                cooldown = delay.getValue();
                            new FadePos(p, fill, true);
                        }


                        return;
                    }

                }

            }


        }



    }




}
