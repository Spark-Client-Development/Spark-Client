package me.wallhacks.spark.util.render;

import net.minecraft.init.Biomes;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.Biome;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
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


    public static Color getBiomeColor(Biome b) {
        if (Biomes.OCEAN.equals(b)) {
            return new Color(000070);
        } else if (Biomes.DEFAULT.equals(b)) {
            return new Color(0x8DB360);
        } else if (Biomes.PLAINS.equals(b)) {
            return new Color(0x8DB360);
        } else if (Biomes.DESERT.equals(b)) {
            return new Color(0xFA9418);
        } else if (Biomes.EXTREME_HILLS.equals(b)) {
            return new Color(0x38DC865E);
        } else if (Biomes.FOREST.equals(b)) {
            return new Color(0xCF1BBE67);
        } else if (Biomes.TAIGA.equals(b)) {
            return new Color(0xDA412828);
        } else if (Biomes.SWAMPLAND.equals(b)) {
            return new Color(0xA1829F8C);
        } else if (Biomes.RIVER.equals(b)) {
            return new Color(0x5858A6);
        } else if (Biomes.HELL.equals(b)) {
            return new Color(0xEA0D0D);
        } else if (Biomes.SKY.equals(b)) {
            return new Color(0x64000000);
        } else if (Biomes.FROZEN_OCEAN.equals(b)) {
            return new Color(0x2DC7C7);
        } else if (Biomes.FROZEN_RIVER.equals(b)) {
            return new Color(0x4ABCCB);
        } else if (Biomes.ICE_PLAINS.equals(b)) {
            return new Color(0x829EA8);
        } else if (Biomes.ICE_MOUNTAINS.equals(b)) {
            return new Color(0x9696B4);
        } else if (Biomes.MUSHROOM_ISLAND.equals(b)) {
            return new Color(0xE01FE0);
        } else if (Biomes.MUSHROOM_ISLAND_SHORE.equals(b)) {
            return new Color(0xC523C5);
        } else if (Biomes.BEACH.equals(b)) {
            return new Color(0x8CFFEC00);
        } else if (Biomes.DESERT_HILLS.equals(b)) {
            return new Color(0x8CDACB0F);
        } else if (Biomes.FOREST_HILLS.equals(b)) {
            return new Color(0x9E9EB0);
        } else if (Biomes.TAIGA_HILLS.equals(b)) {
            return new Color(0xCF1BBE67);
        } else if (Biomes.EXTREME_HILLS_EDGE.equals(b)) {
            return new Color(0x38DC865E);
        } else if (Biomes.JUNGLE.equals(b)) {
            return new Color(0xCF1BBE67);
        } else if (Biomes.JUNGLE_HILLS.equals(b)) {
            return new Color(0xCF1BBE67);
        } else if (Biomes.JUNGLE_EDGE.equals(b)) {
            return new Color(0x10DE0B);
        } else if (Biomes.DEEP_OCEAN.equals(b)) {
            return new Color(0x080893);
        } else if (Biomes.STONE_BEACH.equals(b)) {
            return new Color(0x1E1B9B9);
        } else if (Biomes.COLD_BEACH.equals(b)) {
            return new Color(0xDADAC6C6);
        } else if (Biomes.BIRCH_FOREST.equals(b)) {
            return new Color(0xCF64AB7B);
        } else if (Biomes.BIRCH_FOREST_HILLS.equals(b)) {
            return new Color(0x62C062);
        } else if (Biomes.ROOFED_FOREST.equals(b)) {
            return new Color(0x7E7EA8);
        } else if (Biomes.COLD_TAIGA.equals(b)) {
            return new Color(1);
        } else if (Biomes.COLD_TAIGA_HILLS.equals(b)) {
            return new Color(1);
        } else if (Biomes.REDWOOD_TAIGA.equals(b)) {
            return new Color(1);
        } else if (Biomes.REDWOOD_TAIGA_HILLS.equals(b)) {
            return new Color(1);
        } else if (Biomes.EXTREME_HILLS_WITH_TREES.equals(b)) {
            return new Color(1);
        } else if (Biomes.SAVANNA.equals(b)) {
            return new Color(0x3881543C);
        } else if (Biomes.SAVANNA_PLATEAU.equals(b)) {
            return new Color(0x3883543E);
        } else if (Biomes.MESA.equals(b)) {
            return new Color(1);
        } else if (Biomes.MESA_ROCK.equals(b)) {
            return new Color(1);
        } else if (Biomes.MESA_CLEAR_ROCK.equals(b)) {
            return new Color(1);
        } else if (Biomes.VOID.equals(b)) {
            return new Color(0x918F6E);
        }
        else if (Biomes.MUTATED_PLAINS.equals(b)) {
            return new Color(0x918F6E);
        }
        else if (Biomes.MUTATED_DESERT.equals(b)) {
            return new Color(0x918F6E);
        }
        else if (Biomes.MUTATED_EXTREME_HILLS.equals(b)) {
            return new Color(0x918F6E);
        }
        else if (Biomes.MUTATED_FOREST.equals(b)) {
            return new Color(0x918F6E);
        }
        else if (Biomes.MUTATED_TAIGA.equals(b)) {
            return new Color(0x918F6E);
        }
        else if (Biomes.MUTATED_SWAMPLAND.equals(b)) {
            return new Color(0x918F6E);
        }
        else if (Biomes.MUTATED_ICE_FLATS.equals(b)) {
            return new Color(0x918F6E);
        }
        else if (Biomes.MUTATED_JUNGLE.equals(b)) {
            return new Color(0x918F6E);
        }
        else if (Biomes.MUTATED_JUNGLE_EDGE.equals(b)) {
            return new Color(0x918F6E);
        }
        else if (Biomes.MUTATED_BIRCH_FOREST.equals(b)) {
            return new Color(0x918F6E);
        }
        else if (Biomes.MUTATED_BIRCH_FOREST_HILLS.equals(b)) {
            return new Color(0x918F6E);
        }
        else if (Biomes.MUTATED_ROOFED_FOREST.equals(b)) {
            return new Color(0x918F6E);
        }
        else if (Biomes.MUTATED_TAIGA_COLD.equals(b)) {
            return new Color(0x918F6E);
        }
        else if (Biomes.MUTATED_REDWOOD_TAIGA.equals(b)) {
            return new Color(0x918F6E);
        }
        else if (Biomes.MUTATED_REDWOOD_TAIGA_HILLS.equals(b)) {
            return new Color(0x918F6E);
        }
        else if (Biomes.MUTATED_EXTREME_HILLS_WITH_TREES.equals(b)) {
            return new Color(0x918F6E);
        }
        else if (Biomes.MUTATED_SAVANNA.equals(b)) {
            return new Color(0x918F6E);
        }
        else if (Biomes.MUTATED_SAVANNA_ROCK.equals(b)) {
            return new Color(0x918F6E);
        }
        else if (Biomes.MUTATED_MESA.equals(b)) {
            return new Color(0x918F6E);
        }
        else if (Biomes.MUTATED_MESA_ROCK.equals(b)) {
            return new Color(0x918F6E);
        }
        else if (Biomes.MUTATED_MESA_CLEAR_ROCK.equals(b)) {
            return new Color(0x918F6E);
        }

        return Color.WHITE;
    }
}
