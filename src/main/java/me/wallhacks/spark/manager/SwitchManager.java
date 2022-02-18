package me.wallhacks.spark.manager;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.event.player.UpdateWalkingPlayerEvent;
import me.wallhacks.spark.event.render.RenderEntityEvent;
import me.wallhacks.spark.util.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SwitchManager implements MC {
    public SwitchManager() {
        Spark.eventBus.register(this);
    }

    int doSwitchTo = 4;
    int switchToSlot = 0;
    int fromSlot = 0;

    public void setDoSwitchToSlot(int switchToSlot,int delay){
        this.doSwitchTo = delay;
        this.switchToSlot = switchToSlot;
        this.fromSlot = mc.player.inventory.currentItem;
    }

    @SubscribeEvent
    void OnUpdate(PlayerUpdateEvent event) {

        if(doSwitchTo >= 0)
        {
            if(doSwitchTo == 0 && fromSlot == mc.player.inventory.currentItem)
            {
                mc.player.inventory.currentItem = switchToSlot;
                mc.playerController.syncCurrentPlayItem();
            }
            doSwitchTo--;

        }
    }




}
