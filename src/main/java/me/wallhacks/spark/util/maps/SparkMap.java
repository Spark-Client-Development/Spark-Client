package me.wallhacks.spark.util.maps;

import java.awt.image.BufferedImage;

import me.wallhacks.spark.util.objects.Vec2i;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class SparkMap {


    public DynamicTexture mapTexture;
    public ResourceLocation resourceLocation;
    public BufferedImage bufferedImage;


    public final Vec2i pos;
    public final int dim;

    static final int scale = 2;

    public SparkMap(final Vec2i p,final int d){
        pos = p;
        dim = d;

        mapTexture = new DynamicTexture(128, 128);
        bufferedImage = new BufferedImage(128, 128, BufferedImage.TYPE_4BYTE_ABGR);

        UpdateMapTexture();
    }

    public void UpdateMapTexture()
    {
        mapTexture = new DynamicTexture(bufferedImage);
    }

    public static float getWidthAndHeight(){
        return 128*scale;
    }

    public static double get2dMapPosFromWorldPos(double x,int MapScale){
        return x*(MapScale/getWidthAndHeight());
    }
    public static int get2iMapPosFromWorldPos(double x,int MapScale){
        return (int) (x*(MapScale/getWidthAndHeight()));
    }
    public static double getWorldPosFrom2dMapPos(double x,int MapScale){
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
        return new Vec2i(pos.x*128*scale,pos.y*128*scale);
    }
    public Vec2i getEndPos(){
        return new Vec2i(pos.x*128*scale+128*scale,pos.y*128*scale+128*scale);
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

            for (int x1 = 0; x1 < 16/scale; x1++) {
                for (int z1 = 0; z1 < 16/scale; z1++) {
                    int mapC = getMapColorAtPos(x+x1*scale, z+z1*scale, worldIn);

                    bufferedImage.setRGB(x/scale+x1-(pos.x*128), z/scale+z1-(pos.y*128), mapC);


                }
            }
        }


        UpdateMapTexture();

    }

    int getMapColorAtPos(int x,int z,World worldIn){

        IBlockState iblockstate = Blocks.AIR.getDefaultState();
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        Chunk chunk = worldIn.getChunk(new BlockPos(x,0,z));

        blockpos$mutableblockpos.setPos(x,100,z);
        iblockstate = chunk.getBlockState(x, 100, z);


        int waterDepth = 0;
        int h = 256;

        if(worldIn.provider.isNether())
        {
            h = 122;
            while (true){
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
            j = (scale + scale / 128 & 1) * 8 + 16 << 24;
        }
        else
        {
            j = MapColor.COLORS[j / 4].getMapColor(j & 3);
        }

        return j;

    }




}
