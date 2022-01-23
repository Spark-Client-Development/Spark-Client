package me.wallhacks.spark.util;

import me.wallhacks.spark.Spark;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import me.wallhacks.spark.util.render.ColorUtil;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class GuiUtil {
    public static void drawCompleteImage(float posX, float posY, float width, float height) {
        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(width, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(width, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public static String getLoadingText(boolean text) {
        long time = (System.currentTimeMillis() % 300);
        String dot = ".";
        if (time > 100) dot += ".";
        if (time > 200) dot += ".";
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
        GlStateManager.popAttrib();
    }


    public static void drawCompleteImage(float posX, float posY, float width, float height, ResourceLocation image, Color c) {

        Minecraft.getMinecraft().getTextureManager().bindTexture(image);

        GlStateManager.color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f);
        drawCompleteImage(posX, posY, width, height);

        GlStateManager.color(1, 1, 1, 1);
    }


    public static void drawCompleteImageRotated(float posX, float posY, float width, float height, int rotation, ResourceLocation image, Color c) {

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

    public static void drawPickerBase(int pickerX, int pickerY, int pickerWidth, int pickerHeight, float red, float green, float blue, float alpha) {
        GL11.glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glShadeModel(GL_SMOOTH);
        glBegin(GL_POLYGON);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glVertex2f(pickerX, pickerY);
        glVertex2f(pickerX, pickerY + pickerHeight);
        glColor4f(red, green, blue, alpha);
        glVertex2f(pickerX + pickerWidth, pickerY + pickerHeight);
        glVertex2f(pickerX + pickerWidth, pickerY);
        glEnd();
        glDisable(GL_ALPHA_TEST);
        glBegin(GL_POLYGON);
        glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        glVertex2f(pickerX, pickerY);
        glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        glVertex2f(pickerX, pickerY + pickerHeight);
        glVertex2f(pickerX + pickerWidth, pickerY + pickerHeight);
        glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        glVertex2f(pickerX + pickerWidth, pickerY);
        glEnd();
        glEnable(GL_ALPHA_TEST);
        glShadeModel(GL_FLAT);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        GL11.glPopMatrix();
    }

    public static void drawLeftGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        GL11.glPushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(right, top, 0).color((float) (endColor >> 24 & 255) / 255.0F, (float) (endColor >> 16 & 255) / 255.0F, (float) (endColor >> 8 & 255) / 255.0F, (float) (endColor >> 24 & 255) / 255.0F).endVertex();
        buffer.pos(left, top, 0).color((float) (startColor >> 16 & 255) / 255.0F, (float) (startColor >> 8 & 255) / 255.0F, (float) (startColor & 255) / 255.0F, (float) (startColor >> 24 & 255) / 255.0F).endVertex();
        buffer.pos(left, bottom, 0).color((float) (startColor >> 16 & 255) / 255.0F, (float) (startColor >> 8 & 255) / 255.0F, (float) (startColor & 255) / 255.0F, (float) (startColor >> 24 & 255) / 255.0F).endVertex();
        buffer.pos(right, bottom, 0).color((float) (endColor >> 24 & 255) / 255.0F, (float) (endColor >> 16 & 255) / 255.0F, (float) (endColor >> 8 & 255) / 255.0F, (float) (endColor >> 24 & 255) / 255.0F).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GL11.glPopMatrix();
    }

    static Vec3d glScissorOffset = new Vec3d(0d, 0d, 0d);

    public static Vec3d getGlScissorOffset() {
        return glScissorOffset;
    }

    public static void resetGlScissorOffset() {
        GuiUtil.glScissorOffset = new Vec3d(0, 0, 0);
    }

    public static void setGlScissorOffset(Vec3d glScissorOffset) {
        GuiUtil.glScissorOffset = glScissorOffset;
    }

    public static void glScissor(int x, int y, int width, int height) {
        ScaledResolution scr = new ScaledResolution(MC.mc);
        GL11.glScissor(x * 2, (scr.getScaledHeight() - y - height) * 2, width * 2, height * 2);
    }

    public static boolean drawButton(String text, int left, int top, int right, int bottom, Color color, int mouseX, int mouseY, boolean clicked) {
        boolean hover = false;
        if ((mouseX > left && mouseX < right && mouseY > top && mouseY < bottom) || clicked) {
            if (!clicked) hover = true;
            color = ColorUtil.fromHSB(ColorUtil.getHue(color), (float) Math.min(1, ColorUtil.getSaturation(color) + (clicked ? 0.4 : 0.2)), (float) Math.max(0, ColorUtil.getBrightness(color) - (clicked ? 0.2 : 0.0)));
        }
        Gui.drawRect(left + 1, top + 1, right - 1, bottom - 1, color.getRGB());
        Color outline = ColorUtil.fromHSB(ColorUtil.getHue(color), (float) Math.min(1, ColorUtil.getSaturation(color) + (hover ? 0.2 : 0.1)), ColorUtil.getBrightness(color));
        Gui.drawRect(left, top, left + 1, bottom, outline.getRGB());
        Gui.drawRect(right - 1, top, right, bottom, outline.getRGB());
        Gui.drawRect(left + 1, top, right - 1, top + 1, outline.getRGB());
        Gui.drawRect(left + 1, bottom - 1, right - 1, bottom, outline.getRGB());
        Spark.fontManager.drawString(text, left + (right - left)/2 - Spark.fontManager.getTextWidth(text)/2, top + (bottom - top) / 2 - Spark.fontManager.getTextHeight() / 2, -1);
        return hover;
    }
}
