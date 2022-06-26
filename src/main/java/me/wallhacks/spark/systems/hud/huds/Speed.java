package me.wallhacks.spark.systems.hud.huds;


import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.systems.hud.InfoHudElement;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;
import me.wallhacks.spark.util.MathUtil;
import me.wallhacks.spark.util.StringUtil;
import me.wallhacks.spark.util.render.ColorUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;

@HudElement.Registration(name = "Speed", description = "Graph that shows your speed", posX = 0.5, posY = 0.8, width = 140, height = 40)
public class Speed extends InfoHudElement {

    BooleanSetting UseYMovement = new BooleanSetting("UseYMovement",this,true);

    ModeSetting Calculate = new ModeSetting("Show",this,"average", Arrays.asList("current","average"));

    ModeSetting SpeedDisplay = new ModeSetting("Type",this,"mps", Arrays.asList("kmph","mps","mpt","mpmin"));


    ArrayList<Double> speeds = new ArrayList<>();

    int indexes = 100;

    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        Entity e = mc.player.ridingEntity == null ? mc.player : mc.player.ridingEntity;
        Vec3d v = new Vec3d(e.prevPosX- e.posX, e.prevPosY- e.posY, e.prevPosZ- e.posZ);

        double speed = UseYMovement.isOn() ? ((MathHelper.sqrt(v.y * v.y + v.x * v.x + v.z * v.z))) : ((MathHelper.sqrt(v.x * v.x + v.z * v.z)));
        speed*=(50/mc.timer.tickLength);


        speeds.add(speed);

        if(speeds.size() > indexes)
            speeds.remove(0);
    }

    @Override
    public void draw(float deltaTime) {
        if(speeds.size() <= 0)
            return;

        double total = 0;
        int size = Math.min(speeds.size()-1,20);
        for (int i = speeds.size()-1; i >= speeds.size()-size; i--)
        {
            total+=speeds.get(i);
        }

        double average = total/size;
        double current = speeds.get(speeds.size()-1);

        String speedText = StringUtil.SpeedConvertFromShort(Calculate.is("current") ? current : average,SpeedDisplay.getValue())+" "+StringUtil.SpeedUnitShortToLong(SpeedDisplay.getValue());


        setInfo(String.format(ChatFormatting.GRAY + "Speed %s%s", ChatFormatting.WHITE, speedText));
        drawInfo();
    }



}
