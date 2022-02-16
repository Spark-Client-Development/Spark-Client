package me.wallhacks.spark.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.objects.Notification;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.hud.huds.Notifications;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PopManager implements MC {

    public PopManager() {
        Spark.eventBus.register(this);
    }

    private Map<EntityPlayer, Integer> popList = new ConcurrentHashMap<>();
    public final Map<EntityPlayer, Notification> toAnnouce = new ConcurrentHashMap<>();

    @SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof SPacketDestroyEntities) {
            SPacketDestroyEntities packetIn = (SPacketDestroyEntities)event.getPacket();
            for(int i = 0; i < packetIn.getEntityIDs().length; ++i) {

                try {
                    Entity entity = mc.world.getEntityByID(packetIn.getEntityIDs()[i]);
                    if (entity instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer)entity;
                        if(player.getHealth() <= 0.0f) {
                            if (!player.equals(mc.player) && Notifications.INSTANCE.death.getValue() && Notifications.INSTANCE.isEnabled()) {
                                Notifications.addNotification(new Notification(getDeathString(player),player.getEntityId()));
                            }
                            resetPops(player);
                        }
                    }
                }
                catch (Exception x)
                {

                }
            }
        }
        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            try {
                if (packet.getOpCode() == 0x23 && packet.getEntity(mc.world) instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) packet.getEntity(mc.world);
                    popTotem(player);
                    if (!player.equals(mc.player) && player.isEntityAlive() && Notifications.INSTANCE.pop.getValue() && Notifications.INSTANCE.isEnabled()) {
                        Notifications.addNotification(new Notification(getPopString(player),player.getEntityId()));
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }


    public void resetPops(EntityPlayer player) {
        this.setTotemPops(player, 0);
    }

    public void popTotem(EntityPlayer player) {
        this.popList.merge(player, 1, Integer::sum);
    }

    public void setTotemPops(EntityPlayer player, int amount) {
        this.popList.put(player, amount);
    }

    public int getTotemPops(EntityPlayer player) {
        return this.popList.get(player) == null ? 0 : this.popList.get(player);
    }

    private String getDeathString(EntityPlayer player) {
        int pops = getTotemPops(player);
        if (Spark.socialManager.isFriend(player.getName())) {
            return "you just let " + ChatFormatting.AQUA + player.getName() + ChatFormatting.RESET + " die after popping "
                    + ChatFormatting.RED + ChatFormatting.BOLD
                    + pops + ChatFormatting.RESET + (pops == 1 ? " totem" : " totems");
        } else {
            return ChatFormatting.RED + player.getName() + ChatFormatting.RESET + " just died after popping "
                    + ChatFormatting.RED + ChatFormatting.BOLD
                    + pops + ChatFormatting.RESET + (pops == 1 ? " totem" : " totems");
        }
    }

    private String getPopString(EntityPlayer player) {
        int pops = getTotemPops(player);
        if (Spark.socialManager.isFriend(player.getName())) {
            return "ur friend " + ChatFormatting.AQUA + player.getName() + ChatFormatting.RESET + " has now popped "
                    + ChatFormatting.RED + ChatFormatting.BOLD
                    + pops + ChatFormatting.RESET + (pops == 1 ? " totem" : " totems") + " go help them";
        } else {
            return ChatFormatting.RED + player.getName() + ChatFormatting.RESET + " has now popped "
                    + ChatFormatting.RED + ChatFormatting.BOLD
                    + pops + ChatFormatting.RESET + (pops == 1 ? " totem" : " totems");
        }
    }

}

