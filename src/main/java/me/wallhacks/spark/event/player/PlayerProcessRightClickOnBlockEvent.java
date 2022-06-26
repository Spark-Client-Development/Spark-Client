package me.wallhacks.spark.event.player;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class PlayerProcessRightClickOnBlockEvent extends Event {

    public PlayerProcessRightClickOnBlockEvent(EntityPlayerSP player1, WorldClient worldIn1, BlockPos pos1, EnumFacing direction1, Vec3d vec1, EnumHand hand1) {
        this.player = player1;
        this.worldIn = worldIn1;

        this.pos = pos1;
        this.direction = direction1;
        this.vec = vec1;
        this.hand = hand1;
    }

    final EntityPlayerSP player; final WorldClient worldIn; final BlockPos pos; final EnumFacing direction; final Vec3d vec; final EnumHand hand;

    public EntityPlayer getPlayer() {
        return player;
    }

    public EnumHand getHand() {
        return hand;
    }


}
