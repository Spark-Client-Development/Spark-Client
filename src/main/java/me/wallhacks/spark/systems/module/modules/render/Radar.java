package me.wallhacks.spark.systems.module.modules.render;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.clientsetting.ClientSetting;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.SettingGroup;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.player.PlayerUtil;
import me.wallhacks.spark.util.render.ColorUtil;
import me.wallhacks.spark.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.DoubleSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;

import java.awt.*;

@Module.Registration(name = "Radar", description = "Render arrows to show close players")
public class Radar extends Module {

    private final IntSetting range = new IntSetting("Range", this,260, 25, 260);

    private final IntSetting tilt = new IntSetting("Tilt", this,45, 20, 90);

    private final IntSetting radius = new IntSetting("Radius", this,45, 10, 200);
    private final IntSetting size = new IntSetting("Size",this, 10, 5, 25);


    SettingGroup color = new SettingGroup("Color", this);
    private final ColorSetting closeColor = new ColorSetting("CloseColor",color,new Color(222, 6, 6,250));
    private final ColorSetting distantColor = new ColorSetting("DistantColor",color,new Color(44, 141, 17,250));
    private final BooleanSetting outline = new BooleanSetting("Outline",color, false);
    private final DoubleSetting outlineWidth = new DoubleSetting("OutlineWidth",color, 1.0, 0.5, 3.0,0.5,v -> outline.isOn());

    private final ColorSetting outlineColor = new ColorSetting("OutlineColor",color,new Color(149,70,70,250),v -> outline.isOn());




    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent event) {

        if(mc.renderViewEntity.rotationPitch < 0 && tilt.getValue() >= 90)
            return;

        float tilt = Math.max(0, Math.min(90 - mc.renderViewEntity.rotationPitch, this.tilt.getValue()));




        GL11.glPushMatrix();
        GL11.glPushAttrib(1048575);

        for(Object o : mc.world.loadedEntityList.toArray()){


            if(o instanceof EntityPlayer){
                EntityPlayer entity = (EntityPlayer) o;

                if(mc.player == entity)
                    continue;
                if(range.getValue() < range.getMax() && range.getValue() < mc.player.getDistance(entity))
                    continue;

                int x = Display.getWidth() / 2 / (mc.gameSettings.guiScale == 0 ? 1 : mc.gameSettings.guiScale);
                int y = Display.getHeight() / 2 / (mc.gameSettings.guiScale == 0 ? 1 : mc.gameSettings.guiScale);
                float yaw = this.getRotations(entity) - (mc.renderViewEntity.rotationYaw);

                GL11.glTranslatef((float) x, (float) y, 0.0f);
                GL11.glRotatef(tilt, 1.0f, 0.0f, 0.0f);
                GL11.glRotatef(yaw, 0.0f, 0.0f, 1.0f);
                GL11.glTranslatef((float) -x, (float) -y, 0.0f);

                //stop z fighting
                GL11.glTranslatef(0, 0, 0.02f);

                RenderUtil.drawTracerPointer(x, y - this.radius.getValue(), this.size.getValue().floatValue(), 2, 1.5f, getColor(entity), outline.getValue(), outlineWidth.getValue().floatValue(), outlineColor.getColor());


                GL11.glTranslatef((float) x, (float) y, 0.0f);
                GL11.glRotatef(-yaw, 0.0f, 0.0f, 1.0f);
                GL11.glRotatef(-tilt, 1.0f, 0.0f, 0.0f);
                GL11.glTranslatef((float) (-x), (float) (-y), 0.0f);

            }

        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        GL11.glPopAttrib();
        GL11.glPopMatrix();

    }

    private Color getColor(EntityPlayer entity){
        //lerp by distance
        if(Spark.socialManager.isFriend(entity))
            return ClientConfig.getInstance().friendColor.getColor();
        float l = MathHelper.clamp((PlayerUtil.getDistance(entity.getPositionVector())-5)/40f,0,1);
        return ColorUtil.lerpColor(closeColor.getColor(), distantColor.getColor(), l);
    }


    private float getRotations(EntityLivingBase ent) {
        float partialTicks = mc.getRenderPartialTicks();
        double x = (ent.posX+(ent.posX-ent.lastTickPosX)*partialTicks) - (mc.renderViewEntity.posX+(mc.renderViewEntity.posX- mc.renderViewEntity.lastTickPosX)*partialTicks);
        double z = (ent.posZ+(ent.posZ-ent.lastTickPosZ)*partialTicks) - (mc.renderViewEntity.posZ+(mc.renderViewEntity.posZ- mc.renderViewEntity.lastTickPosZ)*partialTicks);
        return (float) (-(Math.atan2(x, z) * 57.29577951308232));
    }
}
