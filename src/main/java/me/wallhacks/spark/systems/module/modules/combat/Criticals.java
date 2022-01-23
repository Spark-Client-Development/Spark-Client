package me.wallhacks.spark.systems.module.modules.combat;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.AttackEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;

import java.util.Arrays;

@Module.Registration(name = "Criticals", description = "Superior module litarly best ever")
public class Criticals extends Module {

    ModeSetting reverse = new ModeSetting("Mode",this,"Up", Arrays.asList("Reverse","ReverseCons","Up"));


    @SubscribeEvent
    void OnUpdateWalkingEvent(AttackEvent.Pre event) {

        if(event.getAttack() instanceof EntityLivingBase && MC.mc.player.onGround)
        {
            if(reverse.isValueName("ReverseCons") || reverse.isValueName("Reverse")){
                sendPlayerPos( - 1e-10,false);
            }
            else if (reverse.isValueName("Up")) {
                sendPlayerPos( + 0.1,false);
                sendPlayerPos( 0,false);
                Spark.rotationManager.setCancelNextWalkingUpdate();
            }
        }

    }
    @SubscribeEvent
    void OnUpdateWalkingEvent(AttackEvent.Post event) {

        if(event.getAttack() instanceof EntityLivingBase && MC.mc.player.onGround)
        {
            if(reverse.isValueName("ReverseCons")){
                sendPlayerPos(0,true);
                Spark.rotationManager.setCancelNextWalkingUpdate();
            }

        }

    }

    private void sendPlayerPos(double OffsetY, boolean onGround)
    {
        MC.mc.player.connection.sendPacket(new CPacketPlayer.Position(MC.mc.player.posX, MC.mc.player.posY+OffsetY, MC.mc.player.posZ, onGround));
    }
}
