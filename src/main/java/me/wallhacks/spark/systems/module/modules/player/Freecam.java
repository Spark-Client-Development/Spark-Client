package me.wallhacks.spark.systems.module.modules.player;

import me.wallhacks.spark.event.client.SettingChangeEvent;
import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.event.player.PacketSendEvent;
import me.wallhacks.spark.event.player.SneakEvent;
import me.wallhacks.spark.event.render.OpaqueCubeEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.util.objects.FreecamEntity;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

@Module.Registration(name = "Freecam", description = "Allows you to move the camara freely")
public class Freecam extends Module {
    public static Freecam INSTANCE;
    public DoubleSetting speed = new DoubleSetting("Speed", this, 0.5, 0.1, 5.0);
    public DoubleSetting vSpeed = new DoubleSetting("VerticalSpeed", this, 0.7, 0.1, 2.0);
    private int old3rdPMode;
    private FreecamEntity camera = null;
    public Freecam() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        if (mc.world == null)
            return;

        this.old3rdPMode = mc.gameSettings.thirdPersonView;
        this.camera = new FreecamEntity(mc);
        this.camera.setMoveSpeed(speed.getFloatValue(), vSpeed.getFloatValue());
        this.camera.enableCamera();
    }

    @Override
    public void onDisable() {
        if (mc.world == null || camera == null)
            return;

        this.camera.disableCamera();
        this.camera = null;
        mc.gameSettings.thirdPersonView = this.old3rdPMode;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.player == null) {
            this.disable();
            return;
        }
        if (this.camera != null) {
            this.camera.movementTick(event.phase);
        }
    }

    @SubscribeEvent
    public void onLogout(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        this.disable();
    }

    @SubscribeEvent
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacket() instanceof CPacketUseEntity) {
            CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();

            if (packet.getEntityFromWorld(mc.world) == mc.player)
                event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onCubeRender(OpaqueCubeEvent event) {
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof SPacketRespawn) {
            disable();
        }
    }

    @SubscribeEvent
    public void onSneakEvent(SneakEvent event) {
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onSettingChange(SettingChangeEvent event) {
        if (this.camera != null && (event.getSetting() == speed || event.getSetting() == vSpeed)) {
            this.camera.setMoveSpeed(speed.getFloatValue(), vSpeed.getFloatValue());
        }
    }

    public FreecamEntity getCamera() {
        return this.camera;
    }
}
