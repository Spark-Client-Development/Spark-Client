package me.wallhacks.spark.systems.module.modules.render;

import me.wallhacks.spark.event.render.FovModifierEvent;
import me.wallhacks.spark.systems.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.IntSetting;

@Module.Registration(name = "ViewModel", description = "Stop rendering shit we don't want to render", enabled = true)
public class ViewModel extends Module {
    private IntSetting mainFov = new IntSetting("Fov",this,100,60,140,"General");
    private IntSetting itemFov = new IntSetting("ItemFov",this,80,60,140,"General");

    @SubscribeEvent
    public void onEntityViewRenderEvent(FovModifierEvent event) {
        event.setFov(event.getUseSetting() ? mainFov.getValue() : itemFov.getValue());
        event.setCanceled(true);
    }
}
