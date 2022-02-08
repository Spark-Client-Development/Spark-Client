package me.wallhacks.spark.systems.module.modules.render;

import me.wallhacks.spark.event.client.SettingChangeEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.module.SearchChunksModule;
import me.wallhacks.spark.systems.setting.settings.*;
import me.wallhacks.spark.util.render.EspUtil;
import me.wallhacks.spark.util.render.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

@Module.Registration(name = "Search", description = "Search blocks in your render distance")
public class Search extends SearchChunksModule<Search.SearchBlock> {
    BlockListSelectSetting searchBlocks = new BlockListSelectSetting("SearchBlocks", this, new Block[]{
            Block.getBlockFromName("end_portal_frame"),
            Block.getBlockFromName("portal")
    });
    BooleanSetting tracers = new BooleanSetting("Tracers", this, false);

    @SubscribeEvent
    public void onSettingChange(SettingChangeEvent event) {
        if (event.getSetting() == searchBlocks) {
            refresh();
        }
    }


    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent event) {
        if (nullCheck())
            return;
        for (Chunk c : found.keySet()) {
            for (SearchBlock block : found.get(c)) {
                EspUtil.drawBox(block, new Color(block.color.getRed(), block.color.getGreen(), block.color.getBlue(), 80));
                EspUtil.drawOutline(block, new Color(block.color.getRed(), block.color.getGreen(), block.color.getBlue(), 150));
                if (tracers.getValue())
                    EspUtil.renderTracers(new Vec3d(block).add(0.5, 0.5, 0.5), new Color(block.color.getRed(), block.color.getGreen(), block.color.getBlue(), 100), 2);
            }
        }
    }

    @Override
    protected void blockChanged(BlockPos pos) {
        IBlockState state = mc.world.getBlockState(pos);
        if (searchBlocks.contains(state.getBlock())) addFound(new SearchBlock(pos, state));
        else removeFound(pos);
    }

    @Override
    protected void searchChunk(Chunk chunk) {
        for (int x = chunk.getPos().getXStart(); x <= chunk.getPos().getXEnd(); x++) {
            for (int z = chunk.getPos().getZStart(); z <= chunk.getPos().getZEnd(); z++) {
                for (int y = 0; y <= 256; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    IBlockState state = chunk.getBlockState(pos);
                    if (searchBlocks.contains(state.getBlock())) addFound(new SearchBlock(new BlockPos(pos), state));
                }
            }
        }
    }

    class SearchBlock extends BlockPos {
        Color color;

        public SearchBlock(BlockPos pos, IBlockState state) {
            super(pos);
            Block block = state.getBlock();
            if (block == Blocks.PORTAL) {
                color = new Color(100, 0, 255);
            } else if (block == Blocks.ENDER_CHEST) {
                color = new Color(0, 125, 75);
            } else if (block instanceof BlockShulkerBox) {
                color = new Color(200, 100, 200);
            } else {
                color = new Color(mc.getBlockColors().getColor(state, mc.world, pos));
            }
        }
    }
}
