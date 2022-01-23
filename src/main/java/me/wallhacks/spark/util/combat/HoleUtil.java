package me.wallhacks.spark.util.combat;

import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.WorldUtils;
import net.minecraft.init.Blocks;
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

    public static List<Hole> getHoles(float range) {
        List<Hole> holes = new ArrayList<>();

        for (BlockPos pos : WorldUtils.getSphere(mc.player.getPosition(), range, (int) range, 1)) {
            if (isObsidianHole(pos))
                holes.add(new Hole(Type.Obsidian, Facing.None, pos));

            if (isBedRockHole(pos))
                holes.add(new Hole(Type.Bedrock, Facing.None, pos));
        }

        return holes;
    }

    public static boolean isDoubleBedrockHoleX(BlockPos blockPos) {
        if (!mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(1, 1, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(1, 2, 0)).getBlock().equals(Blocks.AIR))
            return false;

        for (BlockPos blockPos2 : new BlockPos[]{blockPos.add(2, 0, 0), blockPos.add(1, 0, 1), blockPos.add(1, 0, -1), blockPos.add(-1, 0, 0), blockPos.add(0, 0, 1), blockPos.add(0, 0, -1), blockPos.add(0, -1, 0), blockPos.add(1, -1, 0)}) {
            IBlockState iBlockState = mc.world.getBlockState(blockPos2);

            if (iBlockState.getBlock() != Blocks.AIR && (iBlockState.getBlock() == Blocks.BEDROCK))
                continue;

            return false;
        }

        return true;
    }

    public static boolean isDoubleBedrockHoleZ(BlockPos blockPos) {
        if (!mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(0, 1, 1)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(0, 2, 1)).getBlock().equals(Blocks.AIR))
            return false;

        for (BlockPos blockPos2 : new BlockPos[]{blockPos.add(0, 0, 2), blockPos.add(1, 0, 1), blockPos.add(-1, 0, 1), blockPos.add(0, 0, -1), blockPos.add(1, 0, 0), blockPos.add(-1, 0, 0), blockPos.add(0, -1, 0), blockPos.add(0, -1, 1)}) {
            IBlockState iBlockState = mc.world.getBlockState(blockPos2);

            if (iBlockState.getBlock() != Blocks.AIR && (iBlockState.getBlock() == Blocks.BEDROCK))
                continue;

            return false;
        }

        return true;
    }

    public static boolean isDoubleObsidianHoleX(BlockPos blockPos) {
        if (!mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(1, 1, 0)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(1, 2, 0)).getBlock().equals(Blocks.AIR))
            return false;

        for (BlockPos blockPos2 : new BlockPos[]{blockPos.add(2, 0, 0), blockPos.add(1, 0, 1), blockPos.add(1, 0, -1), blockPos.add(-1, 0, 0), blockPos.add(0, 0, 1), blockPos.add(0, 0, -1), blockPos.add(0, -1, 0), blockPos.add(1, -1, 0)}) {
            if (getBlockResistance(blockPos2) == BlockResistance.Resistant || getBlockResistance(blockPos2) == BlockResistance.Unbreakable)
                continue;

            return false;
        }

        return true;
    }

    public static boolean isDoubleObsidianHoleZ(BlockPos blockPos) {
        if (!mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(0, 1, 1)).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(blockPos.add(0, 2, 1)).getBlock().equals(Blocks.AIR))
            return false;

        for (BlockPos blockPos2 : new BlockPos[]{blockPos.add(0, 0, 2), blockPos.add(1, 0, 1), blockPos.add(-1, 0, 1), blockPos.add(0, 0, -1), blockPos.add(1, 0, 0), blockPos.add(-1, 0, 0), blockPos.add(0, -1, 0), blockPos.add(0, -1, 1)}) {
            if (getBlockResistance(blockPos2) == BlockResistance.Resistant || getBlockResistance(blockPos2) == BlockResistance.Unbreakable)
                continue;

            return false;
        }

        return true;
    }


    public static boolean isObsidianHole(BlockPos blockPos) {
        return !(getBlockResistance(blockPos.add(0, 1, 0)) != BlockResistance.Blank || isBedRockHole(blockPos) || getBlockResistance(blockPos.add(0, 0, 0)) != BlockResistance.Blank || getBlockResistance(blockPos.add(0, 2, 0)) != BlockResistance.Blank || getBlockResistance(blockPos.add(0, 0, -1)) != BlockResistance.Resistant && getBlockResistance(blockPos.add(0, 0, -1)) != BlockResistance.Unbreakable || getBlockResistance(blockPos.add(1, 0, 0)) != BlockResistance.Resistant && getBlockResistance(blockPos.add(1, 0, 0)) != BlockResistance.Unbreakable || getBlockResistance(blockPos.add(-1, 0, 0)) != BlockResistance.Resistant && getBlockResistance(blockPos.add(-1, 0, 0)) != BlockResistance.Unbreakable || getBlockResistance(blockPos.add(0, 0, 1)) != BlockResistance.Resistant && getBlockResistance(blockPos.add(0, 0, 1)) != BlockResistance.Unbreakable || getBlockResistance(blockPos.add(0.5, 0.5, 0.5)) != BlockResistance.Blank || getBlockResistance(blockPos.add(0, -1, 0)) != BlockResistance.Resistant && getBlockResistance(blockPos.add(0, -1, 0)) != BlockResistance.Unbreakable);
    }

    public static boolean isBedRockHole(BlockPos blockPos) {
        return getBlockResistance(blockPos.add(0, 1, 0)) == BlockResistance.Blank && getBlockResistance(blockPos.add(0, 0, 0)) == BlockResistance.Blank && getBlockResistance(blockPos.add(0, 2, 0)) == BlockResistance.Blank && getBlockResistance(blockPos.add(0, 0, -1)) == BlockResistance.Unbreakable && getBlockResistance(blockPos.add(1, 0, 0)) == BlockResistance.Unbreakable && getBlockResistance(blockPos.add(-1, 0, 0)) == BlockResistance.Unbreakable && getBlockResistance(blockPos.add(0, 0, 1)) == BlockResistance.Unbreakable && getBlockResistance(blockPos.add(0.5, 0.5, 0.5)) == BlockResistance.Blank && getBlockResistance(blockPos.add(0, -1, 0)) == BlockResistance.Unbreakable;
    }

    public static boolean isHole(BlockPos blockPos) {
        return getBlockResistance(blockPos.add(0, 1, 0)) == BlockResistance.Blank && getBlockResistance(blockPos.add(0, 0, 0)) == BlockResistance.Blank && getBlockResistance(blockPos.add(0, 2, 0)) == BlockResistance.Blank && (getBlockResistance(blockPos.add(0, 0, -1)) == BlockResistance.Resistant || getBlockResistance(blockPos.add(0, 0, -1)) == BlockResistance.Unbreakable) && ((getBlockResistance(blockPos.add(1, 0, 0)) == BlockResistance.Resistant || (getBlockResistance(blockPos.add(1, 0, 0)) == BlockResistance.Unbreakable)) && ((getBlockResistance(blockPos.add(-1, 0, 0)) == BlockResistance.Resistant) || (getBlockResistance(blockPos.add(-1, 0, 0)) == BlockResistance.Unbreakable)) && ((getBlockResistance(blockPos.add(0, 0, 1)) == BlockResistance.Resistant) || (getBlockResistance(blockPos.add(0, 0, 1)) == BlockResistance.Unbreakable)) && (getBlockResistance(blockPos.add(0.5, 0.5, 0.5)) == BlockResistance.Blank) && ((getBlockResistance(blockPos.add(0, -1, 0)) == BlockResistance.Resistant) || (getBlockResistance(blockPos.add(0, -1, 0)) == BlockResistance.Unbreakable)));
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
