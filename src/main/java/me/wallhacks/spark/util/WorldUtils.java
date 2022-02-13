package me.wallhacks.spark.util;

import me.wallhacks.spark.Spark;
import net.minecraft.block.Block;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import me.wallhacks.spark.util.player.PlayerUtil;

import java.util.ArrayList;
import java.util.List;

public class WorldUtils implements MC {
    public static List<BlockPos> getBlocksOccupiedByBox (AxisAlignedBB bb){
        double minX = bb.minX+0.000000000001;
        double maxX = bb.maxX-0.000000000001;
        double minZ = bb.minZ+0.000000000001;
        double maxZ = bb.maxZ-0.000000000001;
        double minY = bb.minY+0.000000000001;

        Vec3d[] checkFor = new Vec3d[]{new Vec3d(maxX,minY,maxZ),new Vec3d(minX,minY,minZ),new Vec3d(maxX,minY,minZ),new Vec3d(minX,minY,maxZ)};
        ArrayList<BlockPos> occupiedByPlayer = new ArrayList<>();
        for (Vec3d vec : checkFor) {
            BlockPos floored = PlayerUtil.getPlayerPosFloored(vec,0.2);
            if (!occupiedByPlayer.contains(floored))
                occupiedByPlayer.add(floored);
        }
        return occupiedByPlayer;
    }

    public static List<BlockPos> getSphere(BlockPos loc, float r, int h, int plus_y)
    {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++)
        {
            for (int z = cz - (int) r; z <= cz + r; z++)
            {
                for (int y = cy - (int) h; y < cy + h; y++)
                {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + ((cy - y) * (cy - y));
                    if (dist < r * r)
                    {
                        circleblocks.add(new BlockPos(x, y + plus_y, z));
                    }
                }
            }
        }
        return circleblocks;
    }

    public static Block[] getListOfBlocks(){
        ArrayList<Block> bs = new ArrayList<Block>();

        Block.REGISTRY.forEach( (Block) -> {
            bs.add(Block);
        });

        return ((List<Block>)bs).toArray(new Block[bs.size()]);
    }

    public static ChunkPos[] getAdjacentChunks(ChunkPos c) {
        return new ChunkPos[]{new ChunkPos(c.x+1,c.z),new ChunkPos(c.x,c.z+1),new ChunkPos(c.x-1,c.z),new ChunkPos(c.x,c.z-1),new ChunkPos(c.x+1,c.z+1),new ChunkPos(c.x+1,c.z-1),new ChunkPos(c.x-1,c.z+1),new ChunkPos(c.x-1,c.z-1)};
    }


}
