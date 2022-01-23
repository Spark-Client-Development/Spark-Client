package me.wallhacks.spark.event.player;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;


public class UpdateWalkingPlayerEvent extends Event {

    @Cancelable
    public static class Pre extends Event {}

    public static class Post extends Event {}

}
