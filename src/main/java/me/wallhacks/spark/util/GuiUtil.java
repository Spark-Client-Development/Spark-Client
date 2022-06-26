package me.wallhacks.spark.util;

import me.wallhacks.spark.util.render.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class GuiUtil implements MC {
    public static void drawCompleteImage(double posX, double posY, double width, double height) {
        GL11.glPushMatrix();
        GL11.glEnable(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glTranslated(posX, posY, 0.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3d(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3d(0.0f, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3d(width, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3d(width, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public static void setup(int color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glColor4f(((color >> 16) & 0xff) / 255F, ((color >> 8) & 0xff) / 255F, (color & 0xff) / 255F, ((color >> 24) & 0xff) / 255F);
        glBegin(GL_TRIANGLE_FAN);
    }

    public static void finish() {
        glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void rounded(int x, int y, int right, int bottom, int color, int radius) {
        setup(color);
        corner(right - radius, bottom - radius, radius, 0, 90);
        corner(right - radius, y + radius, radius, 90, 180);
        corner(x + radius, y + radius, radius, 180, 270);
        corner(x + radius, bottom - radius, radius, 270, 360);
        finish();
    }


    public static void corner(int x, int y, double radius, int start, int stop) {
        int i = start;
        while (true) {
            i = Math.min(stop, i+5);
            glVertex2d(x + Math.sin(((i * Math.PI) / 180)) * radius, y + Math.cos(((i * Math.PI) / 180)) * radius);
            if (i==stop) break;
        }
    }

    public static String getLoadingText(boolean text) {
        long time = (System.currentTimeMillis() % 600);
        String dot = ".";
        if (time > 200) dot += ".";
        if (time > 400) dot += ".";
        return (text ? "Loading" : "") + dot;
    }

    public static void renderPlayerHead(NetworkPlayerInfo networkPlayerInfo, int x, int y, int size) {

        GlStateManager.pushAttrib();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);


        Minecraft.getMinecraft().getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
        int l5 = 8;
        int i3 = 8;
        Gui.drawScaledCustomSizeModalRect(x, y, 8.0F, (float) l5, 8, i3, size, size, 64.0F, 64.0F);
        if (true) {
            int j3 = 8;
            int k3 = 8;
            Gui.drawScaledCustomSizeModalRect(x, y, 40.0F, (float) j3, 8, k3, size, size, 64.0F, 64.0F);
        }
        GlStateManager.disableBlend();
        GlStateManager.popAttrib();
    }



    public static void drawCompleteImage(double posX, double posY, double width, double height, ResourceLocation image, Color c) {

        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        ColorUtil.glColor(c);
        drawCompleteImage(posX, posY, width, height);

        GlStateManager.color(1, 1, 1, 1);
    }


    public static void drawCompleteImageRotated(float posX, float posY, float width, float height, float rotation, ResourceLocation image, Color c) {

        GL11.glPushMatrix();
        GlStateManager.translate(posX + width / 2, posY + height / 2, 0.0F);
        GlStateManager.rotate(rotation, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(-width / 2, -height / 2, 0.0F);
        GuiUtil.drawCompleteImage(0, 0, width, height, image, c);
        GL11.glPopMatrix();
    }

    public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent) {
        mouseX = posX - mouseX;
        mouseY = posY - mouseY - scale/7*6;
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GL11.glDepthMask(true);
        GL11.glEnable(GL_DEPTH_TEST);
        GlStateManager.translate((float)posX, (float)posY, 50.0F);
        GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        ent.renderYawOffset = (float)Math.atan((double)(mouseX / 40.0F)) * 20.0F;
        ent.rotationYaw = (float)Math.atan((double)(mouseX / 40.0F)) * 40.0F;
        ent.rotationPitch = -((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GL11.glDisable(GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, int coltl, int colbl, int coltr, int colbr, int zLevel) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.shadeModel(GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(right, top, zLevel).color((coltr & 0x00ff0000) >> 16, (coltr & 0x0000ff00) >> 8,
                (coltr & 0x000000ff), (coltr & 0xff000000) >>> 24).endVertex();
        buffer.pos(left, top, zLevel).color((coltl & 0x00ff0000) >> 16, (coltl & 0x0000ff00) >> 8, (coltl & 0x000000ff),
                (coltl & 0xff000000) >>> 24).endVertex();
        buffer.pos(left, bottom, zLevel).color((colbl & 0x00ff0000) >> 16, (colbl & 0x0000ff00) >> 8,
                (colbl & 0x000000ff), (colbl & 0xff000000) >>> 24).endVertex();
        buffer.pos(right, bottom, zLevel).color((colbr & 0x00ff0000) >> 16, (colbr & 0x0000ff00) >> 8,
                (colbr & 0x000000ff), (colbr & 0xff000000) >>> 24).endVertex();
        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
    public static void drawQuad(float left, float top, float width, float height, int color) {
        drawRect(left,top,left+width,top+height,color);
    }

    public static void drawRect(float left, float top, float right, float bottom, int color) {
        float j;
        if (left < right) {
            j = left;
            left = right;
            right = j;
        }

        if (top < bottom) {
            j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos((double)left, (double)bottom, 0.0D).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, 0.0D).endVertex();
        bufferbuilder.pos((double)right, (double)top, 0.0D).endVertex();
        bufferbuilder.pos((double)left, (double)top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }


    static Vec3d glTransformOffset = new Vec3d(0, 0, 0);

    public static Vec3d getGlTransformOffset() {
        return glTransformOffset;
    }

    public static void resetGlScissorOffset() {
        GuiUtil.glTransformOffset = new Vec3d(0, 0, 0);
    }
    public static void addGlScissorOffset(Vec3d glScissorOffset) {
        GuiUtil.glTransformOffset = GuiUtil.glTransformOffset.add(glScissorOffset);
    }

    public static void glScissor(int x, int y, int width, int height) {
        ScaledResolution scr = new ScaledResolution(mc);
        GL11.glScissor((x+ (int) GuiUtil.getGlTransformOffset().x) * scr.getScaleFactor(), (scr.getScaledHeight() - y - height - (int) GuiUtil.getGlTransformOffset().y) * scr.getScaleFactor(), width * scr.getScaleFactor(), height * scr.getScaleFactor());
    }

    public static void drawHorizontalLine(int startX, int endX, int y, int color) {
        if (endX < startX) {
            int i = startX;
            startX = endX;
            endX = i;
        }

        Gui.drawRect(startX, y, endX + 1, y + 1, color);
    }

    public static void drawVerticalLine(int x, int startY, int endY, int color) {
        if (endY < startY) {
            int i = startY;
            startY = endY;
            endY = i;
        }

        Gui.drawRect(x, startY + 1, x + 1, endY, color);
    }


    public static void linePre(Color color,float width) {
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();

        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);

        GL11.glLineWidth(width);




    }

    public static void linePost() {
        GL11.glDisable(3042);

        GL11.glColor4f(1f,1f,1f,1f);

        GL11.glPopMatrix();
        GL11.glEnable(3553);

        GL11.glDisable(2848);
    }
}
