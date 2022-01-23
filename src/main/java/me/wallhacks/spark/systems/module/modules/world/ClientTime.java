package me.wallhacks.spark.systems.module.modules.world;

import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.IntSetting;

@Module.Registration(name = "ClientTime", description = "Client Weather")
public class ClientTime extends Module {

    IntSetting Time = new IntSetting("Time", this, 20, 0, 24);


    public long getTime() {
        int h = Time.getValue();
        //morning
        h-=6;

        if(h < 0)
            h = 24 + h;



        return h*1000;
    }
}
