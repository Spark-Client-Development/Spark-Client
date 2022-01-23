package me.wallhacks.spark.event.player;

import net.minecraft.entity.Entity;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;


public class ChunkLoadEvent extends Event {



    @Cancelable
    public static class Load extends Event {
        public Load(Chunk chunk) {
            this.chunk = chunk;
        }
        Chunk chunk;


        public Chunk getChunk() {
            return chunk;
        }
    }

    public static class Unload extends Event {

        public Unload(Chunk chunk) {
            this.chunk = chunk;
        }
        Chunk chunk;


        public Chunk getChunk() {
            return chunk;
        }
    }

}
