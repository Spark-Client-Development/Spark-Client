package me.wallhacks.spark.event.client;

import net.minecraftforge.fml.common.eventhandler.Event;

public class InputEvent extends Event {
    private int key;

    public InputEvent(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
