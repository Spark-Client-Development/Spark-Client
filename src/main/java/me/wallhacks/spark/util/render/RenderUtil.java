package me.wallhacks.spark.util.render;

import me.wallhacks.spark.util.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glColor4f;

public class RenderUtil implements MC {
    public static void glBillboardDistanceScaled(float x, float y, float z, Entity entity, float scale) {
        glBillboard(x, y, z);
        int distance = (int) entity.getDistance(x, y, z);
        float scaleDistance = (float) distance / 2.0f / (2.0f + (2.0f - scale));
        if (scaleDistance < 1.0f) {
            scaleDistance = 1.0f;
        }
        GlStateManager.scale(scaleDistance, scaleDistance, scaleDistance);
    }

    public static float getRenderDistance (BlockPos block){
        Vec3d pos = new Vec3d(block.getX()+0.5,block.getY()+0.5,block.getZ()+0.5);
        Vec3d eyes = mc.player.getPositionEyes(mc.getRenderPartialTicks());
        float f = (float)(eyes.x - pos.x);
        float f1 = (float)(eyes.y - pos.y);
        float f2 = (float)(eyes.z - pos.z);
        return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
    }
    public static float getRenderDistance (Vec3d vec){
        Vec3d pos = new Vec3d(vec.x,vec.y,vec.z);
        Vec3d eyes = mc.player.getPositionEyes(mc.getRenderPartialTicks());
        float f = (float)(eyes.x - pos.x);
        float f1 = (float)(eyes.y - pos.y);
        float f2 = (float)(eyes.z - pos.z);
        return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
    }


    public static void glBillboard(float x, float y, float z) {
        float scale = 0.02666667f;
        GlStateManager.translate((double) x - mc.getRenderManager().renderPosX, (double) y - mc.getRenderManager().renderPosY, (double) z - mc.getRenderManager().renderPosZ);
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-mc.getRenderViewEntity().rotationYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(mc.getRenderViewEntity().rotationPitch, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
    }

    public static void drawRect(float x, float y, float w, float h, int color) {
        float lvt52;
        float pdrawRect2 = x + w;
        float pdrawRect3 = y + h;
        if (x < pdrawRect2) {
            lvt52 = x;
            x = pdrawRect2;
            pdrawRect2 = lvt52;
        }

        if (y < pdrawRect3) {
            lvt52 = y;
            y = pdrawRect3;
            pdrawRect3 = lvt52;
        }

        float lvt53 = (float) (color >> 24 & 255) / 255.0F;
        float lvt61 = (float) (color >> 16 & 255) / 255.0F;
        float lvt71 = (float) (color >> 8 & 255) / 255.0F;
        float lvt81 = (float) (color & 255) / 255.0F;
        Tessellator lvt91 = Tessellator.getInstance();
        BufferBuilder lvt101 = lvt91.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(lvt61, lvt71, lvt81, lvt53);
        lvt101.begin(7, DefaultVertexFormats.POSITION);
        lvt101.pos(x, pdrawRect3, 0.0D).endVertex();
        lvt101.pos(pdrawRect2, pdrawRect3, 0.0D).endVertex();
        lvt101.pos(pdrawRect2, y, 0.0D).endVertex();
        lvt101.pos(x, y, 0.0D).endVertex();
        lvt91.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }


    public static void drawFilledCircle(int x, int y, double radius, int color) {
        glEnable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        glColor4f(((color >> 16) & 0xff) / 255F, ((color >> 8) & 0xff) / 255F, (color & 0xff) / 255F, ((color >> 24) & 0xff) / 255F);
        glBegin(GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360; i++)
            glVertex2d( x + Math.sin(((i * Math.PI) / 180)) * radius, y + Math.cos(((i * Math.PI) / 180)) * radius);
        glColor4f(1,1,1,1);
        glEnd();
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    public static void setColor(Color color) {
        GL11.glColor4d((double)((float)color.getRed() / 255.0f), (double)((float)color.getGreen() / 255.0f), (double)((float)color.getBlue() / 255.0f), (double)((float)color.getAlpha() / 255.0f));
    }

    public static float[] getViewRotations(Vec3d vec, EntityPlayer me)
    {
        Vec3d eyesPos = me.getPositionEyes(Minecraft.getMinecraft().getRenderPartialTicks());

        return getViewRotations(vec,eyesPos,me);
    }
    public static float[] getViewRotations(Vec3d vec, Vec3d eyesPos, EntityPlayer me)
    {
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        float[] myRot = new float[]{me.rotationYaw,me.rotationPitch};

        return new float[] {myRot[0] + MathHelper.wrapDegrees(yaw-myRot[0]), myRot[1]+MathHelper.wrapDegrees(pitch-myRot[1]) };

    }

    public static boolean isRenderLoop = false;


    public static void drawTracerPointer(float x, float y, float size, float widthDiv, float heightDiv, Color color, boolean outline, float outlineWidth, Color outlineColor) {
        boolean blend = GL11.glIsEnabled(3042);

        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();

        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);

        GL11.glBegin(7);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x - size / widthDiv, y + size);
        GL11.glVertex2d(x, y + size / heightDiv);
        GL11.glVertex2d(x + size / widthDiv, y + size);
        GL11.glVertex2d(x, y);
        GL11.glEnd();
        if (outline) {
            GL11.glLineWidth(outlineWidth);
            GL11.glColor4f(outlineColor.getRed() / 255.0f, outlineColor.getGreen() / 255.0f, outlineColor.getBlue() / 255.0f, outlineColor.getAlpha() / 255.0f);

            GL11.glBegin(2);
            GL11.glVertex2d(x, y);
            GL11.glVertex2d(x - size / widthDiv, y + size);
            GL11.glVertex2d(x, y + size / heightDiv);
            GL11.glVertex2d(x + size / widthDiv, y + size);
            GL11.glVertex2d(x, y);
            GL11.glEnd();
        }
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        if (!blend) {
            GL11.glDisable(3042);
        }
        GL11.glDisable(2848);
    }

