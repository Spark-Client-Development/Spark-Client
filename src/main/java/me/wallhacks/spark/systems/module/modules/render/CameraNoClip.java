package me.wallhacks.spark.systems.module.modules.render;

import me.wallhacks.spark.event.client.SettingChangeEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BlockListSelectSetting;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import net.minecraft.block.Block;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//I obviously took this module from summit https://github.com/ionar2/summit and I honestly dont feel bad about it
@Module.Registration(name = "CameraNoClip", description = "Best module ever no doubt")
public class CameraNoClip extends Module {
    public static CameraNoClip INSTANCE;
    public CameraNoClip() { INSTANCE = this; }


}
