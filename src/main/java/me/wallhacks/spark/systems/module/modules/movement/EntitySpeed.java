package me.wallhacks.spark.systems.module.modules.movement;

import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "EntitySpeed", description = "Go fast on entity brrrr")
public class EntitySpeed extends Module {
    DoubleSetting speed = new DoubleSetting("Speed", this, 1.5, 0.1, 10.0);
    BooleanSetting bypass = new BooleanSetting("Bypass", this, false);
    BooleanSetting dev = new BooleanSetting("Dev", this, false);
    double currSpeed = 0;
    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        if (mc.player.ridingEntity != null) {
            Entity riding = mc.player.ridingEntity;

            double forward = mc.player.movementInput.moveForward;
            double strafe = mc.player.movementInput.moveStrafe;
            float yaw = mc.player.rotationYaw;
            if (dev.getValue()) {
                boolean x = (riding.posX >= 0);
                boolean z = (riding.posZ >= 0);
                boolean s = Math.abs(Math.abs(riding.posZ) - Math.abs(riding.posX)) < 1.5;
                if (s) {
                    if (x == z) {
                        riding.posX = riding.posZ;
                    } else {
                        riding.posX = riding.posZ*-1;
                    }
                }
                riding.boundingBox = new AxisAlignedBB(riding.posX, riding.boundingBox.minY, riding.posZ, riding.posX, riding.boundingBox.minY, riding.posZ);
                yaw = Math.round((yaw + 1.0f) / 45.0f) * 45.0f;
            }

            riding.rotationYaw = yaw;
            final boolean movingForward = forward != 0.0;
            final boolean movingStrafe = strafe != 0.0;
            if (bypass.getValue() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
                mc.player.connection.sendPacket(new CPacketUseEntity(riding, EnumHand.MAIN_HAND));
            }
            if (!movingForward && !movingStrafe) {
                riding.motionX = 0.0;
                riding.motionZ = 0.0;
                currSpeed = 40;
            } else {
                if (forward != 0.0) {
                    if (strafe > 0.0) {
                        yaw += ((forward > 0.0) ? -45 : 45);
                    } else if (strafe < 0.0) {
                        yaw += ((forward > 0.0) ? 45 : -45);
                    }
                    strafe = 0.0;
                    forward = ((forward > 0.0) ? 1.0 : -1.0);
                }
                if (currSpeed != 80)
                    currSpeed++;
                double speed = this.speed.getValue() * (currSpeed/80);
                double cos = Math.cos(Math.toRadians(yaw + 90.0f));
                double sin = Math.sin(Math.toRadians(yaw + 90.0f));
                riding.motionX = forward * speed * cos + strafe * speed * sin;
                riding.motionZ = forward * speed * sin - strafe * speed * cos;
            }
        } else {
            currSpeed = 40;
        }
    }

    @SubscribeEvent
    public void OnPacketReceive(PacketReceiveEvent event) {
        Packet packet = event.getPacket();
        if (!bypass.getValue() || mc.gameSettings.keyBindSneak.isKeyDown() || mc.currentScreen instanceof GuiDownloadTerrain) return;
        if (packet instanceof SPacketPlayerPosLook) {
            event.setCanceled(true);
        } else if (packet instanceof SPacketSetPassengers && mc.player.ridingEntity != null) event.setCanceled(true);
    }

    @Override
    public void onEnable() {
        currSpeed = 40;
    }

    @Override
    public void onDisable() {
        if (mc.player.ridingEntity != null) mc.player.ridingEntity.noClip = false;
    }
}
