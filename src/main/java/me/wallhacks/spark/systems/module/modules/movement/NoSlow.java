package me.wallhacks.spark.systems.module.modules.movement;


import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.SettingGroup;
import me.wallhacks.spark.util.MC;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;

import java.util.Arrays;

@Module.Registration(name = "NoSlow", description = "Don't get slowed down by using items")
public class NoSlow extends Module {
    SettingGroup websG = new SettingGroup("Webs", this);
    public ModeSetting webs = new ModeSetting("WebMode", websG, "Off", Arrays.asList("Off", "Vanilla", "Fast"));
    public DoubleSetting webSpeed = new DoubleSetting("WebSpeed", websG, 0.5, 0.25, 1.0);
    public DoubleSetting webSpeedY = new DoubleSetting("WebYSpeed", websG, 0.8, 0.05, 1.0);
    public static NoSlow INSTANCE;
    public NoSlow() {
        INSTANCE = this;
    }
    @SubscribeEvent
    public void onInput(InputUpdateEvent event){
        if(mc.player.isHandActive() && !mc.player.isRiding()) {
            mc.player.movementInput.moveForward /= 0.2;
            mc.player.movementInput.moveStrafe /= 0.2;
        }
    }
}
