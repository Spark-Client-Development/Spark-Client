package me.wallhacks.spark.systems.module.modules.player;

import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "AntiRotate", description = "Cancels server rotations")
public class AntiRotate extends Module {
    @SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event) {
        if (nullCheck()) return;
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook p = event.getPacket();
            p.yaw = mc.player.rotationYaw;
            p.pitch = mc.player.rotationPitch;
        }
    }
}
