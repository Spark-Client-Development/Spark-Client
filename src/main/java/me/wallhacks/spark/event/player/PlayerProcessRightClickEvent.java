package me.wallhacks.spark.event.player;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PlayerProcessRightClickEvent extends Event {

    public PlayerProcessRightClickEvent(EntityPlayer player, World world, EnumHand hand) {
        this.player = player;
        this.world = world;
        this.hand = hand;
    }

    EnumHand hand;
    EntityPlayer player;
    World world;

    public EntityPlayer getPlayer() {
        return player;
    }

    public EnumHand getHand() {
        return hand;
    }

    public World getWorld() {
        return world;
    }
}
