package me.wallhacks.spark.systems.module.modules.render;

import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;

@Module.Registration(name = "Fullbright", description = "Adds light")
public class Fullbright extends Module {

    @Override
    public void onEnable() {
        MC.mc.gameSettings.gammaSetting = 100f;
    }

    @Override
    public void onDisable() {
        MC.mc.gameSettings.gammaSetting = 1f;
    }
}
