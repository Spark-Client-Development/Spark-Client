package me.wallhacks.spark.util.combat;

import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.WorldUtils;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import me.wallhacks.spark.util.objects.Hole;
import me.wallhacks.spark.util.objects.Hole.Type;
import me.wallhacks.spark.util.objects.Hole.Facing;

import java.util.ArrayList;
import java.util.List;

public class HoleUtil implements MC {
    public static boolean isInHole(EntityPlayer entityPlayer) {
        return isHole(new BlockPos(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ));
    }

    public static boolean isVoidHole(BlockPos blockPos) {
        return mc.player.dimension == -1 ? (blockPos.getY() == 0 || blockPos.getY() == 127) && getBlockResistance(blockPos) == BlockResistance.Blank : blockPos.getY() == 0 && getBlockResistance(blockPos) == BlockResistance.Blank;
    }


    public static int isDoubleHole(BlockPos blockPos, EnumFacing enumFacing) {
        if (getBlockResistance(blockPos.add(enumFacing.getDirectionVec())) != BlockResistance.Blank || getBlockResistance(blockPos.add(enumFacing.getDirectionVec()).add(0,1,0)) != BlockResistance.Blank
            || getBlockResistance(blockPos) != BlockResistance.Blank || getBlockResistance(blockPos.add(0,1,0)) != BlockResistance.Blank)
            return -1;

        int bedrock = 0;

        BlockResistance resistance = getBlockResistance(blockPos.add(0, -1, 0));
        if(resistance == BlockResistance.Unbreakable)
            bedrock++;
        else if(resistance != BlockResistance.Resistant)
            return -1;

        resistance = getBlockResistance(blockPos.add(enumFacing.getDirectionVec()).add(0, -1, 0));
        if(resistance == BlockResistance.Unbreakable)
            bedrock++;
        else if(resistance != BlockResistance.Resistant)
            return -1;

        for (BlockPos blockPos2 : WorldUtils.getSurroundBlocks(blockPos)) {
            if(blockPos2.equals(blockPos.add(enumFacing.getDirectionVec())))
                continue;

            resistance = getBlockResistance(blockPos2);
            if(resistance == BlockResistance.Unbreakable)
                bedrock++;
            else if(resistance != BlockResistance.Resistant)
                return -1;
        }

        for (BlockPos blockPos2 : WorldUtils.getSurroundBlocks(blockPos.add(enumFacing.getDirectionVec()))) {
            if(blockPos2.equals(blockPos))
                continue;

            resistance = getBlockResistance(blockPos2);
            if(resistance == BlockResistance.Unbreakable)
                bedrock++;
            else if(resistance != BlockResistance.Resistant)
                return -1;
        }

        return bedrock;
    }


    public static int isSingleHole(BlockPos blockPos) {
        if(getBlockResistance(blockPos) != BlockResistance.Blank || getBlockResistance(blockPos.add(0,1,0)) != BlockResistance.Blank)
            return -1;
        int i = 0;

        BlockResistance resistance = getBlockResistance(blockPos.add(0, -1, 0));
        if(resistance == BlockResistance.Unbreakable)
            i++;
        else if(resistance != BlockResistance.Resistant)
            return -1;

        resistance = getBlockResistance(blockPos.add(0, 0, 1));
        if(resistance == BlockResistance.Unbreakable)
            i++;
        else if(resistance != BlockResistance.Resistant)
            return -1;

        resistance = getBlockResistance(blockPos.add(1, 0, 0));
        if(resistance == BlockResistance.Unbreakable)
            i++;
        else if(resistance != BlockResistance.Resistant)
            return -1;

        resistance = getBlockResistance(blockPos.add(-1, 0, 0));
        if(resistance == BlockResistance.Unbreakable)
            i++;
        else if(resistance != BlockResistance.Resistant)
            return -1;

        resistance = getBlockResistance(blockPos.add(0, 0, -1));
        if(resistance == BlockResistance.Unbreakable)
            i++;
        else if(resistance != BlockResistance.Resistant)
            return -1;

        return i;
    }

    public static boolean isHole(BlockPos blockPos) {
        return getBlockResistance(blockPos.add(0, 1, 0)) == BlockResistance.Blank && getBlockResistance(blockPos.add(0, 0, 0)) == BlockResistance.Blank  && (getBlockResistance(blockPos.add(0, 0, -1)) == BlockResistance.Resistant || getBlockResistance(blockPos.add(0, 0, -1)) == BlockResistance.Unbreakable) && ((getBlockResistance(blockPos.add(1, 0, 0)) == BlockResistance.Resistant || (getBlockResistance(blockPos.add(1, 0, 0)) == BlockResistance.Unbreakable)) && ((getBlockResistance(blockPos.add(-1, 0, 0)) == BlockResistance.Resistant) || (getBlockResistance(blockPos.add(-1, 0, 0)) == BlockResistance.Unbreakable)) && ((getBlockResistance(blockPos.add(0, 0, 1)) == BlockResistance.Resistant) || (getBlockResistance(blockPos.add(0, 0, 1)) == BlockResistance.Unbreakable)) && (getBlockResistance(blockPos.add(0.5, 0.5, 0.5)) == BlockResistance.Blank) && ((getBlockResistance(blockPos.add(0, -1, 0)) == BlockResistance.Resistant) || (getBlockResistance(blockPos.add(0, -1, 0)) == BlockResistance.Unbreakable)));
    }

    public static BlockResistance getBlockResistance(BlockPos block) {
        if (mc.world.isAirBlock(block))
            return BlockResistance.Blank;

        else if (mc.world.getBlockState(block).getBlock().getBlockHardness(mc.world.getBlockState(block), mc.world, block) != -1 && !(mc.world.getBlockState(block).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(block).getBlock().equals(Blocks.ANVIL) || mc.world.getBlockState(block).getBlock().equals(Blocks.ENCHANTING_TABLE) || mc.world.getBlockState(block).getBlock().equals(Blocks.ENDER_CHEST)))
            return BlockResistance.Breakable;

        else if (mc.world.getBlockState(block).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(block).getBlock().equals(Blocks.ANVIL) || mc.world.getBlockState(block).getBlock().equals(Blocks.ENCHANTING_TABLE) || mc.world.getBlockState(block).getBlock().equals(Blocks.ENDER_CHEST))
            return BlockResistance.Resistant;

        else if (mc.world.getBlockState(block).getBlock().equals(Blocks.BEDROCK))
            return BlockResistance.Unbreakable;

        return null;
    }

    public enum BlockResistance {
        Blank,
        Breakable,
        Resistant,
        Unbreakable
    }
}
