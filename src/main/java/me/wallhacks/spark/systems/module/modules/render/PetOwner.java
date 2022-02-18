package me.wallhacks.spark.systems.module.modules.render;

import org.lwjgl.opengl.GL11;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.SessionUtils;
import me.wallhacks.spark.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Registration(name = "PetOwner", description = "Renders logout spots for enemy", alwaysListening = true)
public class PetOwner extends Module {

    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent event) {
        if (this.isEnabled() && !nullCheck()) {
        	for (Entity entity : Minecraft.getMinecraft().world.getLoadedEntityList()) {
        		if (entity instanceof EntityTameable) {
        			if (((EntityTameable) entity).isTamed()) {
        				String ownerName = SessionUtils.getname(((EntityTameable) entity).getOwnerId());
        				renderOwner(entity, ownerName);
        			}
        		} else if (entity instanceof AbstractHorse) {
        			if (((AbstractHorse) entity).isTame()) {
        				String ownerName = SessionUtils.getname(((AbstractHorse) entity).getOwnerUniqueId());
        				renderOwner(entity, ownerName);
        			}
        		}
        	}
        }
    }
    
    void renderOwner(Entity entity, String ownerName) {
    	Vec3d center = entity.getPositionVector();
    	GL11.glPushMatrix();
    	RenderUtil.glBillboardDistanceScaled((float) center.x, (float) (center.y + 1.2), (float) center.z, mc.player, 1);
    	String s = "Owned by: " + ownerName;
    	GL11.glDisable(GL11.GL_DEPTH_TEST);
    	mc.fontRenderer.drawStringWithShadow(s, -Spark.fontManager.getTextWidth(s)/2, 0, 0xffffff);
    	GL11.glPopMatrix();
    	GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
}
