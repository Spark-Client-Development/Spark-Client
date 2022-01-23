package me.wallhacks.spark.event.render;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class RenderEntityEvent extends Event {


    public static class Pre extends Event {
        private final Entity entity;


        public Pre(Entity entity) {
            this.entity = entity;

        }

        public Entity getEntity() {
            return entity;
        }


    }

    public static class Post extends Event {
        private final Entity entity;


        public Post(Entity entity) {
            this.entity = entity;

        }

        public Entity getEntity() {
            return entity;
        }


    }

    public enum Type {
        TEXTURE,
        COLOR
    }

}