    public static Vec3d interpolateEntityByTicks(Entity entity, float renderPartialTicks) {
        return new Vec3d (calculateDistanceWithPartialTicks(entity.posX, entity.lastTickPosX, renderPartialTicks) - mc.getRenderManager().renderPosX, calculateDistanceWithPartialTicks(entity.posY, entity.lastTickPosY, renderPartialTicks) - mc.getRenderManager().renderPosY, calculateDistanceWithPartialTicks(entity.posZ, entity.lastTickPosZ, renderPartialTicks) - mc.getRenderManager().renderPosZ);
    }

    public static double calculateDistanceWithPartialTicks(double originalPos, double finalPos, float renderPartialTicks) {
        return finalPos + (originalPos - finalPos) * mc.getRenderPartialTicks();
    }

    public static void drawOutlineLine(double left, double top, double right, double bottom, double width, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GL11.glLineWidth((float) width);

        if (left < right)
        {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom)
        {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float a = (float)(color >> 24 & 255) / 255.0F;
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(left, bottom, 0.0D).color(r, g, b, a).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).color(r, g, b, a).endVertex();
        bufferbuilder.pos(right, top, 0.0D).color(r, g, b, a).endVertex();
        bufferbuilder.pos(left, top, 0.0D).color(r, g, b, a).endVertex();
        bufferbuilder.pos(left, bottom, 0.0D).color(r, g, b, a).endVertex();
        tessellator.draw();
        GL11.glDisable(GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
    
    //You can use it to draw a circle by setting the corners to 360
    public static void drawPolygonOutline(double startDegree, double endDegree, int corners, int x, int y, int radius, float width, int color) {
    	double increment = 360 / (double) corners;
    	x += radius;
    	y += radius;
    	GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.depthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GL11.glLineWidth(width);

        float a = (float)(color >> 24 & 255) / 255.0F;
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for(double i = startDegree; i <= endDegree; i+=increment) {
        	bufferbuilder.pos(x-Math.cos(Math.toRadians(i))*radius, y-Math.sin(Math.toRadians(i))*radius, 0.0D).color(r, g, b, a).endVertex();
        }
        bufferbuilder.pos(x-Math.cos(Math.toRadians(endDegree))*radius, y-Math.sin(Math.toRadians(endDegree))*radius, 0.0D).color(r, g, b, a).endVertex();
        tessellator.draw();
        GL11.glDisable(GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
    
    //You can use it to draw a circle by setting the corners to 360
	public static void draw3DHorizontalPolygonOutline(double startDegree, double endDegree, int corners, double x, double y, double z, double radius, float width, int color) {
		double increment = 360 / (double) corners;
		x += radius;
		z += radius;
		GlStateManager.pushMatrix();
	    GlStateManager.enableBlend();
	    GlStateManager.disableDepth();
	    GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
	    GlStateManager.disableTexture2D();
	    GlStateManager.enableAlpha();
	    GlStateManager.depthMask(false);
	    GL11.glEnable(GL11.GL_LINE_SMOOTH);
	    GL11.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
	    GL11.glLineWidth(width);
	
	    float a = (float)(color >> 24 & 255) / 255.0F;
	    float r = (float)(color >> 16 & 255) / 255.0F;
	    float g = (float)(color >> 8 & 255) / 255.0F;
	    float b = (float)(color & 255) / 255.0F;
	
	    final Tessellator tessellator = Tessellator.getInstance();
	    final BufferBuilder bufferbuilder = tessellator.getBuffer();
	    bufferbuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
	    for(double i = startDegree; i <= endDegree; i+=increment) {
	    	bufferbuilder.pos(x-Math.cos(Math.toRadians(i))*radius, y, z-Math.sin(Math.toRadians(i))*radius).color(r, g, b, a).endVertex();
	    }
	    bufferbuilder.pos(x-Math.cos(Math.toRadians(endDegree))*radius, y, z-Math.sin(Math.toRadians(endDegree))*radius).color(r, g, b, a).endVertex();
	    tessellator.draw();
	    GL11.glDisable(GL_LINE_SMOOTH);
	    GlStateManager.depthMask(true);
	    GlStateManager.enableDepth();
	    GlStateManager.enableTexture2D();
	    GlStateManager.disableBlend();
	    GlStateManager.popMatrix();
	}
	
	public static void draw3DHorizontalPolygonOutline(double startDegree, double endDegree, int corners, Vec3d vector, double radius, float width, int color) {
		draw3DHorizontalPolygonOutline(startDegree, endDegree, corners, vector.x, vector.y, vector.z, radius, width, color);
	}
}
