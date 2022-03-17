package me.wallhacks.spark.systems.module.modules.render;


import me.wallhacks.spark.systems.setting.settings.*;
import me.wallhacks.spark.util.render.ColorUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.module.SearchChunksModule;
import me.wallhacks.spark.util.WorldUtils;
import me.wallhacks.spark.util.combat.HoleUtil;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.render.EspUtil;
import me.wallhacks.spark.util.render.RenderUtil;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Module.Registration(name = "HoleEsp", description = "shows holes near you")
public class HoleEsp extends SearchChunksModule<HoleEsp.HoleInfo> {

    IntSetting range = new IntSetting("Range",this,20,13,100);
    ModeSetting mode = new ModeSetting("Mode", this, "Glow", Arrays.asList("Box", "Glow"));
    DoubleSetting height = new DoubleSetting("MainHeight", this, 1.0D, -1.0D, 3.0D);
    DoubleSetting outlineWidth = new DoubleSetting("OutlineWidth", this, 2.0, 0.5, 5.0);
    BooleanSetting doubles = new BooleanSetting("Doubles", this, true);

    ColorSetting obsidian = new ColorSetting("ObsidianBox", this, new Color(144, 0, 255, 45));
    ColorSetting bedrock = new ColorSetting("BedrockBox", this, new Color(93, 235, 240, 45));

    BooleanSetting smooth = new BooleanSetting("SmoothColor", this, true);


    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent event) {
        if (nullCheck())
            return;

        int range = this.range.getValue();

        for (Chunk c: found.keySet()) {
            if(PlayerUtil.getDistance(c.getPos()) < range + 16)
            {
                if(found.get(c) != null)
                {
                    ArrayList<HoleInfo> info = new ArrayList<>(found.get(c));
                    for (HoleInfo holeInfo : info) {
                        double dis = RenderUtil.getRenderDistance(holeInfo);
                        if(dis < range){




                            double d = 1.0/MathHelper.clamp(range,2,10);
                            renderHole(holeInfo,MathHelper.clamp((range-dis)*d,0,1));
                        }

                    }
                }

            }

        }


    }

    @Override
    protected boolean needsAdjacentChunks() {
        return true;
    }





    public void renderHole(HoleInfo hole,double alpha) {

        int x = (hole.neighbour == null ? 0 : hole.neighbour.getXOffset());
        int z = (hole.neighbour == null ? 0 : hole.neighbour.getZOffset());

        AxisAlignedBB box = new AxisAlignedBB(hole.getX()+(Math.min(x, 0)), hole.getY(), hole.getZ()+(Math.min(z, 0)), hole.getX() + 1 + (Math.max(x, 0)), hole.getY() + height.getValue(), hole.getZ() + 1 + (Math.max(z, 0)));



        Color color = (smooth.isOn() ? ColorUtil.lerpColor(obsidian.getColor(alpha),bedrock.getColor(alpha),hole.getBedrockPercentage()) : (hole.isFullBedrock() ? bedrock.getColor(alpha) : obsidian.getColor(alpha)));



        if (mode.is("Box")) {
            EspUtil.boundingESPBox(box, color.brighter(), outlineWidth.getFloatValue());
            EspUtil.boundingESPBoxFilled(box, color);
        }
        if (mode.is("Glow")) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            EspUtil.drawGradientBlockOutline(box, new Color(0, 0, 0, 0), color.brighter(), outlineWidth.getFloatValue());
            EspUtil.drawOpenGradientBoxBB(box, color, new Color(0, 0, 0, 0));
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }
    }



    class HoleInfo extends BlockPos {

        final EnumFacing neighbour;
        final int bedrock;

        public float getBedrockPercentage() {
            return (float)bedrock / (neighbour == null ? 5 : 10);
        }

        public boolean isFullBedrock() {
            return bedrock >= (neighbour == null ? 5 : 10);
        }

        public HoleInfo(BlockPos pos, EnumFacing neighbour, int bedrock) {
            super(pos);
            this.neighbour = neighbour;
            this.bedrock = bedrock;

        }
    }


    @Override
    protected void blockChanged(BlockPos pos) {
        if(pos == null || nullCheck())
            return;
        List<BlockPos> blocks = WorldUtils.getSphere(pos,3,2,0);
        for (BlockPos p : blocks)
            removeFound(p);
        for (BlockPos p : blocks)
            checkBlock(p);
    }

    @Override
    protected void searchChunk(Chunk chunk) {

        ArrayList<Integer> layersToSearch = new ArrayList<>();
        heightLoop:
        for (int y = 0; y <= 256; y++) {
            int hardBlocks = 0;

            for (int x = chunk.getPos().getXStart(); x <= chunk.getPos().getXEnd(); x+=3) {
                for (int z = chunk.getPos().getZStart(); z <= chunk.getPos().getZEnd(); z++) {
                    Block bs = chunk.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if(bs == Blocks.OBSIDIAN || bs == Blocks.BEDROCK)
                    {
                        layersToSearch.add(y);
                        continue heightLoop;
                    }
                }

            }
        }

        for (int y : layersToSearch) {
            for (int x = chunk.getPos().getXStart(); x <= chunk.getPos().getXEnd(); x++) {
                for (int z = chunk.getPos().getZStart(); z <= chunk.getPos().getZEnd(); z++) {
                    checkBlock(new BlockPos(x, y, z));
                }
            }
        }


    }


    void checkBlock(BlockPos potentialHole) {
        if (!(mc.world.getBlockState(potentialHole).getBlock() instanceof BlockAir)) return;

        int bedrock = HoleUtil.isSingleHole(potentialHole);

        if (bedrock >= 0) {
            addFound(new HoleInfo(potentialHole,null,bedrock));
        }


        if (doubles.getValue()) {
            bedrock = HoleUtil.isDoubleHole(potentialHole,EnumFacing.EAST);

            if (bedrock >= 0)
                addFound(new HoleInfo(potentialHole, EnumFacing.EAST, bedrock));
            else  {
                bedrock = HoleUtil.isDoubleHole(potentialHole,EnumFacing.NORTH);
                if (bedrock >= 0)
                    addFound(new HoleInfo(potentialHole, EnumFacing.NORTH, bedrock));
            }
        }
    }

}
