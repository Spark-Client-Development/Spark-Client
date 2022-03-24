package me.wallhacks.spark.systems.module.modules.render;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.client.SettingChangeEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.module.SearchChunksModule;
import me.wallhacks.spark.systems.setting.settings.*;
import me.wallhacks.spark.util.objects.Hole;
import me.wallhacks.spark.util.objects.Pair;
import me.wallhacks.spark.util.objects.Timer;
import me.wallhacks.spark.util.render.ColorUtil;
import me.wallhacks.spark.util.render.EspUtil;
import me.wallhacks.spark.util.render.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
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
    BooleanSetting renderFullBlocks = new BooleanSetting("FullBlocks",this,false);
    BooleanSetting renderFill = new BooleanSetting("RenderFilled",this,false);

    @SubscribeEvent
    public void onSettingChange(SettingChangeEvent event) {
        if(mc.player == null)
            return;
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
            if (!found.containsKey(c) || found.get(c) == null) return;
            GL11.glBegin(GL11.GL_QUADS);

            if(renderFill.isOn())
            for (SearchBlock block : found.get(c)) {
                ColorUtil.glColor(new Color(block.color.getRed(), block.color.getGreen(), block.color.getBlue(), 120));
                int i = 0;
                double x = block.getX() - Minecraft.getMinecraft().getRenderManager().viewerPosX;
                double y = block.getY() - Minecraft.getMinecraft().getRenderManager().viewerPosY;
                double z = block.getZ() - Minecraft.getMinecraft().getRenderManager().viewerPosZ;
                AxisAlignedBB bb = block.axisAlignedBB.offset(x,y,z);
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
        super.blockChanged(pos);
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
        Pair<Boolean, Pair<Vec3d, Vec3d>>[] lineMap;
        AxisAlignedBB axisAlignedBB;
        boolean[] fill = new boolean[]{true, true, true, true, true, true};

        public SearchBlock(BlockPos pos, IBlockState state, boolean silent, BlockPos removed) {
            super(pos);
            Block block = state.getBlock();

           
            axisAlignedBB = mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos).offset(-pos.getX(),-pos.getY(),-pos.getZ());


            lineMap = new Pair[]{
                    new Pair(true, new Pair(new Vec3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ))),
                    new Pair(true, new Pair(new Vec3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ))),
                    new Pair(true, new Pair(new Vec3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ))),
                    new Pair(true, new Pair(new Vec3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ))),
                    new Pair(true, new Pair(new Vec3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ))),
                    new Pair(true, new Pair(new Vec3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ))),
                    new Pair(true, new Pair(new Vec3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ))),
                    new Pair(true, new Pair(new Vec3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ))),
                    new Pair(true, new Pair(new Vec3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ))),
                    new Pair(true, new Pair(new Vec3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ))),
                    new Pair(true, new Pair(new Vec3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ))),
                    new Pair(true, new Pair(new Vec3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ)))
            };


            for (int i = 0; i < EnumFacing.VALUES.length; i++) {
                EnumFacing facing = EnumFacing.VALUES[i];
                BlockPos p = pos.add(facing.getDirectionVec());
                IBlockState s = mc.world.getBlockState(p);
                if (block == s.getBlock() && !p.equals(removed)) {

                    AxisAlignedBB bb = renderFullBlocks.isOn() ? new AxisAlignedBB(0,0,0,1,1,1) : mc.world.getBlockState(p).getSelectedBoundingBox(mc.world, p).offset(-p.getX(),-p.getY(),-p.getZ());

                    if(!new AxisAlignedBB(0,0,0,1,1,1).equals(axisAlignedBB) || !new AxisAlignedBB(0,0,0,1,1,1).equals(bb))
                    switch (facing) {
                        case UP:
                            if(axisAlignedBB.maxY != 1)
                                continue;
                            if(bb.minY != 0)
                                continue;
                            if(axisAlignedBB.minX != bb.minX || axisAlignedBB.minZ != bb.minZ || axisAlignedBB.maxX != bb.maxX || axisAlignedBB.maxZ != bb.maxZ)
                                continue;
                            break;
                        case DOWN:
                            if(axisAlignedBB.minY != 0)
                                continue;
                            if(bb.maxY != 1)
                                continue;
                            if(axisAlignedBB.minX != bb.minX || axisAlignedBB.minZ != bb.minZ || axisAlignedBB.maxX != bb.maxX || axisAlignedBB.maxZ != bb.maxZ)
                                continue;
                            break;
                        case WEST:
                            if(axisAlignedBB.minX != 0)
                                continue;
                            if(bb.maxX != 1)
                                continue;
                            if(axisAlignedBB.minZ != bb.minZ || axisAlignedBB.minY != bb.minY || axisAlignedBB.maxZ != bb.maxZ || axisAlignedBB.maxY != bb.maxY)
                                continue;
                            break;
                        case NORTH:
                            if(axisAlignedBB.minZ != 0)
                                continue;
                            if(bb.maxZ != 1)
                                continue;
                            if(axisAlignedBB.minX != bb.minX || axisAlignedBB.minY != bb.minY || axisAlignedBB.maxX != bb.maxX || axisAlignedBB.maxY != bb.maxY)
                                continue;
                            break;
                        case EAST:
                            if(axisAlignedBB.maxX != 1)
                                continue;
                            if(bb.minX != 0)
                                continue;
                            if(axisAlignedBB.minZ != bb.minZ || axisAlignedBB.minY != bb.minY || axisAlignedBB.maxZ != bb.maxZ || axisAlignedBB.maxY != bb.maxY)
                                continue;
                            break;
                        case SOUTH:
                            if(axisAlignedBB.maxZ != 1)
                                continue;
                            if(bb.minZ != 0)
                                continue;
                            if(axisAlignedBB.minX != bb.minX || axisAlignedBB.minY != bb.minY || axisAlignedBB.maxX != bb.maxX || axisAlignedBB.maxY != bb.maxY)
                                continue;
                            break;
                    }
                    


                    if (!silent) {
                        Chunk c = mc.world.getChunk(p);
                        if (found.containsKey(c) && found.get(c).contains(p)) {
                            found.get(c).remove(p);
                            addFound(new SearchBlock(p, s, true, null));
                        }
                    }
                    fill[i] = false;
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

            }
            doColor(block, state, pos);
        }

        private void doColor(Block block, IBlockState state, BlockPos pos) {
            if (block == Blocks.PORTAL) {
                color = new Color(100, 0, 255);
            } else if (block == Blocks.ENDER_CHEST) {
                color = new Color(61, 28, 136);
            } else if (block == Blocks.IRON_BLOCK || block == Blocks.IRON_ORE) {
                color = new Color(126, 143, 145);
            } else if (block == Blocks.EMERALD_BLOCK || block == Blocks.EMERALD_ORE) {
                color = new Color(44, 147, 11);
            } else if (block == Blocks.REDSTONE_BLOCK || block == Blocks.REDSTONE_ORE) {
                color = new Color(145, 2, 37);
            } else if (block == Blocks.GOLD_BLOCK || block == Blocks.GOLD_ORE) {
                color = new Color(178, 155, 0);
            } else if (block == Blocks.LAPIS_BLOCK || block == Blocks.LAPIS_ORE) {
                color = new Color(20, 42, 152);
            } else if (block == Blocks.DIAMOND_ORE || block == Blocks.DIAMOND_BLOCK) {
                color = new Color(0, 115, 125);
            } else if (block == Blocks.COAL_BLOCK || block == Blocks.COAL_ORE) {
                color = new Color(52, 43, 43, 100);
            } else if (block == Blocks.QUARTZ_BLOCK || block == Blocks.QUARTZ_ORE || block == Blocks.QUARTZ_STAIRS) {
                color = new Color(193, 193, 211, 255);
            } else if (block instanceof BlockShulkerBox) {
                color = new Color(200, 100, 200);
            } else {
                color = new Color(mc.getBlockColors().getColor(state, mc.world, pos));
            }
        }
    }
}
