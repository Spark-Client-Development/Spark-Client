package me.wallhacks.spark.manager;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.world.WorldLoadEvent;
import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.systems.setting.settings.*;
import me.wallhacks.spark.util.FileUtil;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.StringUtil;
import me.wallhacks.spark.util.WorldUtils;
import me.wallhacks.spark.util.objects.Vec2d;
import me.wallhacks.spark.util.objects.Vec2i;
import me.wallhacks.spark.util.render.ColorUtil;
import me.wallhacks.spark.util.render.MapRender;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class WaypointManager implements MC {

    public static String fileEx = "sparkWayPoint";

    ArrayList<Waypoint> wayPoints = new ArrayList<>();


    public WaypointManager() {
        Spark.eventBus.register(this);
    }


    String server;

    @SubscribeEvent
    void OnWorld(WorldLoadEvent event) {

        Save();
        server = mc.world == null ? null : (StringUtil.getServerName(mc.getCurrentServerData()));
        Load();
    }

    public void Save() {
        if(server == null)
            return;

        String dir = getWaypointsPath();
        if(FileUtil.exists(dir))
            FileUtil.deleteDirectory(dir);

        for (Waypoint waypoint : wayPoints) {

            Spark.configManager.saveSettingHolder(waypoint,getWaypointPath(waypoint),false);
        }

    }
    public void Load() {
        wayPoints.clear();
        if(server == null)
            return;
        String dir = getWaypointsPath();



        if(FileUtil.exists(dir))
        for (String file : FileUtil.listFilesForFolder(dir,"."+fileEx)) {

            Waypoint waypoint = new Waypoint(file.substring(0,file.length()-1-fileEx.length()));
            Spark.configManager.loadSettingHolder(waypoint,dir+System.getProperty("file.separator")+file);

            wayPoints.add(waypoint);
        }
    }

    public Waypoint getWayPoint(String name){
        for (Waypoint waypoint : wayPoints) {

            if(waypoint.getName().equalsIgnoreCase(name))
                return waypoint;
        }
        return null;
    }


    public ArrayList<Waypoint> getWayPoints() {
        return wayPoints;
    }


    public Waypoint createWayPoint(Vec2i pos,int dim) {
        int in = 0;

        while(in < 100)
        {
            in++;
            String name = "WayPoint"+in;
            Waypoint waypoint = createWayPoint(pos,dim,name);
            if(waypoint == null)
                continue;
            return waypoint;
        }
        return null;
    }
    public Waypoint createWayPoint(Vec2i pos,int dim,String name) {
        for (Waypoint point : getWayPoints()) {
            if(point.getName().equalsIgnoreCase(name))
                return null;
        }
        Waypoint w = new Waypoint(name);
        wayPoints.add(w);
        w.setDim(dim);
        w.setPos(pos);
        return w;
    }


    String getWaypointPath(Waypoint wayPoint) {
        return getWaypointsPath()+System.getProperty("file.separator") + wayPoint.getName()+"."+fileEx;
    }
    String getWaypointsPath() {
        return Spark.ParentPath.getAbsolutePath() + System.getProperty("file.separator")+"waypoints"+System.getProperty("file.separator")+ server;
    }





    public static class Waypoint extends SettingsHolder {
        public StringSetting name = new StringSetting("Name",this,"Name");

        ColorSetting color = new ColorSetting("Color",this,new Color(1,1,1),false);

        ModeSetting dim = new ModeSetting("Dim",this,"Overworld", Arrays.asList("Nether","Overworld","End"));

        BooleanSetting hasY = new BooleanSetting("HasY",this,false);



        VectorSetting pos = new VectorSetting("Pos",this,new Vec3i(0,0,0),integer -> hasY.isOn(),null);



        public boolean hasY() {
            return hasY.isOn();
        }
        public Vec2i getLocation2i()
        {
            Vec2d v = MapRender.ConvertPos(new Vec2d(pos.getValue().getX(),pos.getValue().getZ()),dim.getValueIndex()-1,mc.player.dimension);
            return new Vec2i((int)v.x,(int)v.y);
        }
        public Vec2d getLocation2d()
        {
            Vec2d v = MapRender.ConvertPos(new Vec2d(pos.getValue().getX(),pos.getValue().getZ()),dim.getValueIndex()-1,mc.player.dimension);
            return new Vec2d((int)v.x,(int)v.y);
        }

        public Vec2d getLocation2d(int fromDim, int toDim)
        {
            Vec2d v = MapRender.ConvertPos(new Vec2d(pos.getValue().getX(),pos.getValue().getZ()),fromDim,toDim);
            return new Vec2d((int)v.x,(int)v.y);
        }

        public Vec3i getLocation()
        {
            Vec2d v = MapRender.ConvertPos(new Vec2d(pos.getValue().getX(),pos.getValue().getZ()),dim.getValueIndex()-1,mc.player.dimension);
            return new Vec3i((int)v.x,(int)pos.getValue().getY(),(int)v.y);
        }


        public Waypoint(String inName) {
            name.setValue(inName);
            color.setColor(ColorUtil.generateColor(inName));
        }




        @Override
        public String getName() {
            return name.getValue();
        }


        public void setPos(Vec2i v) {

            pos.setValue(new Vec3i(v.x,0,v.y));

        }

        public Color getColor() {
            return color.getColor();
        }

        public void setDim(int d) {
            dim.setValueWithIndex(d+1);
        }

        public String getDimName() {
            return dim.getValueName();
        }

        public int getDim() {
            return dim.getValueIndex()-1;
        }
    }
}

