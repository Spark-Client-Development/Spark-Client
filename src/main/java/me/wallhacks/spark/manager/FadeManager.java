package me.wallhacks.spark.manager;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.objects.FadePos;
import me.wallhacks.spark.util.render.EspUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class FadeManager {
    HashMap<BlockPos, FadePos> positions = new HashMap<>();

    public Collection<FadePos> getPositions() {
        return positions.values();
    }
    public void add(FadePos pos) {
        positions.put(pos.pos,pos);
    }

    public FadeManager() {
        Spark.eventBus.register(this);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        ArrayList<BlockPos> remove = new ArrayList<>();
        int maxTime = (int) (ClientConfig.getInstance().getFadeTime() * 1000);
        for (FadePos pos : getPositions()) {
            int current = (int) pos.fadeTimer.getPassedTimeMs();
            if (current > maxTime) {
                remove.add(pos.pos);
                continue;
            }
            Color outline;
            Color fill;
            if (pos.isFading()) {
                double percent = Math.min((double) (maxTime - current) / maxTime,1);
                outline = new Color(pos.outline.getColor().getRed(), pos.outline.getColor().getGreen(), pos.outline.getColor().getBlue(), (int) (pos.outline.getColor().getAlpha() * percent));
                fill = new Color(pos.fill.getColor().getRed(), pos.fill.getColor().getGreen(), pos.fill.getColor().getBlue(), (int) (pos.fill.getColor().getAlpha() * percent));
            } else {
                fill = pos.fill.getColor();
                outline = pos.outline.getColor();
                pos.fadeTimer.reset();
            }
            EspUtil.drawBox(pos.pos, fill);
            EspUtil.drawOutline(pos.pos, outline);
        }
        for (BlockPos r : remove) {
            positions.remove(r);
        }

    }

   
}
