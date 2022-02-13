package me.wallhacks.spark.systems.module.modules.player;

import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.player.BlockInteractUtil;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.player.itemswitcher.itemswitchers.ItemStopFall;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;

@Module.Registration(name = "NoFall", description = "Prevents death from falling :D")
public class NoFall extends Module {

    ModeSetting Mode = new ModeSetting("Mode", this, "Packet", Arrays.asList("Packet","Dream","Rubberband"),"General");

    BooleanSetting AntiElytraGlitch = new BooleanSetting("AntiElytraGlitch", this, true,v -> Mode.isValueName("Packet"),"General");





    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        if(!mc.player.onGround && mc.player.fallDistance > 3){
            BlockPos p = PlayerUtil.getPlayerPosFloored(mc.player);
            if(Mode.isValueName("Packet") && (!AntiElytraGlitch.isOn() || !mc.player.isElytraFlying() || mc.world.getBlockState(p.add(0, -3, 0)).getBlock() != Blocks.AIR))
            {
                mc.player.connection.sendPacket(new CPacketPlayer(true));
            }
            else if(Mode.isValueName("Rubberband")){
                mc.player.motionY = -0.1;
            }
            else if(Mode.isValueName("Dream")){

                for (int h = 1; h < 5; h++)
                {
                    Block b = mc.world.getBlockState(p.add(0, -h, 0)).getBlock();
                    if(b.material.isSolid() && b != Blocks.HAY_BLOCK && b != Blocks.WEB) {
                        BlockInteractUtil.tryPlaceBlock(p.add(0, 1-h, 0), new ItemStopFall(), false, false, 4, false);
                        break;
                    }
                }


            }
        }

    }




}
