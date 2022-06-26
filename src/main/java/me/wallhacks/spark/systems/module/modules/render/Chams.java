package me.wallhacks.spark.systems.module.modules.render;

import com.mojang.authlib.GameProfile;
import me.wallhacks.spark.event.entity.DeathEvent;
import me.wallhacks.spark.event.render.RenderLivingEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.SettingGroup;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.objects.Timer;
import me.wallhacks.spark.util.render.RenderUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.opengl.GL11.*;


@Module.Registration(name = "Chams", description = "Highlights people behind walls")
public class Chams extends Module {
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static final ResourceLocation CUSTOM = new ResourceLocation("textures/glint.png");
    ModeSetting mode = new ModeSetting("Mode", this, "Fill", Arrays.asList("Fill", "Wire", "WireFill"));
    BooleanSetting players = new BooleanSetting("Players", this, true);
    BooleanSetting mobs = new BooleanSetting("Mobs", this, true);
    BooleanSetting animals = new BooleanSetting("Animals", this, true);
    BooleanSetting texture = new BooleanSetting("Texture", this, false);
    BooleanSetting visible = new BooleanSetting("Visible", this, false);
    BooleanSetting lighting = new BooleanSetting("Lighting", this, false);
    ColorSetting hiddenColor = new ColorSetting("HiddenColor", this, new Color(0x442CD512, true));
    ColorSetting visibleColor = new ColorSetting("VisibleColor", this, new Color(0x5715D7D7, true));
    SettingGroup popG = new SettingGroup("PopChams", this);
    BooleanSetting popChams = new BooleanSetting("PopChams", popG, false);
    BooleanSetting deathChams = new BooleanSetting("DeathChams", popG, false);
    ColorSetting popColor = new ColorSetting("PopColor", popG, new Color(0x76FFFFFF, true));
    BooleanSetting glintP = new BooleanSetting("Glint", popG, false);
    DoubleSetting popTime = new DoubleSetting("StayTime", popG, 1.5D, 0.5D, 5D);
    SettingGroup glintG = new SettingGroup("Glint", this);
    ModeSetting glint = new ModeSetting("Glint", glintG, "Off", Arrays.asList("Off", "Normal", "Custom"));
    DoubleSetting glintScale = new DoubleSetting("GlintScale", glintG, 1.0f, 0.1f, 10.0f);
    DoubleSetting glintSpeed = new DoubleSetting("GlintSpeed", glintG, 5.0f, 0.1f, 20.0f);

    CopyOnWriteArrayList<PopCham> pops = new CopyOnWriteArrayList<>();



