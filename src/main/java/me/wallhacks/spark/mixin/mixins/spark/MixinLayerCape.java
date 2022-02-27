package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.util.render.ColorUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(LayerCape.class)
public class MixinLayerCape {

    @Inject(method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/layers/LayerCape;playerRenderer:Lnet/minecraft/client/renderer/entity/RenderPlayer;", ordinal = 1))
    public void doRenderLayer(AbstractClientPlayer d1, float d2, float f, float d3, float d4, float f1, float f2, float f3, CallbackInfo ci) {
        if (Spark.capeManager.isFancy(d1.entityUniqueID.toString().replaceAll("-", ""))) {
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            GL11.glBegin(GL11.GL_QUADS);
            Color up = ColorUtil.fromHSB((System.currentTimeMillis() % 6000) / 6000F, 1f, 1f);
            Color down = ColorUtil.fromHSB(((System.currentTimeMillis() - 1200) % 6000) / 6000F, 1f, 1f);
            GL11.glColor3f(up.getRed() / 255f, up.getGreen() / 255f, up.getBlue() / 255f);
            GL11.glVertex3d(-0.3, 0, -0.05);
            GL11.glVertex3d(0.3, 0, -0.05);
            GL11.glColor3f(down.getRed() / 255f, down.getGreen() / 255f, down.getBlue() / 255f);
            GL11.glVertex3d(0.3, 0.9, -0.05);
            GL11.glVertex3d(-0.3, 0.9, -0.05);
            GL11.glEnd();
            GL11.glColor3f(1f, 1f, 1f);
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }
    }
}
