package me.wallhacks.spark.event.block;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;


public class BlockChangeEvent extends Event {

    private final BlockPos blockPos;

    public BlockChangeEvent(BlockPos blockPos)
    {
        super();
        this.blockPos = blockPos;
    }


    public BlockPos getBlockPos()
    {
        return blockPos;
    }



}
