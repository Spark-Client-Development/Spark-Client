package me.wallhacks.spark.event.player;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class PlayerDamageBlockEvent extends Event {



    boolean returnValue = false;
    BlockPos pos;
    EnumFacing facing;

    public PlayerDamageBlockEvent(BlockPos pos, EnumFacing facing) {
        super();
        this.pos = pos;
        this.facing = facing;

    }
    public EnumFacing getFacing() {
        return this.facing;
    }
    public BlockPos getPos() {
        return this.pos;
    }
    public boolean getReturnValue() {
        return this.returnValue;
    }

    public void setReturnValue(Boolean returnValue) {
        this.returnValue = returnValue;
    }



}
