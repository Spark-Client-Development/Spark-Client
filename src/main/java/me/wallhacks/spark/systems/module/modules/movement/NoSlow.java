package me.wallhacks.spark.systems.module.modules.movement;


import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;

import java.util.Arrays;

@Module.Registration(name = "NoSlow", description = "Don't get slowed down by using items")
public class NoSlow extends Module {
    public ModeSetting webs = new ModeSetting("Mode", this, "Off", Arrays.asList("Off", "Vanilla", "Fast"), "Webs");
    public DoubleSetting webSpeed = new DoubleSetting("Speed", this, 0.5, 0.25, 1.0, "Webs");
    public DoubleSetting webSpeedY = new DoubleSetting("YSpeed", this, 0.8, 0.05, 1.0, "Webs");
    public static NoSlow INSTANCE;
    public NoSlow() {
        INSTANCE = this;
    }
    @SubscribeEvent
    public void onInput(InputUpdateEvent event){
        if(MC.mc.player.isHandActive() && !MC.mc.player.isRiding()) {
            MC.mc.player.movementInput.moveForward /= 0.2;
            MC.mc.player.movementInput.moveStrafe /= 0.2;
        }
    }
}
