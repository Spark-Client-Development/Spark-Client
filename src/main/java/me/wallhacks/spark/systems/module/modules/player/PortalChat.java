package me.wallhacks.spark.systems.module.modules.player;

import me.wallhacks.spark.systems.module.Module;

@Module.Registration(name = "PortalChat", description = "Use chat in portals")
public class PortalChat extends Module {
    public static PortalChat INSTANCE;
    public PortalChat() {
        INSTANCE = this;
    }
}
