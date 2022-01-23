package me.wallhacks.spark.systems.module.modules.render;

import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import net.minecraft.block.Block;
import me.wallhacks.spark.systems.setting.settings.BlockListSelectSetting;

@Module.Registration(name = "Xray", description = "Adds light")
public class Xray extends Module {


    BlockListSelectSetting blocks = new BlockListSelectSetting("Blocks", this,
            new Block[]{
                    Block.getBlockFromName("coal_ore"),
                    Block.getBlockFromName("iron_ore"),
                    Block.getBlockFromName("gold_ore"),
                    Block.getBlockFromName("redstone_ore"),
                    Block.getBlockFromName("lapis_ore"),
                    Block.getBlockFromName("diamond_ore"),
                    Block.getBlockFromName("emerald_ore"),
                    Block.getBlockFromName("quartz_ore"),
                    Block.getBlockFromName("clay"),
                    Block.getBlockFromName("glowstone"),
                    Block.getBlockFromName("crafting_table"),
                    Block.getBlockFromName("torch"),
                    Block.getBlockFromName("ladder"),
                    Block.getBlockFromName("tnt"),
                    Block.getBlockFromName("coal_block"),
                    Block.getBlockFromName("iron_block"),
                    Block.getBlockFromName("gold_block"),
                    Block.getBlockFromName("diamond_block"),
                    Block.getBlockFromName("emerald_block"),
                    Block.getBlockFromName("redstone_block"),
                    Block.getBlockFromName("lapis_block"),
                    Block.getBlockFromName("fire"),
                    Block.getBlockFromName("mossy_cobblestone"),
                    Block.getBlockFromName("mob_spawner"),
                    Block.getBlockFromName("end_portal_frame"),
                    Block.getBlockFromName("enchanting_table"),
                    Block.getBlockFromName("bookshelf"),
                    Block.getBlockFromName("command_block"),
                    Block.getBlockFromName("bed"),
                    Block.getBlockFromName("chest"),
                    Block.getBlockFromName("echest_chest"),
                    Block.getBlockFromName("trapped_chest"),
                    Block.getBlockFromName("spawner"),
                    Block.getBlockFromName("torch"),

            }, "General");


    public boolean isXrayBlock(Block o){
        return (blocks.contains(o));
    }

    @Override
    public void onDisable() {
        MC.mc.renderGlobal.loadRenderers();

    }

    @Override
    public void onEnable() {
        MC.mc.renderGlobal.loadRenderers();

    }
}
