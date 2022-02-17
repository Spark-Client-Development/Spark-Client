package me.wallhacks.spark.systems.module.modules.render;


import me.wallhacks.spark.systems.setting.settings.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
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
    ColorSetting obsidianOL = new ColorSetting("ObsidianOutline", this, new Color(144, 0, 255, 144));
    ColorSetting bedrockOL = new ColorSetting("BedrockOutline", this, new Color(93, 235, 240, 144));

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

                            //anti fuckup
                            if(!isBlockHole(holeInfo))
                            {
                                chunksToSearch.add(c.getPos());
                                break;
                            }

                            double d = 1.0/MathHelper.clamp(range,2,10);
                            renderHole(holeInfo,holeInfo.type,holeInfo.length,holeInfo.width, MathHelper.clamp((range-dis)*d,0,1));
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




    public void renderHole(BlockPos hole, Type type, double length, double width,double alpha) {
        AxisAlignedBB box = new AxisAlignedBB(hole.getX(), hole.getY(), hole.getZ(), hole.getX() + 1 + length, hole.getY() + height.getValue(), hole.getZ() + width + 1);
        Color color = type == Type.Bedrock ? bedrockOL.getColor(alpha) : obsidianOL.getColor(alpha);



        if (mode.is("Box")) {
            EspUtil.boundingESPBox(box, color, outlineWidth.getFloatValue());
            color = type == Type.Bedrock ? bedrock.getColor(alpha) : obsidian.getColor(alpha);
            EspUtil.boundingESPBoxFilled(box, color);
        }
        if (mode.is("Glow")) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            EspUtil.drawGradientBlockOutline(box, new Color(0, 0, 0, 0), color, outlineWidth.getFloatValue());
            color = type == Type.Bedrock ? bedrock.getColor(alpha) : obsidian.getColor(alpha);
            EspUtil.drawOpenGradientBoxBB(box, color, new Color(0, 0, 0, 0));
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }
    }

    public enum Type {
        Obsidian,
        Bedrock,
        Double
    }

    class HoleInfo extends BlockPos {

        Type type;
        int length;
        int width;

        public HoleInfo(BlockPos pos, Type type, int length, int width) {
            super(pos);
            this.type = type;
            this.length = length;
            this.width = width;
        }
    }


    @Override
    protected void blockChanged(BlockPos pos) {
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

        if (HoleUtil.isBedRockHole(potentialHole))
            addFound(new HoleInfo(potentialHole, Type.Bedrock, 0, 0));
        else if (HoleUtil.isObsidianHole(potentialHole))
            addFound(new HoleInfo(potentialHole, Type.Obsidian, 0, 0));

        if (doubles.getValue()) {
            if (HoleUtil.isDoubleBedrockHoleX(potentialHole))
                addFound(new HoleInfo(potentialHole, Type.Bedrock, 1, 0));
            else if (HoleUtil.isDoubleBedrockHoleZ(potentialHole))
                addFound(new HoleInfo(potentialHole, Type.Bedrock, 0, 1));
            else if (HoleUtil.isDoubleObsidianHoleX(potentialHole))
                addFound(new HoleInfo(potentialHole, Type.Obsidian, 1, 0));
            else if (HoleUtil.isDoubleObsidianHoleZ(potentialHole))
                addFound(new HoleInfo(potentialHole, Type.Obsidian, 0, 1));
        }
    }

    boolean isBlockHole(BlockPos potentialHole) {
        if (!(mc.world.getBlockState(potentialHole).getBlock() instanceof BlockAir)) return false;

        if (HoleUtil.isBedRockHole(potentialHole))
            return true;
        else if (HoleUtil.isObsidianHole(potentialHole))
            return true;

        if (doubles.getValue()) {
            if (HoleUtil.isDoubleBedrockHoleX(potentialHole))
                return true;
            else if (HoleUtil.isDoubleBedrockHoleZ(potentialHole))
                return true;
            else if (HoleUtil.isDoubleObsidianHoleX(potentialHole))
                return true;
            else if (HoleUtil.isDoubleObsidianHoleZ(potentialHole))
                return true;
        }
        return false;
    }

}
