package me.wallhacks.spark.util.render;

import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class ColorGradient {

    final Color[] colors;

    public ColorGradient(Color[] colors) {
        this.colors = colors;
    }

    public Color getColor(float percent) {

        float index = MathHelper.clamp(percent,0,1) *(colors.length-1);

        int c1Index = (int)Math.floor(index);
        int c2Index = (int)Math.ceil(index);

        Color c1 = colors[c1Index];
        Color c2 = colors[c2Index];

        return ColorUtil.lerpColor(c1,c2,index-c1Index);

    }


}
