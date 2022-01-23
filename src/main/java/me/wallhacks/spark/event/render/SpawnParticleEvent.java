package me.wallhacks.spark.event.render;

import net.minecraft.client.particle.Particle;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class SpawnParticleEvent extends Event {

    Particle particle;

    public SpawnParticleEvent(Particle particle) {
        this.particle = particle;

    }

    public Particle getParticle() {
        return particle;
    }
}
