package me.wallhacks.spark.systems.module.modules.render;

import me.wallhacks.spark.event.render.RenderLivingEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.SettingGroup;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;

import java.awt.*;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;

@Module.Registration(name = "CrystalRender", description = "Render esp for entities")
public class CrystalRender extends Module {
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    ModeSetting mode = new ModeSetting("Mode", this, "Fill", Arrays.asList("Fill", "Wire", "WireFill"));
    BooleanSetting texture = new BooleanSetting("Texture", this, false);
    BooleanSetting lighting = new BooleanSetting("Lighting", this, false);
    ColorSetting hiddenColor = new ColorSetting("HiddenColor", this, new Color(0x442CD512, true));
    ColorSetting visibleColor = new ColorSetting("VisibleColor", this, new Color(0x5715D7D7, true));
    SettingGroup glintG = new SettingGroup("Glint", this);
    BooleanSetting glint = new BooleanSetting("Glint", glintG, false);
    BooleanSetting customColor = new BooleanSetting("CustomColor", glintG, false);
    ColorSetting glintColor = new ColorSetting("GlintColor", glintG, new Color(0x81FFFFFF, true));
    DoubleSetting glintScale = new DoubleSetting("GlintScale", glintG, 1.0f, 0.1f, 10.0f);
    DoubleSetting glintSpeed = new DoubleSetting("GlintSpeed", glintG, 5.0f, 0.1f, 20.0f);

    @SubscribeEvent
    public void renderCrystal(RenderLivingEvent event) {
        if (event.getEntity() instanceof EntityEnderCrystal) {
            GlStateManager.pushMatrix();
            GL11.glPushAttrib(1048575);
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.disableAlpha();
            if (lighting.getValue())
                GlStateManager.disableLighting();
            if (!texture.getValue())
                GlStateManager.disableTexture2D();
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL_FILL);
            GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            GL11.glDepthFunc(GL_GEQUAL);
            RenderUtil.setColor(hiddenColor.getColor());
            GL11.glLineWidth(2.0f);
            handleMode(event);
            event.setCanceled(true);
            GL11.glDepthFunc(GL_LEQUAL);
            RenderUtil.setColor(visibleColor.getColor());
            handleMode(event);
            GL11.glDepthFunc(GL_LESS);
            if (customColor.getValue() && glint.getValue() && !mode.is("Wire")) {
                GL11.glDepthFunc(GL_ALWAYS);
                RenderUtil.setColor(glintColor.getColor());
                renderShine(event);
                GL11.glDepthFunc(GL_LESS);
            }
            if (!texture.getValue())
                GlStateManager.enableTexture2D();
            if (lighting.getValue())
                GlStateManager.enableLighting();
            GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
        }
    }

    private void handleMode(RenderLivingEvent event) {
        switch (mode.getValue()) {
            case "Fill":
                doFill(event);
                break;
            case "WireFill":
                doFill(event);
            case "Wire":
                GL11.glEnable(GL_LINE_SMOOTH);
                GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                renderModel(event);
        }
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL_FILL);
    }

    private void doFill(RenderLivingEvent event) {
        if (!texture.getValue()) {
            if (glint.getValue() && !customColor.getValue())
                renderShine(event);
            else renderModel(event);
        } else {
            renderModel(event);
            if (glint.getValue() && !customColor.getValue())
                renderShine(event);
        }
    }

    private void renderModel(RenderLivingEvent event) {
        event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
    }

    private void renderShine(RenderLivingEvent event) {
        mc.getTextureManager().bindTexture(RES_ITEM_GLINT);
        if (!texture.getValue())
            GlStateManager.enableTexture2D();
        for (int i = 0; i < 2; ++i) {
            GlStateManager.matrixMode(GL_TEXTURE);
            GlStateManager.loadIdentity();
            float f8 = 0.33333334f * glintScale.getValue().floatValue();
            GlStateManager.scale(f8, f8, f8);
            GlStateManager.rotate(30.0f - (float) i * 60.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.translate(0.0f, ((float) event.getEntity().ticksExisted + mc.getRenderPartialTicks()) * (0.001f + (float) i * 0.003f) * glintSpeed.getValue().floatValue(), 0.0f);
            GlStateManager.matrixMode(GL_MODELVIEW);
            GL11.glTranslatef(0.0f, 0.0f, 0.0f);
            renderModel(event);
        }
        GlStateManager.matrixMode(GL_TEXTURE);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(GL_MODELVIEW);
        mc.getTextureManager().bindTexture(RenderEnderCrystal.ENDER_CRYSTAL_TEXTURES);
        if (!texture.getValue())
            GlStateManager.disableTexture2D();
    }
}
