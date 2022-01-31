package me.wallhacks.spark.event.block;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class LiquidCollisionBBEvent extends Event {
    private AxisAlignedBB boundingBox;
    private BlockPos blockPos;

    public LiquidCollisionBBEvent(BlockPos blockPos)
    {
        super();
        this.blockPos = blockPos;
    }

    public AxisAlignedBB getBoundingBox()
    {
        return boundingBox;
    }

    public void setBoundingBox(AxisAlignedBB boundingBox)
    {
        this.boundingBox = boundingBox;
    }

    public BlockPos getBlockPos()
    {
        return blockPos;
    }

    public void setBlockPos(BlockPos blockPos)
    {
        this.blockPos = blockPos;
    }


}
