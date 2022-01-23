package me.wallhacks.spark.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;
public class BlockChangeEvent extends Event {
    public BlockPos pos;
    public IBlockState state;
    public BlockChangeEvent(BlockPos pos, IBlockState state) {
        this.pos = pos;
        this.state = state;
    }
}
