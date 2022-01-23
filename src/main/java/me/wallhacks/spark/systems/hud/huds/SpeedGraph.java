package me.wallhacks.spark.systems.hud.huds;


import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.MathUtil;
import me.wallhacks.spark.util.StringUtil;
import me.wallhacks.spark.util.render.ColorUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.ModeSetting;

import java.util.ArrayList;
import java.util.Arrays;

@HudElement.Registration(name = "SpeedGraph", description = "Graph that shows your speed", posX = 0.5, posY = 0.8, width = 140, height = 40)
public class SpeedGraph extends HudElement {
    BooleanSetting average = new BooleanSetting("Average", this, false);
    BooleanSetting ShowSpeed = new BooleanSetting("SpeedText",this,true);
    ModeSetting SpeedDisplay = new ModeSetting("Type",this,"mps", Arrays.asList("kmph","mps","mpt","mpmin"), v -> ShowSpeed.isOn());


    ArrayList<Double> speeds = new ArrayList<>();

    int indexes = 100;

    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {
        Vec3d v = new Vec3d(MC.mc.player.prevPosX- MC.mc.player.posX, MC.mc.player.prevPosY- MC.mc.player.posY, MC.mc.player.prevPosZ- MC.mc.player.posZ);

        double speed = ((MathHelper.sqrt(v.x * v.x + v.y * v.y + v.z * v.z)));



        speeds.add(speed);

        if(speeds.size() > indexes)
            speeds.remove(0);
    }

    double yMultiplySmooth = 0;

    @Override
    public void draw(float deltaTime) {
        if(speeds.size() <= 0)
            return;


        if(ShowSpeed.isOn())
        {


            double total = 0;
            int size = Math.min(speeds.size()-1,20);
            for (int i = speeds.size()-1; i >= speeds.size()-size; i--)
            {
                total+=speeds.get(i);
            }

            double currentSpeed = total/size;

            String speedString = "m/tick";
            if(SpeedDisplay.isValueName("mps")){
                speedString = "m/s";
                currentSpeed = currentSpeed * 20f;
            }
            else if(SpeedDisplay.isValueName("kmph"))
            {
                speedString = "km/h";
                currentSpeed = ((currentSpeed * 20) / 1000)*60*60;
            }
            else if(SpeedDisplay.isValueName("mpmin"))
            {
                speedString = "m/min";
                currentSpeed = ((currentSpeed * 20))*60;
            }
            speedString = StringUtil.fmt(currentSpeed,1)+" "+speedString;

            int x = getRenderPosX()+getWidth()/2-fontManager.getTextWidth(speedString)/2;
            fontManager.drawString(speedString,x,getEndRenderPosY()+fontManager.getTextHeight()-2,hudSettings.getGuiHudSecondColor().getRGB());
        }


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

        ColorUtil.glColor(hudSettings.getGuiHudMainColor());

        double total = 0;
        double biggest = 0.1;
        for (double f : speeds) {
            if(f > biggest)
                biggest = f;
            total+=f;
        }
        double average = total/speeds.size();




        int height = getHeight() - 10;
        int width = getWidth() - 10;


        double yMultiply = height / biggest;
        yMultiplySmooth = MathUtil.moveTwards(yMultiplySmooth,yMultiply,deltaTime*(yMultiply < yMultiplySmooth ? 2 : 0.5));

        double xf = (double)width / (double)indexes;

        GL11.glBegin(GL11.GL_LINES);

        int i = speeds.size()-1;
        while (i >= 1)
        {
            int li = i-1;

            double ly = getEndRenderPosY()-5-Math.min(height,speeds.get(li)*yMultiplySmooth);
            double y = getEndRenderPosY()-5-Math.min(height,speeds.get(i)*yMultiplySmooth);

            GL11.glVertex2d(getRenderPosX()+5+li*xf, ly);
            GL11.glVertex2d(getRenderPosX()+5+i*xf, y);

            i--;
        }

        GL11.glEnd();

        if (this.average.getValue()) {
            ColorUtil.glColor(hudSettings.getGuiHudSecondColor());
            GL11.glBegin(GL11.GL_LINES);

            GL11.glVertex2d(getRenderPosX() + 5, getEndRenderPosY() - 5 - average * yMultiplySmooth);
            GL11.glVertex2d(getRenderPosX() + 5 + width, getEndRenderPosY() - 5 - average * yMultiplySmooth);

            GL11.glEnd();
        }


        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glPopAttrib();
        GL11.glPopMatrix();

    }



}
