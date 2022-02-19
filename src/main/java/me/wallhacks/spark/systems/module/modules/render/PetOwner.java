package me.wallhacks.spark.systems.module.modules.render;

import java.awt.Color;

import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.util.SessionUtils;
import me.wallhacks.spark.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "PetOwner", description = "Displays the owner of tamed entities")
public class PetOwner extends Module {
	public DoubleSetting scale = new DoubleSetting("Scale", this, 1.0, 0.0, 10.0);
	public DoubleSetting scaleByDistance = new DoubleSetting("ScaleByDistance", this, 1,0,1);
	ColorSetting color = new ColorSetting("Color", this, new Color(0xFFFFFFFF, true));

	@SubscribeEvent
	public void onRenderWorld(RenderWorldLastEvent event) {
		if (nullCheck() || mc.renderEngine == null || mc.getRenderManager().options == null)
			return;

		for (Entity entity : Minecraft.getMinecraft().world.getLoadedEntityList()) {
			if (entity instanceof EntityTameable) {
				if (((EntityTameable) entity).isTamed()) {
					String ownerName = SessionUtils.getname(((EntityTameable) entity).getOwnerId());
					renderString(entity, "Owned by: " + ownerName, event.getPartialTicks());
				}
			} else if (entity instanceof AbstractHorse) {
				if (((AbstractHorse) entity).isTame()) {
					String ownerName = SessionUtils.getname(((AbstractHorse) entity).getOwnerUniqueId());
					renderString(entity, "Owned by: " + ownerName, event.getPartialTicks());
				}
			}
		}
	}

	void renderString(Entity entity, String displayString, float partialTicks) {
		Entity viewEntity = mc.getRenderViewEntity();
		Vec3d nametagPosition = RenderUtil.interpolateEntityByTicks(entity, partialTicks);

		double x = nametagPosition.x;
		double y = nametagPosition.y;
		double z = nametagPosition.z;

		nametagPosition = RenderUtil.interpolateEntityByTicks(viewEntity, partialTicks);

		double posX = viewEntity.posX;
		double posY = viewEntity.posY;
		double posZ = viewEntity.posZ;

		viewEntity.posX = nametagPosition.x;
		viewEntity.posY = nametagPosition.y;
		viewEntity.posZ = nametagPosition.z;

		double distance = viewEntity.getDistance(x, y, z);

		double distanceScale = scale.getValue() + (scale.getValue() / 5) * distance * scaleByDistance.getValue();

		float width = mc.fontRenderer.getStringWidth(displayString) / 2;
		float height = mc.fontRenderer.FONT_HEIGHT;

		GlStateManager.pushMatrix();

		GlStateManager.enablePolygonOffset();
		GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
		GlStateManager.translate((float) x, (float) y + entity.height, (float) z);
		GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
		GlStateManager.rotate(mc.getRenderManager().playerViewX, (mc.gameSettings.thirdPersonView == 2) ? -1.0f : 1.0f, 0.0f, (float) 0);
		GlStateManager.scale(-(distanceScale / 100), -(distanceScale / 100), (distanceScale / 100));

		mc.fontRenderer.drawStringWithShadow(displayString, (int)-width + 1, (int)-height + 3, color.getRGB());

		GlStateManager.pushMatrix();

		GlStateManager.popMatrix();

		GlStateManager.enableDepth();
		GlStateManager.disableBlend();
		GlStateManager.disablePolygonOffset();
		GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
		GlStateManager.popMatrix();

		mc.getRenderViewEntity().posX = posX;
		mc.getRenderViewEntity().posY = posY;
		mc.getRenderViewEntity().posZ = posZ;
	}
}

