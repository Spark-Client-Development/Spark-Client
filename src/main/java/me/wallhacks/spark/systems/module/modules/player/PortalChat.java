package me.wallhacks.spark.systems.module.modules.player;

import me.wallhacks.spark.event.player.PlayerLivingTickEvent;
import me.wallhacks.spark.systems.module.Module;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "PortalChat", description = "Use chat in portals")
public class PortalChat extends Module {
    public static PortalChat INSTANCE;
    public PortalChat() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onUpdate(PlayerLivingTickEvent event) {
        if(mc.player.inPortal)
        {
            if (mc.player.timeInPortal == 0.0F) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_PORTAL_TRIGGER, mc.player.rand.nextFloat() * 0.4F + 0.8F));
            }

            mc.player.timeInPortal += 0.0125F;
            if (mc.player.timeInPortal >= 1.0F) {
                mc.player.timeInPortal = 1.0F;
            }
        }
        mc.player.inPortal = false;
    }
}
