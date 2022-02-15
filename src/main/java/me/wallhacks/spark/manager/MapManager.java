package me.wallhacks.spark.manager;

import com.google.common.io.Files;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.client.ThreadEvent;
import me.wallhacks.spark.event.player.ChunkLoadEvent;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.event.world.WorldLoadEvent;
import me.wallhacks.spark.systems.clientsetting.ClientSetting;
import me.wallhacks.spark.systems.clientsetting.clientsettings.ClientConfig;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.util.GuiUtil;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.maps.SparkMap;
import me.wallhacks.spark.util.objects.Vec2i;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MapManager implements MC {

    public MapManager() {
        instance = this;

        Spark.eventBus.register(this);

    }
    public static MapManager instance;








    String CurrentServer;
    private ConcurrentHashMap<Integer, ConcurrentHashMap<Vec2i, SparkMap>> loadedMaps = new ConcurrentHashMap<Integer,ConcurrentHashMap<Vec2i,SparkMap>>();

    CopyOnWriteArrayList<SparkMap> toLoad = new CopyOnWriteArrayList<SparkMap>();

    public SparkMap getMap(Vec2i mapPos,int dim){

        if(!loadedMaps.containsKey(dim))
            loadedMaps.put(dim, new ConcurrentHashMap<Vec2i,SparkMap>());
        if(!loadedMaps.get(dim).containsKey(mapPos))
        {
            loadedMaps.get(dim).put(mapPos,new SparkMap(mapPos,dim));
            if(!toLoad.contains(loadedMaps.get(dim).get(mapPos)))
                toLoad.add(loadedMaps.get(dim).get(mapPos));
        }

        return loadedMaps.get(dim).get(mapPos);

    }

    @SubscribeEvent
    public void worldLoadEvent(WorldLoadEvent event) {
        String serv = mc.getCurrentServerData() != null ? mc.getCurrentServerData().serverIP : "singleplayer";
        if(!serv.equals(CurrentServer))
        {
            loadedMaps.clear();
            CurrentServer = serv;
        }

    }




    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {

        while(toLoad.size() > 0){
            SparkMap m = toLoad.get(0);
            Spark.threadManager.execute(() -> {LoadMap(m);});
            toLoad.remove(0);
        }

    }





    @SubscribeEvent
    public void onChunk(ChunkLoadEvent.Load event) {
        Chunk c = event.getChunk();

        Vec2i mapAtC = SparkMap.getMapPosFromWorldPos(c.getPos().x*16, c.getPos().z*16);

        SparkMap M = getMap(mapAtC,getDim());

        boolean needsLoad = toLoad.contains(M);
        if(needsLoad)
            toLoad.remove(M);

        Spark.threadManager.execute(() -> {

            //don't remove this
            if(needsLoad)
                LoadMap(M);

            M.updateMapData(c, mc.world);

            //save map to files
            if(ClientConfig.getInstance().SaveMap.isOn())
                SaveMap(M);
        });


    }











    public String getPath(SparkMap m){

        return Spark.ParentPath.getAbsoluteFile()+System.getProperty("file.separator")+"maps"+System.getProperty("file.separator")+CurrentServer+System.getProperty("file.separator")+"Dim"+m.dim+System.getProperty("file.separator")+m.pos.toString()+".png";
    }


    int lastDim = 0;
    public int getDim(){
        int dim = mc.player == null ? lastDim : mc.player.dimension;
        lastDim = dim;
        return dim;
    }



    public boolean LoadMap(SparkMap m){

        m.setBufferedImage(new BufferedImage(128, 128, BufferedImage.TYPE_4BYTE_ABGR));

        File f = new File(getPath(m));
        if (!f.exists())
            return false;




        try {
            m.setBufferedImage(ImageIO.read(f));


            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;

    }
    public void SaveMap(SparkMap m){
        String path = getPath(m);

        File f = new File(path);
        if (!f.exists())
        {
            try {
                Files.createParentDirs(f);
                f.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

        }

        try {
            ImageIO.write(m.getBufferedImage(), "PNG", f);
        } catch (IOException e) {
            System.out.println("Image could not be read");
        }

    }

}
