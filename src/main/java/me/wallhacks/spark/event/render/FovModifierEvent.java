package me.wallhacks.spark.event.render;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class FovModifierEvent extends Event {

    private final boolean useSetting;

    private float fov;

    public FovModifierEvent(boolean useSetting) {
        this.useSetting = useSetting;
    }

    public boolean getUseSetting() {
        return useSetting;
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov)
    {
        this.fov = fov;
    }
}
