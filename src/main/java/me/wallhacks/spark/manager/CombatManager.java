package me.wallhacks.spark.manager;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.event.world.WorldLoadEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.module.modules.combat.CevBreaker;
import me.wallhacks.spark.systems.module.modules.combat.CrystalAura;
import me.wallhacks.spark.systems.module.modules.combat.KillAura;
import me.wallhacks.spark.systems.module.modules.combat.ShulkerAura;
import me.wallhacks.spark.systems.module.modules.misc.AutoGG;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.StringUtil;
import me.wallhacks.spark.util.objects.Notification;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.hud.huds.Notifications;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CombatManager implements MC {

    public CombatManager() {
        Spark.eventBus.register(this);
    }

    private Map<String, Integer> popList = new ConcurrentHashMap<>();
    private ArrayList<Kill> kills = new ArrayList<>();

    String lastServer;
    @SubscribeEvent
    public void onWorld(WorldLoadEvent event) {
        String server = StringUtil.getServerName(mc.getCurrentServerData());
        if(server != null && !server.equals(lastServer))
        {
            popList.clear();
            kills.clear();
            lastServer = server;
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            try {
                if (packet.getOpCode() == 35 && packet.getEntity(mc.world) instanceof EntityPlayer) {
                    handlePop((EntityPlayer) packet.getEntity(mc.world));
                }
                if (packet.getOpCode() == 3 && packet.getEntity(mc.world) instanceof EntityPlayer) {
                    handleDeath((EntityPlayer) packet.getEntity(mc.world));
                }
            } catch (Exception ignored) {
            }
        }
    }




    void handleDeath(EntityPlayer player) {

        int diedAfterPops = getTotemPops(player);


        //detect if we killed him
        if(mc.player != player)
        {
            if (Notifications.INSTANCE.death.getValue() && Notifications.INSTANCE.isEnabled())
                Notifications.addNotification(new Notification(StringUtil.getDeathString(player,diedAfterPops),player));


            Module killedWith = null;

            if(player.equals(CrystalAura.instance.getTarget()))
                killedWith = CrystalAura.instance;
            else if(player.equals(KillAura.instance.getTarget()))
                killedWith = KillAura.instance;
            else if(CevBreaker.INSTANCE.isInAttackZone(player))
                killedWith = CevBreaker.INSTANCE;
            else if(ShulkerAura.INSTANCE.isInAttackZone(player))
                killedWith = ShulkerAura.INSTANCE;

            if(killedWith != null)
            {
                if(AutoGG.instance.isEnabled())
                    AutoGG.instance.onKilledPlayer(player,diedAfterPops);
                //looks like we killed it :(
                kills.add(new Kill(Spark.socialManager.getSocial(player.getName()),killedWith));
            }
        }


        this.setTotemPops(player, 0);
    }
    void handlePop(EntityPlayer player) {
        this.popList.merge(player.getName(), 1, Integer::sum);
        if (!player.equals(mc.player) && player.isEntityAlive() && Notifications.INSTANCE.pop.getValue() && Notifications.INSTANCE.isEnabled()) {
            Notifications.addNotification(new Notification(StringUtil.getPopString(player,getTotemPops(player)),player));
        }
    }






    void setTotemPops(EntityPlayer player, int amount) {
        this.popList.put(player.getName(), amount);
    }

    public int getTotemPops(EntityPlayer player) {
        return this.popList.containsKey(player.getName()) ? this.popList.get(player.getName()) : 0;
    }



    public class Kill{
        public Kill(SocialManager.SocialEntry playerKilled, Module killedWith) {
            this.playerKilled = playerKilled;
            this.killedWith = killedWith;
        }

        public SocialManager.SocialEntry getPlayerKilled() {
            return playerKilled;
        }

        public Module getKilledWith() {
            return killedWith;
        }

        final SocialManager.SocialEntry playerKilled;
        final Module killedWith;
    }
}

