package me.wallhacks.spark.util.render;

import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import static java.awt.Color.RGBtoHSB;

public class ColorUtil {
    public static TextFormatting generateTextColor(String seed) {
        int i = 0;

        TextFormatting[] array = TextFormatting.values();

        int index = 1;
        for (Byte c : seed.getBytes())
            i = i + (c * index++);

        Random random = new Random(i);

        return array[random.nextInt(array.length-1)];
    }
    public static Color generateColor(String seed) {
        int i = 0;
        int index = 1;
        for (Byte c : seed.getBytes()) {
            index ++;
            i = i + (c * index);
        }
        Random random = new Random(i);
        int r = random.nextInt(200) + 55;
        int g = random.nextInt(200) + 55;
        int b = random.nextInt(200) + 55;
        return new Color(r, g, b);
    }



    public static float getHue(Color color) {
        return RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null)[0];
    }
    public static float getSaturation(Color color) {
        return RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null)[1];
    }

    public static float getBrightness(Color color) {
        return RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null)[2];
    }

    public static Color lerpColor(Color from, Color to, float blending) {
        blending = MathHelper.clamp(blending,0,1);
        float inverse_blending = 1 - blending;
        float red = to.getRed()   * blending   +   from.getRed()   * inverse_blending;
        float green = to.getGreen() * blending   +   from.getGreen() * inverse_blending;
        float blue = to.getBlue()  * blending   +   from.getBlue()  * inverse_blending;
        float alpha = to.getAlpha()  * blending   +   from.getAlpha()  * inverse_blending;

        return new Color (red / 255, green / 255, blue / 255,alpha/255);
    }



    public static Color fromHSB(float hue, float saturation, float brightness) {
        return new Color(Color.getHSBColor(hue, saturation, brightness).getRGB());
    }
    public static int toRGBA(int r, int g, int b, int a) {
        return (r << 16) + (g << 8) + (b) + (a << 24);
    }

    public static void glColor(Color color) {
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);

    }

    public static Color mutiplyAlpha(Color c,float mul) {
        return new Color(c.getRed(),c.getGreen(),c.getBlue(), (int)(c.getAlpha()*mul));
    }




    public static Color getColorBasedOnHealthPercent(float percent) {
        return lerpColor(
                new Color(236, 6, 6, 255),
                new Color(11, 245, 3, 255),


                percent
        );
    }

    public static Color getDimColor(int dim) {
        return new Color[]{
                new Color(190, 100, 51, 255),
                new Color(70, 161, 67, 255),
                new Color(215, 200, 153, 255)
        }[dim+1];
    }


}
