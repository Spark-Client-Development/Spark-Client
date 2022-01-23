package me.wallhacks.spark.manager;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.client.InputEvent;
import me.wallhacks.spark.util.MC;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class KeyManager implements MC {
    public KeyManager() {
        Spark.eventBus.register(this);
    }

    boolean[] down = new boolean[5];

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity().equals(mc.player))
            if (mc.currentScreen == null) {

                for (int i = 0; i < down.length; i++)
                {
                    if (Mouse.isButtonDown(i)) {
                        if (!down[i]) {
                            onMousePress(-(2+i));
                        }
                        down[i] = true;
                    } else {
                        down[i] = false;
                    }
                }

            }
    }

    private void onMousePress(int button) {
        Spark.eventBus.post(new InputEvent(button));
    }
}
