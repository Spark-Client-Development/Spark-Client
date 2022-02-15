package me.wallhacks.spark.systems.module.modules.render;

import java.awt.Color;

import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.render.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;

@Module.Registration(name = "CustomCrosshair", description = "Changes how your crosshair renders.")
public class CustomCrosshair extends Module {

	IntSetting scale = new IntSetting("Scale", this, 3, 0, 10);
	IntSetting edges = new IntSetting("Edges", this, 5, 0, 20);
	IntSetting speed = new IntSetting("Speed", this, 1, 0, 10);
	IntSetting width = new IntSetting("Width", this, 1, 1, 5);
	ColorSetting color = new ColorSetting("Color", this, Color.GREEN);
	
	double rotation = 0;
	
	@SubscribeEvent
	public void onCrosshairRender(RenderGameOverlayEvent.Pre e) {
		if(e.getType() == ElementType.CROSSHAIRS) {
			e.setCanceled(true);
			ScaledResolution src = new ScaledResolution(mc);
			RenderUtil.drawPolygonOutline(0+rotation, 360+rotation, edges.getValue(), src.getScaledWidth()/2-scale.getValue(), src.getScaledHeight()/2-scale.getValue(), scale.getValue(), width.getValue(), color.getColor().getRGB());
			rotation = (rotation + speed.getValue() * e.getPartialTicks()) % 360;
		}
	}
}
