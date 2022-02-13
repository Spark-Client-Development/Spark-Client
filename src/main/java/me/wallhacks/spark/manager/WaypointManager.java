package me.wallhacks.spark.manager;

import baritone.api.event.events.WorldEvent;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.world.WorldLoadEvent;
import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.systems.setting.settings.*;
import me.wallhacks.spark.util.FileUtil;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.StringUtil;
import me.wallhacks.spark.util.WorldUtils;
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
            FileUtil.deleteFile(dir);

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
        for (String file : FileUtil.listFilesForFolder(dir,fileEx)) {
            Waypoint waypoint = new Waypoint(file);
            Spark.configManager.loadSettingHolder(waypoint,getWaypointPath(waypoint));
            wayPoints.add(waypoint);
        }
    }


    public ArrayList<Waypoint> getWayPoints() {
        return wayPoints;
    }



    String getWaypointPath(Waypoint wayPoint) {
        return getWaypointsPath()+System.getProperty("file.separator") + wayPoint.getName()+"."+fileEx;
    }
    String getWaypointsPath() {
        return Spark.ParentPath.getAbsolutePath() + System.getProperty("file.separator")+"waypoints"+System.getProperty("file.separator")+ server;
    }





    public static class Waypoint extends SettingsHolder {
        public StringSetting name = new StringSetting("Name",this,"","Display");

        ColorSetting color = new ColorSetting("Color",this,new Color(1,1,1),false,"Display");

        ModeSetting dim = new ModeSetting("Dim",this,"Overworld", Arrays.asList("Nether","Overworld","End"),"Location");

        IntSetting posX = new IntSetting("PosX",this,0,null,"Location");
        IntSetting posY = new IntSetting("PosY",this,0,null,"Location");
        IntSetting posZ = new IntSetting("PosZ",this,0,null,"Location");



        public Waypoint(String inName) {
            name.setValue(inName);
        }




        @Override
        public String getName() {
            return name.getValue();
        }



    }
}

