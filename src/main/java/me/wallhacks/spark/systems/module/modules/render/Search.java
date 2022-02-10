package me.wallhacks.spark.systems.module.modules.render;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.client.SettingChangeEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.module.SearchChunksModule;
import me.wallhacks.spark.systems.setting.settings.*;
import me.wallhacks.spark.util.objects.Pair;
import me.wallhacks.spark.util.objects.Timer;
import me.wallhacks.spark.util.render.ColorUtil;
import me.wallhacks.spark.util.render.EspUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;

@Module.Registration(name = "Search", description = "Search blocks in your render distance")
public class Search extends SearchChunksModule<Search.SearchBlock> {
    BlockListSelectSetting searchBlocks = new BlockListSelectSetting("SearchBlocks", this, new Block[]{
            Block.getBlockFromName("end_portal_frame"),
            Block.getBlockFromName("portal")
    });

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
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(2.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        for (Chunk c : found.keySet()) {
            if (!found.containsKey(c) || found.get(c) == null) return;
            for (SearchBlock block : found.get(c)) {
                ColorUtil.glColor(new Color(block.color.getRed(), block.color.getGreen(), block.color.getBlue(), 170));
                for (int i = 0; i <= 11; i++) {
                    boolean should = block.lineMap[i].getKey();
                    if (!should) continue;
                    Vec3d a = block.lineMap[i].getValue().getValue();
                    Vec3d b = block.lineMap[i].getValue().getKey();
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex3d(a.x - mc.getRenderManager().viewerPosX + block.getX(), a.y - mc.getRenderManager().viewerPosY + block.getY(), a.z - mc.getRenderManager().viewerPosZ + block.getZ());
                    GL11.glVertex3d(b.x - mc.getRenderManager().viewerPosX + block.getX(), b.y - mc.getRenderManager().viewerPosY + block.getY(), b.z - mc.getRenderManager().viewerPosZ + block.getZ());
                    GL11.glEnd();
                }
            }
            GL11.glBegin(GL11.GL_QUADS);
            for (SearchBlock block : found.get(c)) {
                ColorUtil.glColor(new Color(block.color.getRed(), block.color.getGreen(), block.color.getBlue(), 120));
                int i = 0;
                double x = block.getX() - Minecraft.getMinecraft().getRenderManager().viewerPosX;
                double y = block.getY() - Minecraft.getMinecraft().getRenderManager().viewerPosY;
                double z = block.getZ() - Minecraft.getMinecraft().getRenderManager().viewerPosZ;
                AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
                for (EnumFacing facing : EnumFacing.VALUES) {
                    if (block.fill[i]) {
                        switch (facing) {
                            case UP:
                                GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
                                GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
                                GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
                                GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
                                break;
                            case DOWN:
                                GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
                                GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
                                GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
                                GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
                                break;
                            case NORTH:
                                GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
                                GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
                                GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
                                GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
                                break;
                            case SOUTH:
                                GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
                                GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
                                GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
                                GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
                                break;
                            case WEST:
                                GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
                                GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
                                GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
                                GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
                                break;
                            case EAST:
                                GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
                                GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
                                GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
                                GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
                                break;
                        }
                    }
                    i++;
                }
            }
            GL11.glEnd();
        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    protected void blockChanged(BlockPos pos) {
        IBlockState state = mc.world.getBlockState(pos);
        if (searchBlocks.contains(state.getBlock())) {
            addFound(new SearchBlock(pos, state, false, null));
        } else {
            removeFound(pos);
            for (EnumFacing facing : EnumFacing.VALUES) {
                BlockPos p = pos.add(facing.getDirectionVec());
                IBlockState s = mc.world.getBlockState(p);
                if (searchBlocks.contains(s.getBlock())) {
                    Chunk c = mc.world.getChunk(p);
                    if (found.containsKey(c) && found.get(c).contains(p)) {
                        found.get(c).remove(p);
                        addFound(new SearchBlock(p, s, true, pos));
                    }
                }
            }
        }
    }

    @Override
    protected void searchChunk(Chunk chunk) {
        for (int x = chunk.getPos().getXStart(); x <= chunk.getPos().getXEnd(); x++) {
            for (int z = chunk.getPos().getZStart(); z <= chunk.getPos().getZEnd(); z++) {
                for (int y = 0; y <= 256; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    IBlockState state = chunk.getBlockState(pos);
                    if (searchBlocks.contains(state.getBlock()))
                        addFound(new SearchBlock(new BlockPos(pos), state, true, null));
                }
            }
        }
    }

    class SearchBlock extends BlockPos {
        Color color;
        Pair<Boolean, Pair<Vec3d, Vec3d>>[] lineMap = new Pair[]{
                new Pair(true, new Pair(new Vec3d(0, 0, 0), new Vec3d(1, 0, 0))),
                new Pair(true, new Pair(new Vec3d(0, 0, 0), new Vec3d(0, 1, 0))),
                new Pair(true, new Pair(new Vec3d(0, 0, 0), new Vec3d(0, 0, 1))),
                new Pair(true, new Pair(new Vec3d(1, 0, 1), new Vec3d(1, 1, 1))),
                new Pair(true, new Pair(new Vec3d(1, 0, 1), new Vec3d(1, 0, 0))),
                new Pair(true, new Pair(new Vec3d(1, 0, 1), new Vec3d(0, 0, 1))),
                new Pair(true, new Pair(new Vec3d(1, 1, 0), new Vec3d(1, 0, 0))),
                new Pair(true, new Pair(new Vec3d(1, 1, 0), new Vec3d(0, 1, 0))),
                new Pair(true, new Pair(new Vec3d(1, 1, 0), new Vec3d(1, 1, 1))),
                new Pair(true, new Pair(new Vec3d(0, 1, 1), new Vec3d(0, 0, 1))),
                new Pair(true, new Pair(new Vec3d(0, 1, 1), new Vec3d(0, 1, 0))),
                new Pair(true, new Pair(new Vec3d(0, 1, 1), new Vec3d(1, 1, 1)))
        };
        boolean[] fill = new boolean[]{true, true, true, true, true, true};

        public SearchBlock(BlockPos pos, IBlockState state, boolean silent, BlockPos removed) {
            super(pos);
            Block block = state.getBlock();
            int i = 0;
            for (EnumFacing facing : EnumFacing.VALUES) {
                BlockPos p = pos.add(facing.getDirectionVec());
                IBlockState s = mc.world.getBlockState(p);
                if (block == s.getBlock() && !p.equals(removed)) {
                    fill[i] = false;
                    if (!silent) {
                        Chunk c = mc.world.getChunk(p);
                        if (found.containsKey(c) && found.get(c).contains(p)) {
                            found.get(c).remove(p);
                            addFound(new SearchBlock(p, s, true, null));
                        }
                    }
                    switch (facing) {
                        case UP:
                            lineMap[7].setKey(false);
                            lineMap[8].setKey(false);
                            lineMap[10].setKey(false);
                            lineMap[11].setKey(false);
                            break;
                        case DOWN:
                            lineMap[0].setKey(false);
                            lineMap[2].setKey(false);
                            lineMap[4].setKey(false);
                            lineMap[5].setKey(false);
                            break;
                        case WEST:
                            lineMap[1].setKey(false);
                            lineMap[2].setKey(false);
                            lineMap[9].setKey(false);
                            lineMap[10].setKey(false);
                            break;
                        case NORTH:
                            lineMap[0].setKey(false);
                            lineMap[1].setKey(false);
                            lineMap[6].setKey(false);
                            lineMap[7].setKey(false);
                            break;
                        case EAST:
                            lineMap[3].setKey(false);
                            lineMap[4].setKey(false);
                            lineMap[6].setKey(false);
                            lineMap[8].setKey(false);
                            break;
                        case SOUTH:
                            lineMap[3].setKey(false);
                            lineMap[5].setKey(false);
                            lineMap[9].setKey(false);
                            lineMap[11].setKey(false);
                            break;
                    }
                }
                i++;
            }
            doColor(block, state, pos);
        }

        private void doColor(Block block, IBlockState state, BlockPos pos) {
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
