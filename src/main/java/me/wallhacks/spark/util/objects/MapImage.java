package me.wallhacks.spark.util.objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;

public class MapImage {




    int size = 512;

    public int getSize() {
        return size;
    }

    ResourceLocation resourceLocation;
    DynamicTexture mapTextures;
    BufferedImage bufferedImages;

    public MapImage(int size) {

        this.size = size;

        bufferedImages = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);

    }



    public ResourceLocation getResourceLocation()
    {
        return resourceLocation;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImages;
    }


    public void setBufferedImage(BufferedImage bufferedImage) {


        this.bufferedImages = bufferedImage;

        this.changedImage = true;
    }

    DynamicTexture getDynamicTexture() {
        return mapTextures;
    }

    public void setRGB(int x,int y,int rgb) {
        bufferedImages.setRGB(x,y,rgb);
    }

    public void setChangedImage() {
        this.changedImage = true;
    }

    public void UpdateMapTextures()
    {
        if(mapTextures == null)
        {
            mapTextures = new DynamicTexture(bufferedImages);
            resourceLocation = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("temp", mapTextures );

        }
            else
        {
            bufferedImages.getRGB(0, 0, bufferedImages.getWidth(), bufferedImages.getHeight(), mapTextures.getTextureData(), 0, bufferedImages.getWidth());
            mapTextures.updateDynamicTexture();
        }


        changedImage = false;
    }

    public void delete() {
        if(resourceLocation != null)
            Minecraft.getMinecraft().getTextureManager().deleteTexture(resourceLocation);

    }




    public boolean isChangedImage() {
        return changedImage;
    }

    boolean changedImage = false;

}
