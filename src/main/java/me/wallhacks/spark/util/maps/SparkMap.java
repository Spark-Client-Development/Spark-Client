package me.wallhacks.spark.util.maps;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.manager.SeedManager;
import me.wallhacks.spark.util.objects.*;
import me.wallhacks.spark.util.render.ColorUtil;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class SparkMap {


    MapImage image;

    MapImage biomeImage;

    public boolean isEmpty() {
        return image == null;
    }
    public boolean isBiomeMapEmpty() {
        return biomeImage == null;
    }
    public ResourceLocation getResourceLocation()
    {
        return image.getResourceLocation();
    }
    public ResourceLocation getBiomeResourceLocation()
    {
        return biomeImage.getResourceLocation();
    }

    public BufferedImage getBufferedImage() {
        return image.getBufferedImage();
    }

    public CopyOnWriteArrayList<Pair<Vec2i,MCStructures>> structures = new CopyOnWriteArrayList<Pair<Vec2i, MCStructures>>();

    public void setBufferedImage(BufferedImage bufferedImage) {
        if(image == null)
            image = new MapImage(size);
        image.setBufferedImage(bufferedImage);
    }

    public void delete() {
        if(image != null)
            image.delete();
        if(biomeImage != null)
            biomeImage.delete();
    }

    public boolean updateMapTextures(){
        if(!isEmpty() && image.isChangedImage())
        {
            image.UpdateMapTextures();
            return true;
        }
        if(!isBiomeMapEmpty() && biomeImage.isChangedImage())
        {
            biomeImage.UpdateMapTextures();
            return true;
        }
        return false;
    }


    public static int getChunksInMap() {
        return size*scale/16;
    }


    public final Vec2i pos;
    public final int dim;

    public static final int scale = 2;
    public static int size = 512;
    public SparkMap(final Vec3i p){
        this(new Vec2i(p.getX(),p.getZ()),p.getY());
    }
    public SparkMap(final Vec2i p,final int d){
        pos = p;
        dim = d;

    }

    public static Vec2i getWorldPosFromScreenPosOnMap(double zoom, Vec2d pos, double x, double y, double centerX, double centerY) {

        return new Vec2i(SparkMap.getWorldPosFrom2dMapPosRound(SparkMap.get2dMapPosFromWorldPos(pos.x,(int)zoom) + x - centerX, (int) zoom), SparkMap.getWorldPosFrom2dMapPosRound(SparkMap.get2dMapPosFromWorldPos(pos.y,(int)zoom) + y - centerY, (int) zoom));

    }




    public static float getWidthAndHeight(){
        return size*scale;
    }

    public static double get2dMapPosFromWorldPos(double x,double MapScale){
        return x*(MapScale/getWidthAndHeight());
    }
    public static int get2iMapPosFromWorldPos(double x,int MapScale){
        return (int) (x*(MapScale/getWidthAndHeight()));
    }
    public static double getWorldPosFrom2dMapPos(double x,double MapScale){
        return x/(MapScale/getWidthAndHeight());
    }
    public static int getWorldPosFrom2dMapPosRound(double x,int MapScale){
        return (int)Math.round((x/(MapScale/getWidthAndHeight())));
    }


    public static Vec2i getMapPosFrom2dMapPos(double ScreenX,double ScreenY,int MapScale){
        return getMapPosFromWorldPos(getWorldPosFrom2dMapPos(ScreenX,MapScale),getWorldPosFrom2dMapPos(ScreenY,MapScale));
    }
    public static Vec2i getMapPosFromWorldPos(double x,double y){
        return new Vec2i((int)Math.floor(x /getWidthAndHeight()),(int)Math.floor(y /getWidthAndHeight()));
    }



    public Vec2i getStartPos(){
        return new Vec2i(pos.x*size*scale,pos.y*size*scale);
    }
    public Vec2i getEndPos(){
        return new Vec2i(pos.x*size*scale+size*scale,pos.y*size*scale+size*scale);
    }
    public boolean isPosInMap(int x,int z){

        return (pos.x == (x/scale)>>7 && pos.y == (z/scale)>>7);
    }
    public boolean isChunkInMap(Chunk c){
        return isPosInMap(c.getPos().x*16,c.getPos().z*16);
    }







    public void updateMapData(Chunk chunk,World worldIn)
    {


        int x = chunk.getPos().x * 16;
        int z = chunk.getPos().z * 16;
        int j1 = 16;


        boolean flag = false;

        flag = false;
        double d0 = 0.0D;



        if (!chunk.isEmpty())
        {
            if(image == null)
                image = new MapImage(size);

            for (int x1 = 0; x1 < 16/scale; x1++) {
                for (int z1 = 0; z1 < 16/scale; z1++) {
                    int mapC = getMapColorAtPos(x+x1*scale, z+z1*scale, chunk, worldIn);

                    image.setRGB(x/scale+x1-(pos.x*size), z/scale+z1-(pos.y*size), mapC);


                }
            }

            image.setChangedImage();
        }


    }

    int getMapColorAtPos(int x,int z,Chunk chunk,World worldIn){

        IBlockState iblockstate = Blocks.AIR.getDefaultState();
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();


        blockpos$mutableblockpos.setPos(x,100,z);
        iblockstate = chunk.getBlockState(x, 100, z);


        int waterDepth = 0;
        int h = 256;

        if(worldIn.provider.isNether())
        {
            h = 122;
            while (true) {
                iblockstate = chunk.getBlockState(x, h, z);
                blockpos$mutableblockpos.setPos(x, h,z);

                if(iblockstate != Blocks.LAVA && iblockstate.getMapColor(worldIn, blockpos$mutableblockpos) != MapColor.NETHERRACK){

                    break;
                }
                h--;
                if(h <= 90)
                    break;
            }
        }

        while (true)
        {
            iblockstate = chunk.getBlockState(x, h, z);
            blockpos$mutableblockpos.setPos(x, h,z);

            if(iblockstate.getMapColor(worldIn, blockpos$mutableblockpos) != MapColor.AIR){

                if(!worldIn.provider.isNether() || (iblockstate.getBlock() != Blocks.LAVA && iblockstate.getBlock() != Blocks.FIRE) || h < 32)
                {
                    break;
                }
            }
            h--;
            if(h < 0)
                break;

        }
        if (h > 0 && iblockstate.getMaterial().isLiquid())
        {
            int l4 = h - 1;

            while (true)
            {
                IBlockState iblockstate1 = chunk.getBlockState(x, l4--, z);
                ++waterDepth;

                if (l4 <= 0 || !iblockstate1.getMaterial().isLiquid())
                {
                    break;
                }
            }
        }


        MapColor mapColor = iblockstate.getMapColor(worldIn, blockpos$mutableblockpos);

        waterDepth = waterDepth / (scale * scale);

        int i5 = 1;

        if (mapColor == MapColor.WATER)
        {
            double d2 = (double)waterDepth * 0.1D + (double)(x + z & 1) * 0.2D;


            if (d2 < 0.5D)
                i5 = 2;


            if (d2 > 0.9D)
                i5 = 0;

        }
        else{
            double d2 = Math.sin(((double)h / (double)(scale * scale))*Math.PI*0.5);

            if (d2 > 0.33D)
                i5 = 2;
            if (d2 < -0.33D)
                i5 = 0;
        }

        int j = ((byte)(mapColor.colorIndex * 4 + i5)) & 255;

        if (j / 4 == 0)
        {
            j = (scale + scale / size & 1) * 8 + 16 << 24;
        }
        else
        {
            j = MapColor.COLORS[j / 4].getMapColor(j & 3);
        }

        return j;

    }


    public void generateBiomeMap() {
        if(biomeImage == null)
            biomeImage = new MapImage(getChunksInMap());

        Vec2i vec2i = getStartPos();


        for (int x1 = 0; x1 < getChunksInMap(); x1++) {
            for (int z1 = 0; z1 < getChunksInMap(); z1++) {
                Biome mapC = Spark.seedManager.getBiome(vec2i.x+x1*16, vec2i.y+z1*16, dim);

                biomeImage.setRGB(x1, z1, ColorUtil.getBiomeColor(mapC).getRGB());
            }
        }

        biomeImage.setChangedImage();
    }
}
