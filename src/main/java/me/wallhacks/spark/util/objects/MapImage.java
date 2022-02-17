package me.wallhacks.spark.util.objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;

public class MapImage {



    public static int lods = 4;
    public static int size = 512;

    final ResourceLocation[] resourceLocation;
    final DynamicTexture[] mapTextures;
    final BufferedImage[] bufferedImages;

    public MapImage() {
        mapTextures = new DynamicTexture[lods];
        bufferedImages = new BufferedImage[lods];
        resourceLocation = new ResourceLocation[lods];

        ResetBufferImages();
    }

    public void ResetBufferImages() {
        for (int i = 0; i < bufferedImages.length; i++)
        {
            int scale = i == 0 ? 1 : i * 3;
            bufferedImages[i] = new BufferedImage(size/scale, size/scale, BufferedImage.TYPE_4BYTE_ABGR);
        }

    }

    public ResourceLocation getResourceLocation(int lod)
    {
        DynamicTexture texture = getDynamicTexture(lod);
        if(texture == null)
            return null;
        resourceLocation[lod] = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("temp", texture);
        return resourceLocation[lod];
    }

    public BufferedImage getBufferedImage() {
        return bufferedImages[0];
    }


    public void setBufferedImage(BufferedImage bufferedImage) {
        bufferedImages[0] = bufferedImage;

        for (int i = 1; i < bufferedImages.length; i++)
        {
            int scale = i == 0 ? 1 : i * 3;
            bufferedImages[i] = new BufferedImage(size/scale, size/scale, BufferedImage.TYPE_4BYTE_ABGR);

            for(int x = 0; x < bufferedImages[i].getWidth(); x++)
            {
                for(int y = 0; y < bufferedImages[i].getHeight(); y++)
                {
                    bufferedImages[i].setRGB(x,y,bufferedImage.getRGB(x*scale,y*scale));
                }
            }
        }

        this.changedImage = true;
    }

    DynamicTexture getDynamicTexture(int lod) {
        return mapTextures[lod];
    }

    public void setRGB(int x,int y,int rgb) {
        bufferedImages[0].setRGB(x,y,rgb);
        for (int i = 1; i < bufferedImages.length; i++)
        {
            int scale = i * 3;

            if(x % scale == 0 && y % scale == 0)
                bufferedImages[i].setRGB(Math.min(x/scale,size/scale-1),Math.min(y/scale,size/scale-1),rgb);
        }

    }

    public void setChangedImage() {
        this.changedImage = true;
    }

    public void UpdateMapTextures()
    {
        for (int i = 0; i < mapTextures.length; i++)
            mapTextures[i] = new DynamicTexture(bufferedImages[i]);

        changedImage = false;
    }



    public boolean isChangedImage() {
        return changedImage;
    }

    boolean changedImage = true;
}
