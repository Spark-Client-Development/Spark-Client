package com.github.lunatrius.schematica.block.state.pattern;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockStateMatcher;

import java.util.Map;

public class BlockStateReplacer {
    private final IBlockState defaultReplacement;

    private BlockStateReplacer(final IBlockState defaultReplacement) {
        this.defaultReplacement = defaultReplacement;
    }

    @SuppressWarnings({ "rawtypes" })
    public IBlockState getReplacement(final IBlockState original, final Map<IProperty, Comparable> properties) {
        IBlockState replacement = this.defaultReplacement;

        replacement = applyProperties(replacement, original.getProperties());
        replacement = applyProperties(replacement, properties);

        return replacement;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private <K extends IProperty, V extends Comparable> IBlockState applyProperties(IBlockState blockState, final Map<K, V> properties) {
        for (final Map.Entry<K, V> entry : properties.entrySet()) {
            try {
                blockState = blockState.withProperty(entry.getKey(), entry.getValue());
            } catch (final IllegalArgumentException ignored) {
            }
        }

        return blockState;
    }

    public static BlockStateReplacer forBlockState(final IBlockState replacement) {
        return new BlockStateReplacer(replacement);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static BlockStateMatcher getMatcher(final BlockStateInfo blockStateInfo) {
        final BlockStateMatcher matcher = BlockStateMatcher.forBlock(blockStateInfo.block);
        for (final Map.Entry<IProperty, Comparable> entry : blockStateInfo.stateData.entrySet()) {
            matcher.where(entry.getKey(), new Predicate<Comparable>() {
                @Override
                public boolean apply(final Comparable input) {
                    return input != null && input.equals(entry.getValue());
                }
            });
        }

        return matcher;
    }

    @SuppressWarnings({ "rawtypes" })
    public static class BlockStateInfo {
        public final Block block;
        public final Map<IProperty, Comparable> stateData;

        public BlockStateInfo(final Block block, final Map<IProperty, Comparable> stateData) {
            this.block = block;
            this.stateData = stateData;
        }
    }
}
