package me.wallhacks.spark.systems.module.modules.world;

import com.github.lunatrius.core.util.math.BlockPosHelper;
import com.github.lunatrius.core.util.math.MBlockPos;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.util.objects.FadePos;
import me.wallhacks.spark.util.player.BlockInteractUtil;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.SpecBlockSwitchItem;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

@Module.Registration(name = "Printer", description = "Steals from chests")
public class Printer extends Module {


    IntSetting delay = new IntSetting("Delay",this,0,0,8);
    BooleanSetting render = new BooleanSetting("Render", this, true);
    ColorSetting fill = new ColorSetting("Fill", this, new Color(0x389F5EDC, true));


    int cooldown = 0;

    @SubscribeEvent
    void OnUpdate(PlayerUpdateEvent event) {

        if(ClientProxy.schematic == null)
            return;

        if(cooldown > 0)
        {
            cooldown--;
            return;
        }


        final double dX = ClientProxy.playerPosition.x - ClientProxy.schematic.position.x;
        final double dY = ClientProxy.playerPosition.y - ClientProxy.schematic.position.y;
        final double dZ = ClientProxy.playerPosition.z - ClientProxy.schematic.position.z;
        final int x = (int) Math.floor(dX);
        final int y = (int) Math.floor(dY);
        final int z = (int) Math.floor(dZ);
        final int range = 5;

        final int minX = Math.max(0, x - range);
        final int maxX = Math.min(ClientProxy.schematic.getWidth() - 1, x + range);
        int minY = Math.max(0, y - range);
        int maxY = Math.min(ClientProxy.schematic.getHeight() - 1, y + range);
        final int minZ = Math.max(0, z - range);
        final int maxZ = Math.min(ClientProxy.schematic.getLength() - 1, z + range);

        if (minX > maxX || minY > maxY || minZ > maxZ) {
            return;
        }



        for (final MBlockPos p : BlockPosHelper.getAllInBoxXZY(minX, minY, minZ, maxX, maxY, maxZ)) {

            BlockPos worldpos = p.add(ClientProxy.schematic.position);

            if(mc.world.getBlockState(worldpos).getBlock().material.isReplaceable())
            {



                Block b = ClientProxy.schematic.getBlockState(p).getBlock();



                if(b != Blocks.AIR)
                {

                    BlockInteractUtil.BlockPlaceResult res = BlockInteractUtil.tryPlaceBlock(p, new SpecBlockSwitchItem(b), true);
                    if(res != BlockInteractUtil.BlockPlaceResult.FAILED)
                    {
                        if (res == BlockInteractUtil.BlockPlaceResult.PLACED)
                        {
                            if(render.getValue())
                                new FadePos(p, fill, true);
                            cooldown = delay.getValue();
                        }


                        return;
                    }

                }

            }


        }



    }




}
