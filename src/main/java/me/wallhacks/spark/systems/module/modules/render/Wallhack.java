package me.wallhacks.spark.systems.module.modules.render;

import me.wallhacks.spark.event.client.SettingChangeEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import net.minecraft.block.Block;
import me.wallhacks.spark.systems.setting.settings.BlockListSelectSetting;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//I obviously took this module from summit https://github.com/ionar2/summit and I honestly dont feel bad about it
@Module.Registration(name = "Wallhack", description = "Best module ever no doubt")
public class Wallhack extends Module {
    public static Wallhack INSTANCE;
    public Wallhack() { INSTANCE = this; }
    private BooleanSetting softReload = new BooleanSetting("SoftReload", this, true);
    public DoubleSetting opacity = new DoubleSetting("Opacity", this, 125.0, 0.0, 255.0);
    private BooleanSetting xray = new BooleanSetting("XRay", this, true);
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

            }, v -> (xray.isOn()));

    @Override
    public void onEnable() {
        mc.renderChunksMany = false;
        reloadWorld();
        ForgeModContainer.forgeLightPipelineEnabled = false;
    }

    @SubscribeEvent
    public void onSettingChange(SettingChangeEvent event) {
        if (event.getSetting() == opacity || event.getSetting() == blocks || event.getSetting() == xray)
            reloadWorld();
    }


    @Override
    public void onDisable() {
        mc.renderChunksMany = false;
        reloadWorld();
        ForgeModContainer.forgeLightPipelineEnabled = true;
    }

    private void reloadWorld()
    {
        if (mc.world == null || mc.renderGlobal == null)
            return;

        if (softReload.getValue()) {
            mc.addScheduledTask(() -> {
                int x = (int) mc.player.posX;
                int y = (int) mc.player.posY;
                int z = (int) mc.player.posZ;

                int distance = mc.gameSettings.renderDistanceChunks * 16;

                mc.renderGlobal.markBlockRangeForRenderUpdate(x - distance, y - distance, z - distance, x + distance, y + distance, z + distance);
            });
        }
        else
            mc.renderGlobal.loadRenderers();
    }

    public boolean isXrayBlock(Block o){
        if (this.isEnabled() && xray.getValue())
            return (blocks.contains(o));
        else return false;
    }
}
