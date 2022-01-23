package me.wallhacks.spark.systems.module.modules.world;

import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;

@Module.Registration(name = "ClientWeather", description = "Client Weather")
public class ClientWeather extends Module {

    public DoubleSetting Rain = new DoubleSetting("Rain", this, 0, 0, 1);
    public DoubleSetting Thunder = new DoubleSetting("Thunder", this, 0, 0, 1);









}
