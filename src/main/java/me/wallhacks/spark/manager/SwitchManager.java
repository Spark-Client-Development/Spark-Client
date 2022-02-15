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

    boolean doSwitchTo = false;
    int switchToSlot = 0;

    public void setDoSwitchToSlot(int switchToSlot){
        this.doSwitchTo = true;
        this.switchToSlot = switchToSlot;
    }

    @SubscribeEvent
    void OnUpdate(PlayerUpdateEvent event) {

        if(doSwitchTo)
        {
            doSwitchTo = false;
            mc.player.inventory.currentItem = switchToSlot;
            mc.playerController.syncCurrentPlayItem();
        }
    }


}
