package me.wallhacks.spark.event.render;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class SkyEvent extends Event {


    private float partialTicks;

    public SkyEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }


    public float getPartialTicks() {
        return partialTicks;
    }
}
