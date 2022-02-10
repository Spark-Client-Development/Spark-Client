package me.wallhacks.spark.systems.module.modules.render;

import baritone.api.event.events.WorldEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.*;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.render.RenderUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Module.Registration(name = "Fullbright", description = "Adds light")
public class FullBright extends Module {



	@Override
	public void onEnable() {
		MC.mc.gameSettings.gammaSetting = 100f;
	}

	@Override
	public void onDisable() {
		MC.mc.gameSettings.gammaSetting = 1f;
	}
}
