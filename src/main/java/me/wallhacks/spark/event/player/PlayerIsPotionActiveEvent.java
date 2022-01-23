package me.wallhacks.spark.event.player;

import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class PlayerIsPotionActiveEvent extends Event {

    public Potion potion;

    public PlayerIsPotionActiveEvent(Potion p_Potion)
    {
        super();

        potion = p_Potion;
    }

}
