package me.wallhacks.spark.systems.module.modules.misc;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.event.player.PacketSendEvent;
import me.wallhacks.spark.systems.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;

import java.util.Arrays;

@Module.Registration(name = "PacketLogger", description = "Logs Packets")
public class PacketLogger extends Module {

    ModeSetting mode = new ModeSetting("Mode",this,"client", Arrays.asList("client","server","both"));

    @SubscribeEvent
    public void onServer(PacketReceiveEvent e) {

        if(mc.player == null)
            return;

        if(mode.is("server") || mode.is("both"))
            Spark.sendInfo("[Server] "+e.getPacket().getClass().getName());

    }

    @SubscribeEvent
    public void onClient(PacketSendEvent e) {
        if(mc.player == null)
            return;

        if(mode.is("client") || mode.is("both"))
            Spark.sendInfo("[Client] "+e.getPacket().getClass().getName());

    }
}
