package me.wallhacks.spark.systems.autoconfigs.configs;

import me.wallhacks.spark.manager.SystemManager;
import me.wallhacks.spark.systems.autoconfigs.AutoConfig;
import me.wallhacks.spark.systems.module.modules.movement.Velocity;
import me.wallhacks.spark.systems.module.modules.player.PortalChat;
import me.wallhacks.spark.systems.module.modules.render.NoRender;
import me.wallhacks.spark.systems.module.modules.render.ViewModel;

public class BaseSetup extends AutoConfig {

    public BaseSetup() {
        super("Essentials");
    }
    public BaseSetup(String name) {
        super(name);
    }

    public void config() {
        super.config();

        SystemManager.getModule(Velocity.class).enable();
        SystemManager.getModule(NoRender.class).enable();
        SystemManager.getModule(ViewModel.class).enable();
        SystemManager.getModule(PortalChat.class).enable();



    }


}
