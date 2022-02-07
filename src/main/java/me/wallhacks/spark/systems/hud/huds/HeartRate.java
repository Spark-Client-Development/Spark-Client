package me.wallhacks.spark.systems.hud.huds;


import me.wallhacks.spark.event.client.RunTickEvent;
import me.wallhacks.spark.event.player.PlayerLivingTickEvent;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.util.MathUtil;
import me.wallhacks.spark.util.StringUtil;
import me.wallhacks.spark.util.render.ColorUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

@HudElement.Registration(name = "HearthRate", description = "Graph that shows your heart rate", posX = 0.5, posY = 0.8, width = 140, height = 40)
public class HeartRate extends HudElement {


    ArrayList<Double> rates = new ArrayList<>();

    int indexes = 100;

    double t = 0;
    @SubscribeEvent
    public void onUpdate(RunTickEvent event) {




        double rate = mc.player == null || !mc.player.isEntityAlive() ? 0 : Math.pow(Math.sin(t),63) * Math.sin(t+1.5)*8;
        t+=0.15;
        rates.add(rate);


        if(rates.size() > indexes)
            rates.remove(0);
    }

    double yMultiplySmooth = 0;

    @Override
    public void draw(float deltaTime) {
        super.draw(deltaTime);
        if(rates.size() <= 0)
            return;


        //draw graph
        drawGraph(deltaTime);


    }


    void drawGraph(float deltaTime) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(1048575);


        GL11.glBlendFunc(770, 771);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(1.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);

        ColorUtil.glColor(new Color(227, 10, 10, 179));


        double smallest = 0;
        double biggest = 0.1;
        for (double f : rates) {
            if(f > biggest)
                biggest = f;

            if(f < smallest)
                smallest = f;
        }



        int height = getHeight() - 10;
        int width = getWidth() - 10;


        double yMultiply = height / (biggest-smallest);
        yMultiplySmooth = MathUtil.moveTwards(yMultiplySmooth,yMultiply,deltaTime*(yMultiply < yMultiplySmooth ? 2 : 0.5));

        double xf = (double)width / (double)indexes;

        GL11.glBegin(GL11.GL_LINES);

        int i = rates.size()-1;
        while (i >= 1)
        {
            int li = i-1;

            double ly = getEndRenderPosY()-10-Math.min(height, rates.get(li)*yMultiplySmooth);
            double y = getEndRenderPosY()-10-Math.min(height, rates.get(i)*yMultiplySmooth);

            GL11.glVertex2d(getRenderPosX()+10+li*xf, ly);
            GL11.glVertex2d(getRenderPosX()+10+i*xf, y);

            i--;
        }

        GL11.glEnd();



        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glPopAttrib();
        GL11.glPopMatrix();

    }



}
