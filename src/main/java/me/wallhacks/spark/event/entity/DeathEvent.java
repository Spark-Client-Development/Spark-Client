package me.wallhacks.spark.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

public class DeathEvent extends Event {
    Type type;
    EntityPlayer entity;
    public DeathEvent(EntityPlayer entity, Type type) {
        this.entity = entity;
        this.type = type;
    }
    public enum Type {
        TOTEMPOP,
        DEATH
    }

    public EntityPlayer getEntity() {
        return entity;
    }

    public Type getType() {
        return type;
    }
}
