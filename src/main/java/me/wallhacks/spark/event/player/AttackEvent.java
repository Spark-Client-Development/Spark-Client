package me.wallhacks.spark.event.player;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;


public class AttackEvent extends Event {



    @Cancelable
    public static class Pre extends Event {
        public Pre(Entity attack) {
            this.attack = attack;
        }
        Entity attack;


        public Entity getAttack() {
            return attack;
        }
    }

    public static class Post extends Event {

        public Post(Entity attack) {
            this.attack = attack;
        }
        Entity attack;


        public Entity getAttack() {
            return attack;
        }
    }

}
