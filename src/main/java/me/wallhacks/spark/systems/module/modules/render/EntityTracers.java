package me.wallhacks.spark.systems.module.modules.render;

import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.render.EspUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ColorSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;

import java.awt.*;

@Module.Registration(name = "EntityTracers", description = "Render esp for entities")
public class EntityTracers extends Module {

    BooleanSetting Players = new BooleanSetting("Players", this, false);
    BooleanSetting living = new BooleanSetting("Living",this, false);

    BooleanSetting RenderItems = new BooleanSetting("RenderItems", this, false);

    IntSetting lineWidth = new IntSetting("LineWidth",this, 1, 1, 10,"Render");

    ColorSetting playerColor = new ColorSetting("PlayerColor",this, new Color(177,41,18,186),"Render");
    ColorSetting livingColor = new ColorSetting("LivingColor",this, new Color(83,83,77,255),"Render");
    ColorSetting itemColor = new ColorSetting("ItemColor",this, new Color(40,40,40,255),"Render");


    IntSetting range = new IntSetting("Range",this, 260, 20, 260);


    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        MC.mc.gameSettings.viewBobbing = false;

        GL11.glPushMatrix();

        for(Object o : MC.mc.world.loadedEntityList.toArray()){

            Entity entity = (Entity)o;
            if(range.getValue() < range.getMax() && range.getValue() < MC.mc.player.getDistance(entity))
                continue;
            if(entity instanceof EntityLivingBase){
                EntityLivingBase e = (EntityLivingBase)entity;

                if((e instanceof EntityOtherPlayerMP)){
                    if(Players.isOn())
                        EspUtil.entityESPTracers(e, playerColor.getColor(),lineWidth.getValue());
                }
                else if(living.isOn()){
                    EspUtil.entityESPTracers(e, livingColor.getColor(),lineWidth.getValue());
                }

            }else if(entity instanceof EntityItem && RenderItems.isOn()){
                EspUtil.entityESPTracers(entity, itemColor.getColor(),lineWidth.getValue());
            }else if(entity instanceof EntityItemFrame && RenderItems.isOn()){
                EspUtil.entityESPTracers(entity, itemColor.getColor(),lineWidth.getValue());
            }

        }

        GL11.glPopMatrix();
    }



}
