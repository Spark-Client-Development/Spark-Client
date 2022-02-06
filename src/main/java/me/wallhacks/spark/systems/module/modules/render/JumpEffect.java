package me.wallhacks.spark.systems.module.modules.render;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.render.RenderUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@Module.Registration(name = "JumpEffect", description = "Renders effects when you jump")
public class JumpEffect extends Module {

	final String SHAPE = "Shape";
	IntSetting vertices = new IntSetting("Vertices", this, 3, 2, 20, SHAPE);
	DoubleSetting radius = new DoubleSetting("Radius", this, 1, 1, 5, SHAPE);
	BooleanSetting doubled = new BooleanSetting("Doubled", this, true, SHAPE);
	
	final String PROPERTIES = "Properties";
	DoubleSetting thickness = new DoubleSetting("Thickness", this, 1, 1, 20, PROPERTIES);
	ColorSetting color = new ColorSetting("Color", this, Color.CYAN, PROPERTIES);
	
	final String ANIMATION = "Animation";
	IntSetting duration = new IntSetting("Duration", this, 20, 0, 100, ANIMATION);
	BooleanSetting spin = new BooleanSetting("Spin", this, true, ANIMATION);
	ModeSetting fade = new ModeSetting("Fade", this, "FadeOut", Arrays.asList("FadeOut", "FadeIn", "None"), ANIMATION);
	ModeSetting sizechange = new ModeSetting("SizeChange", this, "Grow", Arrays.asList("Grow", "Shrink", "None"), ANIMATION);
	
	static List<RenderEffect> effects = new CopyOnWriteArrayList<>();
	
	private static class RenderEffect {
		private int ticksExisted;
		private Vec3d pos;
		private IntSetting duration;
		
		public RenderEffect(IntSetting duration) {
			this.pos = mc.player.getPositionVector().add(0, -mc.player.motionY-0.03, 0);
			this.ticksExisted = 0;
			this.duration = duration;
			effects.add(this);
		}	
		
		public void update() {
			ticksExisted++;
			if(ticksExisted >= duration.getValue()) {
				effects.remove(this);
			}
		}
		
		public double getDelta(float partialTicks) {
			return MathHelper.clamp((ticksExisted-1 + 1 * partialTicks) / duration.getNumber(), 0, 1);
		}
		
		public Vec3d getCameraRelativePos() {
			return pos.add(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);
		}
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent e) {
		if(e.phase == Phase.END)
			effects.forEach(effect -> effect.update());
	}
	
	Boolean prevOnGround = null;
	
	@SubscribeEvent
	public void onJump(LivingUpdateEvent e) {
		if(e.getEntity() == mc.player) {
			if(prevOnGround != null) {
				if(prevOnGround && !mc.player.onGround && mc.player.motionY > 0) {
					new RenderEffect(duration);
				}
			}
			prevOnGround = mc.player.onGround;
		}
	}
	
	@SubscribeEvent
	public void onRender(RenderWorldLastEvent e) {
		for(RenderEffect effect : effects) {
			double delta = effect.getDelta(e.getPartialTicks());
			double radius = this.radius.getNumber();
			if(sizechange.getValueIndex() < 2) {
				if(sizechange.getValueIndex() == 0) {
					radius *= delta;
				} else {
					radius *= (1-delta);
				}
			}
			int c = color.getRGB();
			if(fade.getValueIndex() < 2) {
				Color noAlpha = new Color(color.getColor().getRed(), color.getColor().getGreen(), color.getColor().getBlue());
				if(fade.getValueIndex() == 0) {
					c = new Color(noAlpha.getRed(), noAlpha.getGreen(), noAlpha.getBlue(), (int)(color.getColor().getAlpha()*(1-delta))).getRGB();
				} else {
					c = new Color(noAlpha.getRed(), noAlpha.getGreen(), noAlpha.getBlue(), (int)(color.getColor().getAlpha()*delta)).getRGB();
				}
			}
			double angle = 0;
			if(spin.getValue()) {
				angle = 360*delta;
			}
			RenderUtil.draw3DHorizontalPolygonOutline(angle, 360+angle, vertices.getValue(), effect.getCameraRelativePos().add(-radius, 0, -radius), radius, (float)thickness.getNumber(), c);
			if(doubled.getValue()) {
				double offset = 360d/vertices.getValue()/2;
				RenderUtil.draw3DHorizontalPolygonOutline(offset+angle, 360+offset+angle, vertices.getValue(), effect.getCameraRelativePos().add(-radius, 0, -radius), radius, (float)thickness.getNumber(), c);
			}
		}
	}
}
