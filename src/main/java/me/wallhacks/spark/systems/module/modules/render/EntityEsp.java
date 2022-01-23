package me.wallhacks.spark.systems.module.modules.render;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.render.RenderLivingEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.render.EspUtil;
import me.wallhacks.spark.util.render.ShaderEspUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;

import java.awt.*;

@Module.Registration(name = "EntityEsp", description = "Render shader esp for entities")
public class EntityEsp extends Module {
    BooleanSetting player = new BooleanSetting("Player",this, true,"Entities");

    BooleanSetting living = new BooleanSetting("Living",this, true,"Entities");
    BooleanSetting RenderItems = new BooleanSetting("RenderItems", this, false, "Entities");

    IntSetting lineWidth = new IntSetting("LineWidth",this, 2, 1, 10,"Render");
    ColorSetting playerColor = new ColorSetting("PlayerColor",this, new Color(177,41,18,186),"Render");
    ColorSetting livingColor = new ColorSetting("LivingColor",this, new Color(83,83,77,255),"Render");

    ColorSetting itemColor = new ColorSetting("Item", this, new Color(60,60,60,140), "Render");


    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        MC.mc.gameSettings.viewBobbing = false;

        GL11.glPushMatrix();

        for(Object o : MC.mc.world.loadedEntityList.toArray()){

            Entity entity = (Entity)o;
            if(entity instanceof EntityItem && RenderItems.isOn()){
                EspUtil.boundingESPBox(EspUtil.getRenderBB(entity),itemColor.getColor(), 2.0f);
            }else if(entity instanceof EntityItemFrame && RenderItems.isOn()){
                EspUtil.boundingESPBox(EspUtil.getRenderBB(entity), itemColor.getColor(), 2.0f);
            }

        }

        GL11.glPopMatrix();
    }


    @SubscribeEvent
    public void renderEntityEvent (RenderLivingEvent event) {

        Entity entity = event.getEntity();
        if (MC.mc.player == null || MC.mc.player == entity || MC.mc.world == null) {
            return;
        }

        if (entity instanceof EntityPlayer) {
            if(player.isOn())
                drawOutline(event, Spark.socialManager.isFriend((EntityPlayer)entity) ? ClientConfig.getInstance().friendColor.getColor() : playerColor.getColor());
        }
        else if (entity instanceof EntityLivingBase) {
            if(living.isOn())
                drawOutline(event,livingColor.getColor());
        }


    }

    void drawOutline(RenderLivingEvent event, Color color){
        final boolean fancy = MC.mc.gameSettings.fancyGraphics;
        final float gamma = MC.mc.gameSettings.gammaSetting;
        MC.mc.gameSettings.fancyGraphics = false;
        MC.mc.gameSettings.gammaSetting = 10000.0f;

        GL11.glPushMatrix();

        ShaderEspUtil.renderOne(lineWidth.getValue());

        event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
        GlStateManager.glLineWidth((float)lineWidth.getValue());

        ShaderEspUtil.renderTwo();

        event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
        GlStateManager.glLineWidth((float)lineWidth.getValue());
        ShaderEspUtil.renderThree();
        ShaderEspUtil.renderFour(color);
        event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
        GlStateManager.glLineWidth((float)lineWidth.getValue());
        ShaderEspUtil.renderFive();

        GL11.glPopMatrix();

        MC.mc.gameSettings.fancyGraphics = fancy;
        MC.mc.gameSettings.gammaSetting = gamma;
    }







}
