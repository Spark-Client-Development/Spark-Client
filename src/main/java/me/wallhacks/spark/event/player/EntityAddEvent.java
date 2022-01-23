package me.wallhacks.spark.event.player;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class EntityAddEvent extends Event {

    public EntityAddEvent(Entity p){
        this.p = p;
    }

    final Entity p;
    
    @SuppressWarnings("unchecked")
	public Entity getEntity() {
        return p;
    }
}
