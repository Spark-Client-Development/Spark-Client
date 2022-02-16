package me.wallhacks.spark.systems.module.modules.render;

import java.awt.Color;

import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.render.ColorUtil;
import me.wallhacks.spark.util.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import org.lwjgl.opengl.GL11;

@Module.Registration(name = "Crosshair", description = "Changes how your crosshair renders.")
public class Crosshair extends Module {
	public BooleanSetting dynamic = new BooleanSetting("Dynamic", this, false);
	public BooleanSetting attackIndicator = new BooleanSetting("AttackIndicator",this,  false);
	public BooleanSetting outline = new BooleanSetting("Outline",this,  false);
	public DoubleSetting lineWidth = new DoubleSetting("LineWidth",this,   1.0, 0.0, 5.0);
	public DoubleSetting length = new DoubleSetting("Length",this,  10.0, 0.0, 20.0);
	public DoubleSetting thick = new DoubleSetting("Thick", this, 10.0, 0.0, 20.0);
	public DoubleSetting gap = new DoubleSetting("Gap", this, 10.0, 0.0, 20.0);
	public ColorSetting colour = new ColorSetting("Colour", this, new Color(0xD83535), false);
	
	@SubscribeEvent
	public void onCrosshairRender(RenderGameOverlayEvent.Pre e) {
		if(e.getType() == ElementType.CROSSHAIRS) {
			if (e.isCanceled()) return;
			e.setCanceled(true);
			int red = colour.getColor().getRed();
			int green = colour.getColor().getGreen();
			int blue = colour.getColor().getBlue();
			int alpha = colour.getColor().getAlpha();

			int color = new Color(red, green, blue, alpha).getRGB();
			int black = new Color(0, 0, 0, 255).getRGB();
			
			ScaledResolution resolution = new ScaledResolution(mc);

			RenderUtil.drawRect((resolution.getScaledWidth()/2) - gap.getFloatValue() - length.getFloatValue() - (moving() ? 2 : 0), (resolution.getScaledHeight()/2) - (thick.getFloatValue()/2), length.getFloatValue(), thick.getFloatValue(), color);
			RenderUtil.drawRect((resolution.getScaledWidth()/2) + gap.getFloatValue() + (moving() ? 2 : 0), (resolution.getScaledHeight()/2) - (thick.getFloatValue()/2), length.getFloatValue(), thick.getFloatValue(), color);
			RenderUtil.drawRect((resolution.getScaledWidth()/2) - (thick.getFloatValue()/2), (resolution.getScaledHeight()/2) - gap.getFloatValue() - length.getFloatValue() - (moving() ? 2 : 0), thick.getFloatValue(), length.getFloatValue(), color);
			RenderUtil.drawRect((resolution.getScaledWidth()/2) - (thick.getFloatValue()/2), (resolution.getScaledHeight()/2) + gap.getFloatValue() + (moving() ? 2 : 0), thick.getFloatValue(), length.getFloatValue(), color);

			if(outline.getValue()) {
				RenderUtil.drawOutlineLine((resolution.getScaledWidth()/2) - gap.getFloatValue() - length.getFloatValue() - (moving() ? 2 : 0), (resolution.getScaledHeight()/2) - (thick.getFloatValue()/2), (resolution.getScaledWidth()/2) - gap.getFloatValue() - (moving() ? 2 : 0), (resolution.getScaledHeight()/2) + (thick.getFloatValue()/2), lineWidth.getFloatValue(), black);
				RenderUtil.drawOutlineLine((resolution.getScaledWidth()/2) + gap.getFloatValue() + (moving() ? 2 : 0), (resolution.getScaledHeight()/2) - (thick.getFloatValue()/2), (resolution.getScaledWidth()/2) + length.getFloatValue() + gap.getFloatValue() + (moving() ? 2 : 0), (resolution.getScaledHeight()/2) + (thick.getFloatValue()/2), lineWidth.getFloatValue(), black);
				RenderUtil.drawOutlineLine((resolution.getScaledWidth()/2) - (thick.getFloatValue()/2), (resolution.getScaledHeight()/2) - gap.getFloatValue() - length.getFloatValue() - (moving() ? 2 : 0), (resolution.getScaledWidth()/2) + (thick.getFloatValue()/2), (resolution.getScaledHeight()/2) - gap.getFloatValue() - (moving() ? 2 : 0), lineWidth.getFloatValue(), black);
				RenderUtil.drawOutlineLine((resolution.getScaledWidth()/2) - (thick.getFloatValue()/2), (resolution.getScaledHeight()/2) + gap.getFloatValue() + (moving() ? 2 : 0), (resolution.getScaledWidth()/2) + (thick.getFloatValue()/2), (resolution.getScaledHeight()/2) + length.getFloatValue() + gap.getFloatValue() + (moving() ? 2 : 0), lineWidth.getFloatValue(), black);
			}

			if(attackIndicator.getValue()) {
				float f = mc.player.getCooledAttackStrength(0.0F);
				if (f < 1.0F) {
					int k = (int) (f * 20.0F);
					RenderUtil.drawRect((resolution.getScaledWidth() / 2) - (10), ((resolution.getScaledHeight() / 2) + gap.getFloatValue() + length.getFloatValue() + (moving() ? 2 : 0) + 2), k, 2, color);
					RenderUtil.drawOutlineLine((resolution.getScaledWidth() / 2) - (10), (resolution.getScaledHeight() / 2) + gap.getFloatValue() + length.getFloatValue() + (moving() ? 2 : 0) + 2, (resolution.getScaledWidth() / 2) - (10) + k, (resolution.getScaledHeight() / 2) + gap.getFloatValue() + length.getFloatValue() + (moving() ? 2 : 0) + 4, 1.0f, black);
				}
			}
			ColorUtil.glColor(new Color(255, 255, 255));
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
	}

	public boolean moving() {
		if((mc.player.isSneaking() || mc.player.moveStrafing != 0 || mc.player.moveForward != 0 || !mc.player.onGround) && dynamic.getValue()) {
			return true;
		}
		return false;
	}
}
