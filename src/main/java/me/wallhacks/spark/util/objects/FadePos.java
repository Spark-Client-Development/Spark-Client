package me.wallhacks.spark.util.objects;

import me.wallhacks.spark.Spark;
import net.minecraft.util.math.BlockPos;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;

public class FadePos {
    public BlockPos pos;
    public ColorSetting fill;

    public Timer fadeTimer;
    boolean fading;

    public FadePos(BlockPos pos, ColorSetting fill) {
        this(pos,fill,0,true);
    }
    public FadePos(BlockPos pos, ColorSetting fill,boolean fading) {
        this(pos,fill,0,fading);
    }
    public FadePos(BlockPos pos, ColorSetting fill, int fadeDelay,boolean fading) {
        this.fading = fading;
        this.pos = pos;
        this.fill = fill;
        fadeTimer = new Timer();
        fadeTimer.delay(fadeDelay);
        Spark.fadeManager.add(this);
    }

    public void startFade() {
        fading = true;
    }

    public boolean isFading() {
        return fading;
    }
}