    @SubscribeEvent
    public void onRenderLivingBase(RenderLivingEvent event) {

        if(!RenderUtil.isRenderLoop && !(event.getEntity() instanceof PopCham))
            return;
        if ((event.getEntity() instanceof PopCham) || (event.getEntity() instanceof EntityPlayer && players.getValue()) || (event.getEntity() instanceof EntityMob && mobs.getValue()) || (event.getEntity() instanceof EntityAnimal && animals.getValue()) && event.getEntity().world == mc.world) {
            GlStateManager.pushMatrix();
            GL11.glPushAttrib(1048575);
            ((EntityLivingBase) event.getEntity()).hurtTime = 0;
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.disableAlpha();
            boolean bipedLeftArmwear = false;
            boolean bipedRightArmwear = false;
            boolean bipedLeftLegwear = false;
            boolean bipedRightLegwear = false;
            boolean bipedBodyWear = false;
            boolean bipedHeadwear = false;
            if (lighting.getValue())
                GlStateManager.disableLighting();
            if (!texture.getValue() || event.getEntity() instanceof PopCham) {
                if (event.getModelBase() instanceof ModelPlayer) {
                    ModelPlayer modelPlayer = (ModelPlayer) event.getModelBase();
                    bipedLeftArmwear = modelPlayer.bipedLeftArmwear.showModel;
                    bipedRightArmwear = modelPlayer.bipedRightArmwear.showModel;
                    bipedLeftLegwear = modelPlayer.bipedLeftLegwear.showModel;
                    bipedRightLegwear = modelPlayer.bipedRightLegwear.showModel;
                    bipedBodyWear = modelPlayer.bipedBodyWear.showModel;
                    bipedHeadwear = modelPlayer.bipedHeadwear.showModel;
                    modelPlayer.bipedLeftArmwear.showModel = false;
                    modelPlayer.bipedRightArmwear.showModel = false;
                    modelPlayer.bipedLeftLegwear.showModel = false;
                    modelPlayer.bipedRightLegwear.showModel = false;
                    modelPlayer.bipedBodyWear.showModel = false;
                    modelPlayer.bipedHeadwear.showModel = false;
                }
                GlStateManager.disableTexture2D();
            }
            Color color = popColor.getColor();
            if (event.getEntity() instanceof PopCham) {
                PopCham popCham = (PopCham) event.getEntity();
                double factor = 1 - (popCham.timer.getPassedTimeMs() / (popTime.getValue() * 1000L));
                if (factor < 0) {
                    popCham.flag = true;
                    factor = 0;
                }
                color = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * factor));
            }
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL_FILL);
            GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
            GL11.glDepthFunc(GL_GEQUAL);
            if (event.getEntity() instanceof PopCham)
                RenderUtil.setColor(color);
            else
                RenderUtil.setColor(hiddenColor.getColor());
            GL11.glLineWidth(2.0f);
            handleMode(event);
            if (visible.getValue() || event.getEntity() instanceof PopCham) {
                event.setCanceled(true);
                GL11.glDepthFunc(GL_LEQUAL);
                if (event.getEntity() instanceof PopCham)
                    RenderUtil.setColor(color);
                else
                    RenderUtil.setColor(visibleColor.getColor());
                handleMode(event);
            }
            if (!glint.is("Off") && (!(event.getEntity() instanceof PopCham) || glintP.getValue()) && !mode.is("Wire")) {
                if (event.getEntity() instanceof PopCham)
                    RenderUtil.setColor(color);
                else
                    RenderUtil.setColor(hiddenColor.getColor());
                GL11.glDepthFunc(GL_GEQUAL);
                renderShine(event);
                if (visible.getValue() || event.getEntity() instanceof PopCham) {
                    GL11.glDepthFunc(GL_LEQUAL);
                    if (event.getEntity() instanceof PopCham)
                        RenderUtil.setColor(color);
                    else
                        RenderUtil.setColor(visibleColor.getColor());
                    renderShine(event);
                }
            }
            GL11.glDepthFunc(GL_LESS);
            if (!texture.getValue() || event.getEntity() instanceof PopCham) {
                GlStateManager.enableTexture2D();
                if (event.getModelBase() instanceof ModelPlayer) {
                    ModelPlayer modelPlayer = (ModelPlayer) event.getModelBase();
                    modelPlayer.bipedLeftArmwear.showModel = bipedLeftArmwear;
                    modelPlayer.bipedRightArmwear.showModel = bipedRightArmwear;
                    modelPlayer.bipedLeftLegwear.showModel = bipedLeftLegwear;
                    modelPlayer.bipedRightLegwear.showModel = bipedRightLegwear;
                    modelPlayer.bipedBodyWear.showModel = bipedBodyWear;
                    modelPlayer.bipedHeadwear.showModel = bipedHeadwear;
                }
            }
            if (lighting.getValue())
                GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();

        }
    }

    private void handleMode(RenderLivingEvent event) {
        switch (mode.getValue()) {
            case "Fill":
                if (glint.is("Off") || (event.getEntity() instanceof PopCham && !glintP.getValue()) || texture.getValue())
                    renderModel(event);
                break;
            case "WireFill":
                if (glint.is("Off") || (event.getEntity() instanceof PopCham && !glintP.getValue()) || texture.getValue())
                    renderModel(event);
            case "Wire":
                GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                renderModel(event);
        }
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL_FILL);
    }

    @SubscribeEvent
    public void onRender3d(RenderWorldLastEvent event) {
        boolean flag = false;
        pops.removeIf(popCham -> popCham.flag);
        for (PopCham popCham : pops) {
            if (popCham != null)
                try {
                    mc.renderManager.renderEntityStatic(popCham, popCham.partial, true);
                } catch (Exception e) {
                    flag = true;
                }
        }
        if (flag) pops.clear();
    }

    @SubscribeEvent
    public void deathEvent(DeathEvent event) {
        if (event.getEntity() != mc.player && ((event.getType() == DeathEvent.Type.TOTEMPOP && popChams.getValue()) || (event.getType() == DeathEvent.Type.DEATH && deathChams.getValue())))
            pops.add(new PopCham(event.getEntity()));
    }

    private void renderModel(RenderLivingEvent event) {
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
        event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
    }

    private void renderShine(RenderLivingEvent event) {
  
        mc.getTextureManager().bindTexture(glint.is("Custom") ? CUSTOM : RES_ITEM_GLINT);
        if (!texture.getValue() || event.getEntity() instanceof PopCham)
            GlStateManager.enableTexture2D();
        for (int i = 0; i < 2; ++i) {
            GlStateManager.matrixMode(GL_TEXTURE);
            GlStateManager.loadIdentity();
            float f8 = 0.33333334f * glintScale.getValue().floatValue();
            GlStateManager.scale(f8, f8, f8);
            GlStateManager.rotate(30.0f - (float) i * 60.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.translate(0.0f, ((float) mc.player.ticksExisted + mc.getRenderPartialTicks()) * (0.001f + (float) i * 0.003f) * glintSpeed.getValue().floatValue(), 0.0f);
            GlStateManager.matrixMode(GL_MODELVIEW);
            GL11.glTranslatef(0.0f, 0.0f, 0.0f);
            renderModel(event);
        }
        GlStateManager.matrixMode(GL_TEXTURE);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(GL_MODELVIEW);
        if (!texture.getValue() || event.getEntity() instanceof PopCham)
            GlStateManager.disableTexture2D();
    }

    public class PopCham extends EntityOtherPlayerMP {
        Timer timer;
        float partial;
        boolean flag = false;
        public PopCham(EntityPlayer player) {
            //ding dong
            super(mc.world, new GameProfile(UUID.fromString("fb43302e-b957-46af-822d-7742d624dd24"), "dummy"));
            posY = player.posY;
            posX = player.posX;
            posZ = player.posZ;
            prevPosX = player.prevPosX;
            prevPosY = player.prevPosY;
            prevPosZ = player.prevPosZ;
            limbSwing = player.limbSwing;
            ticksExisted = player.ticksExisted;
            limbSwingAmount = player.limbSwingAmount;
            prevLimbSwingAmount = player.prevLimbSwingAmount;
            lastTickPosX = player.lastTickPosX;
            lastTickPosY = player.lastTickPosY;
            lastTickPosZ = player.lastTickPosZ;
            rotationYaw = player.rotationYaw;
            rotationYawHead = player.rotationYawHead;
            rotationPitch = player.rotationPitch;
            prevRotationPitch = player.prevRotationPitch;
            prevRotationYaw = player.prevRotationYaw;
            prevRotationYawHead = player.prevRotationYawHead;
            pops.add(this);
            timer = new Timer().reset();
            partial = mc.getRenderPartialTicks();
        }
    }
}
