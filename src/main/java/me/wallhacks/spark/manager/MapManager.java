package me.wallhacks.spark.manager;

import com.google.common.io.Files;
import io.netty.util.internal.ConcurrentSet;
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
import me.wallhacks.spark.util.objects.MapImage;
import me.wallhacks.spark.util.objects.Vec2i;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
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
import java.util.Set;
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

    private ConcurrentHashMap<Vec3i,SparkMap> loadedMaps = new ConcurrentHashMap<Vec3i,SparkMap>();

    ConcurrentSet<Vec3i> toLoad = new ConcurrentSet<Vec3i>();
    ConcurrentSet<ChunkPos> chunksToLoad = new ConcurrentSet<>();

    ConcurrentSet<Vec3i> toSave = new ConcurrentSet<Vec3i>();


    ConcurrentHashMap<Vec3i,Integer> mapsUsed = new ConcurrentHashMap<Vec3i,Integer>();


    public SparkMap getMap(Vec2i mapPos,int dim)
    {
        return getMap(new Vec3i(mapPos.x,dim,mapPos.y));
    }
    public SparkMap getMap(Vec3i mapPos){

        if(!loadedMaps.containsKey(mapPos)) {
            toLoad.add(mapPos);
            loadedMaps.put(mapPos, new SparkMap(mapPos));
        }

        mapsUsed.put(mapPos,20*12);


        return loadedMaps.get(mapPos);

    }

    @SubscribeEvent
    public void worldLoadEvent(WorldLoadEvent event) {
        String serv = mc.getCurrentServerData() != null ? mc.getCurrentServerData().serverIP : "singleplayer";
        if(!serv.equals(CurrentServer))
        {
            loadedMaps.clear();
            mapsUsed.clear();
            toSave.clear();
            toLoad.clear();
            chunksToLoad.clear();
            CurrentServer = serv;
        }

    }




    @SubscribeEvent
    public void onThread(ThreadEvent event) {

        if (toLoad.size() > 0) {
            Vec3i v = toLoad.iterator().next();
            toLoad.remove(v);

            SparkMap map = getMap(v);
            if (map != null)
                LoadMap(map);


        }

        else if (chunksToLoad.size() > 0) {
            ChunkPos p = chunksToLoad.iterator().next();
            chunksToLoad.remove(p);
            Chunk c = mc.world.getChunk(p.x,p.z);
            if(c != null)
            {
                Vec2i mapAtC = SparkMap.getMapPosFromWorldPos(c.getPos().x*16, c.getPos().z*16);
                Vec3i mapPos = (new Vec3i(mapAtC.x,getDim(),mapAtC.y));

                SparkMap M = getMap(mapPos);
                if(toLoad.contains(mapPos)) {
                    toLoad.remove(mapPos);
                    LoadMap(M);
                }
                if(mc.world != null)
                    M.updateMapData(c, mc.world);
                //save map to files
                toSave.add(mapPos);
            }
        }

        else if (toSave.size() > 0) {
            Vec3i v = toSave.iterator().next();
            toSave.remove(v);

            if (ClientConfig.getInstance().SaveMap.isOn()) {
                SparkMap map = getMap(v);
                if (map != null)
                    SaveMap(map);
            }


        }

    }

    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {

        Set<Vec3i> unused = mapsUsed.keySet();
        for (Vec3i map : unused) {
            int i = mapsUsed.get(map);

            if(i <= 0)
            {
                mapsUsed.remove(map);
                loadedMaps.remove(map);
            }
            else
            {
                mapsUsed.put(map,i-1);
            }
        }
        for (SparkMap map : loadedMaps.values())
        {
            if(map.updateMapTextures())
                break;
        }





    }





    @SubscribeEvent
    public void onChunk(ChunkLoadEvent.Load event) {
        Chunk c = event.getChunk();

        chunksToLoad.add(c.getPos());

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

        File f = new File(getPath(m));
        if (!f.exists())
            return false;

        try {
            BufferedImage image = ImageIO.read(f);
            if(image != null && image.getHeight() == MapImage.size && image.getWidth() == MapImage.size)
                m.setBufferedImage(image);

            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;

    }
    public void SaveMap(SparkMap m){
        String path = getPath(m);

        if(m.isEmpty())
            return;

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
