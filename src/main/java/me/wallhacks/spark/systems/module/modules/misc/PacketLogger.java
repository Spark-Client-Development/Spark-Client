package me.wallhacks.spark.systems.module.modules.misc;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.event.player.PacketSendEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import net.minecraft.network.play.client.CPacketPlaceRecipe;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;

@Module.Registration(name = "PacketLogger", description = "Logs Packets")
public class PacketLogger extends Module {

    ModeSetting mode = new ModeSetting("Mode",this,"client", Arrays.asList("client","server","both"));
    BooleanSetting pos = new BooleanSetting("PosPacks",this,true);
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onServer(PacketReceiveEvent e) {
        if(mc.player == null)
            return;
        if (e.getPacket().getClass().getName().contains("CPacket")) return;
        if(mode.is("server") || mode.is("both"))
            Spark.sendInfo("[Server] "+e.getPacket().getClass().getName());

    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClient(PacketSendEvent e) {
        if(mc.player == null)
            return;

        if (e.getPacket().getClass().getName().contains("SPacket")) return;
        if (e.getPacket() instanceof CPacketPlaceRecipe) Spark.logger.info(((CPacketPlaceRecipe) e.getPacket()).func_194318_a());
        if(mode.is("client") || mode.is("both"))
        {
            if(e.getPacket() instanceof CPacketPlayer && !pos.isOn())
                return;
            else if(e.getPacket() instanceof CPacketPlayerTryUseItemOnBlock)
            {
                Spark.sendInfo("[Client] "+e.getPacket().getClass().getName()+" "+((CPacketPlayerTryUseItemOnBlock)(e.getPacket())).getPos()
                        +" "+((CPacketPlayerTryUseItemOnBlock)(e.getPacket())).getDirection()+" "+((CPacketPlayerTryUseItemOnBlock)(e.getPacket())).getHand()+" "+((CPacketPlayerTryUseItemOnBlock)(e.getPacket())).getFacingX()+" "+((CPacketPlayerTryUseItemOnBlock)(e.getPacket())).getFacingY()+" "+((CPacketPlayerTryUseItemOnBlock)(e.getPacket())).getFacingZ());
            }
            else
                Spark.sendInfo("[Client] "+e.getPacket().getClass().getName());
        }
    }
}
