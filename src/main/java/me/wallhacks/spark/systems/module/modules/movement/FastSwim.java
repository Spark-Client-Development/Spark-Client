package me.wallhacks.spark.systems.module.modules.movement;

import me.wallhacks.spark.event.player.PacketSendEvent;
import me.wallhacks.spark.event.player.PlayerMoveEvent;
import me.wallhacks.spark.event.player.SneakEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "FastSwim", description = "Swim faster")
public class FastSwim extends Module {
    public static FastSwim INSTANCE;
    public DoubleSetting waterSpeed = new DoubleSetting("WaterSpeed", this, 1.35, 1, 3);
    public DoubleSetting lavaSpeed = new DoubleSetting("LavaSpeed", this, 4.8, 1, 6);
    BooleanSetting bypass = new BooleanSetting("Bypass", this, true);
    boolean flag = false;

    public FastSwim() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onMove(PlayerMoveEvent event) {
        if ((mc.player.isInWater() || mc.player.isInLava()) && !mc.player.onGround) {
            boolean jump = mc.player.movementInput.jump;
            boolean sneak = mc.player.movementInput.sneak;
            mc.player.setSprinting(true);
            if (jump && !sneak) {
                event.setY(0.11);
                flag = false;
            } else if (!jump && sneak) {
                mc.player.setSneaking(false);
                flag = false;
            } else {
                event.setY(0);
                if (bypass.getValue())
                    flag = !flag;
                else flag = false;
            }
            if (mc.player.isInWater()) {
                event.setX(event.getX() * waterSpeed.getFloatValue());
                event.setZ(event.getZ() * waterSpeed.getFloatValue());
            } else {
                event.setX(event.getX() * lavaSpeed.getFloatValue());
                event.setZ(event.getZ() * lavaSpeed.getFloatValue());
            }
        }
    }

    @SubscribeEvent
    public void onSneak(SneakEvent event) {
        if ((mc.player.isInWater() || mc.player.isInLava()) && !mc.player.onGround) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacket() instanceof CPacketPlayer.Position || event.getPacket() instanceof CPacketPlayer.PositionRotation) {
            if ((mc.player.isInWater() || mc.player.isInLava()) && !mc.player.onGround)
                if (flag) ((CPacketPlayer) event.getPacket()).y -= 0.005;
        }
    }
}
