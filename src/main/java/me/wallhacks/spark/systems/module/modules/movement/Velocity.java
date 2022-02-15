package me.wallhacks.spark.systems.module.modules.movement;

import me.wallhacks.spark.event.entity.LiquidPushEvent;
import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.systems.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;

@Module.Registration(name = "Velocity", description = "No velocity applied by server")
public class Velocity extends Module {

    DoubleSetting Horizontal = new DoubleSetting("Horizontal", this, 0, 0, 1,0.1);
    DoubleSetting Vertical = new DoubleSetting("Vertical", this, 0, 0, 1,0.1);

    public BooleanSetting NoPush = new BooleanSetting("NoPush", this, true);
    public BooleanSetting noWater = new BooleanSetting("NoWater", this, true);
    public static Velocity INSTANCE;
    public Velocity() {INSTANCE = this;}

    @SubscribeEvent
    public void onLiquidPush(LiquidPushEvent event) {
        if (noWater.getValue() && event.getEntity() == mc.player) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onPacketGet(PacketReceiveEvent event) {
        if (nullCheck()) return;
        Packet<?> p = event.getPacket();


        if(p instanceof SPacketExplosion && (!Speed.INSTANCE.isEnabled() || !Speed.INSTANCE.boost.getValue())) {
            SPacketExplosion ex = (SPacketExplosion)p;
            ex.motionX = (float) (Horizontal.getValue() * ex.motionX);
            ex.motionY = (float) (Vertical.getValue() * ex.motionY);
            ex.motionZ = (float) (Horizontal.getValue() * ex.motionZ);
        }
        if(p instanceof SPacketEntityVelocity) {
            SPacketEntityVelocity s = (SPacketEntityVelocity)p;
            //prevents arrayindex out of bound exception error
            if(s.entityID == mc.player.getEntityId()) {
                float changeX = (float) (s.motionX / 8000.0D - mc.player.motionX);
                float changeY = (float) (s.motionY / 8000.0D - mc.player.motionY);
                float changeZ = (float) (s.motionZ / 8000.0D - mc.player.motionZ);


                s.motionX = (int) ((mc.player.motionX + changeX*Horizontal.getValue()) * 8000.0D);
                s.motionY = (int) ((mc.player.motionY + changeY*Vertical.getValue()) * 8000.0D);
                s.motionZ = (int) ((mc.player.motionZ + changeZ*Horizontal.getValue()) * 8000.0D);

            }

        }
        if (event.getPacket() instanceof SPacketEntityStatus)
        {
            final SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if (packet.getOpCode() == 31)
            {
                final Entity entity = packet.getEntity(Minecraft.getMinecraft().world);
                if (entity != null && entity instanceof EntityFishHook)
                {
                    final EntityFishHook fishHook = (EntityFishHook) entity;
                    if (fishHook.caughtEntity == Minecraft.getMinecraft().player)
                    {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPacketGet(PlayerSPPushOutOfBlocksEvent event) {
        if(NoPush.isOn())
            event.setCanceled(true);
    }
}
