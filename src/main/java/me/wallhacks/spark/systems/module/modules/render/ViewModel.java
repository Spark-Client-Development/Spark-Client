package me.wallhacks.spark.systems.module.modules.render;

import me.wallhacks.spark.event.render.FovModifierEvent;
import me.wallhacks.spark.systems.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.IntSetting;

@Module.Registration(name = "ViewModel", description = "Change your fov without default limits")
public class ViewModel extends Module {
    private IntSetting mainFov = new IntSetting("Fov",this,100,60,140);
    private IntSetting itemFov = new IntSetting("ItemFov",this,80,60,140);

    @SubscribeEvent
    public void onEntityViewRenderEvent(FovModifierEvent event) {
        event.setFov(event.getUseSetting() ? mainFov.getValue() : itemFov.getValue());
        event.setCanceled(true);
    }
}
