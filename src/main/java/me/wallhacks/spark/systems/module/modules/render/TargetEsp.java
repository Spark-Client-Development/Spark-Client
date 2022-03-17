package me.wallhacks.spark.systems.module.modules.render;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.module.modules.combat.CrystalAura;
import me.wallhacks.spark.systems.module.modules.combat.KillAura;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.util.objects.Timer;
import me.wallhacks.spark.util.render.ColorUtil;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@Module.Registration(name = "TargetESP", description = "Highlights your current target")
public class TargetEsp extends Module {
    ColorSetting color1 = new ColorSetting("Color1", this, new Color(0x577A41B2, true));
    ColorSetting color2 = new ColorSetting("Color2", this, new Color(0x0000000, true));
    DoubleSetting offset = new DoubleSetting("Offset", this, 0.2D, 0.1D, 1D);
    Entity target;
    Timer delta = new Timer();
    float alphaMultiplier;
    boolean fade = false;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onUpdateEvent(PlayerUpdateEvent event) {
        if (isValid(CrystalAura.instance.getTarget())) {
            Spark.sendInfo("TEST");
            target = CrystalAura.instance.getTarget();
            alphaMultiplier = 0;
            fade = true;
        } else if (isValid(KillAura.instance.getTarget()) && CrystalAura.instance.getTarget() == null) {
            target = KillAura.instance.getTarget();
            alphaMultiplier = 0;
            fade = true;
        } else if (CrystalAura.instance.getTarget() == null && KillAura.instance.getTarget() == null) {
            fade = false;
        }
    }

    @Override
    public void onEnable() {
        fade = true;
        alphaMultiplier = 0;
    }

    boolean isValid(Entity entity) {
        return mc.world.loadedEntityList.contains(entity) && target != entity;
    }

    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent event) {
        if (target != null) {
            if (fade) {
                if (alphaMultiplier != 1)
                    alphaMultiplier = Math.min(1, alphaMultiplier + delta.getPassedTimeMs()/500f);
            } else {
                alphaMultiplier = Math.max(0, alphaMultiplier - delta.getPassedTimeMs()/500f);
                if (alphaMultiplier == 0) {
                    target = null;
                    delta.reset();
                    return;
                }
            }
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glDepthFunc(GL11.GL_ALWAYS);
            GL11.glDisable(GL11.GL_CULL_FACE);
            float state = ((System.currentTimeMillis() % 1000) / 500f);
            float state2 = (((System.currentTimeMillis() - (500*offset.getFloatValue())) % 1000) / 500f);
            if (state > 1) {
                state -= 1;
                state = 1 - state;
            }
            if (state2 > 1) {
                state2 -= 1;
                state2 = 1 - state2;
            }
            float y1 = (float) (target.posY - mc.renderManager.viewerPosY + (target.height * state));
            float y2 = (float) (target.posY - mc.renderManager.viewerPosY + (target.height * state2));
            GL11.glBegin(GL11.GL_QUAD_STRIP);
            for (int i = 0; i <= 360; i++) {
                float x = (float) ((Math.cos(i * Math.PI / 180F) * target.width) + target.posX - mc.renderManager.viewerPosX);
                float z = (float) ((Math.sin(i * Math.PI / 180F) * target.width) + target.posZ - mc.renderManager.viewerPosZ);
                GL11.glColor4f(color1.getColor().getRed()/255f, color1.getColor().getGreen()/255f, color1.getColor().getBlue()/255f, (color1.getColor().getAlpha()/255f)*alphaMultiplier);
                GL11.glVertex3f(x, y1, z);
                GL11.glColor4f(color2.getColor().getRed()/255f, color2.getColor().getGreen()/255f, color2.getColor().getBlue()/255f, (color2.getColor().getAlpha()/255f)*alphaMultiplier);
                GL11.glVertex3f(x, y2, z);
            }
            GL11.glEnd();
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDepthFunc(GL11.GL_LESS);
            GL11.glDisable(GL11.GL_BLEND);
        }
        delta.reset();
    }

}
