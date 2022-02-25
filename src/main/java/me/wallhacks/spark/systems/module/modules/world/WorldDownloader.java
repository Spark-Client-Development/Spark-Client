package me.wallhacks.spark.systems.module.modules.world;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.ChunkLoadEvent;
import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.event.player.PacketSendEvent;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.event.world.WorldLoadEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.StringSetting;
import me.wallhacks.spark.util.render.EspUtil;
import me.wallhacks.spark.util.render.RenderUtil;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.GameType;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Module.Registration(name = "WorldDownloader", description = "steals chunks")
public class WorldDownloader extends Module {


    public WorldDownloader() {
        instance = this;
    }
    public static WorldDownloader instance;




    @SubscribeEvent
    public void onWorld(WorldLoadEvent event){
        savedChestsContent.clear();
        setEnabled(false);
    }


    int saveSpeed = 10;





    public void onEnable() {

        WorldName =  "UnionWorldDownload - " + (mc.getCurrentServerData() != null ? mc.getCurrentServerData().serverIP : mc.getIntegratedServer().getWorldName());


        WorldSettings worldsettings = new WorldSettings(0, GameType.CREATIVE, false, false, WorldType.FLAT);

        FlatGeneratorInfo info = new FlatGeneratorInfo();
        info.setBiome(Biome.getIdForBiome(Biomes.VOID));

        info.getFlatLayers().add(new FlatLayerInfo(1, Blocks.AIR));
        info.updateLayers();

        worldsettings.setGeneratorOptions(info.toString());


        AnvilSaveConverter con = new AnvilSaveConverter(new File(mc.gameDir, "saves"), mc.getDataFixer());
        ISaveHandler isavehandler = con.getSaveLoader(WorldName, false);
        WorldInfo worldinfo = isavehandler.loadWorldInfo();

        if (worldinfo == null && worldsettings != null)
        {
            worldinfo = new WorldInfo(worldsettings, WorldName);
            isavehandler.saveWorldInfo(worldinfo);
        }

        Chunks.clear();

        Spark.sendInfo("["+this.getName()+"] " +ChatFormatting.BLUE +"Download started!");
        Spark.sendInfo("["+this.getName()+"] "+ ChatFormatting.BLUE +"Saving download to world: '"+WorldName+"'");

        downLoadInView();
    }
    private void downLoadInView() {
        if(mc.player == null || mc.world == null)
            return;

        int viewDist = mc.gameSettings.renderDistanceChunks;

        for (int x = (int)(mc.player.chunkCoordX) - viewDist; x <= (int)(mc.player.chunkCoordX) + viewDist; x++) {
            for (int z = (int)(mc.player.chunkCoordZ) - viewDist; z <= (int)(mc.player.chunkCoordZ) + viewDist; z++) {
                if(mc.world != null){
                    Chunk c = mc.world.getChunkProvider().getLoadedChunk(x, z);
                    if (c != null)
                        Chunks.add(c);
                }

            }
        }


    }

    String WorldName = null;


    public void saveChunk(Chunk c) {



        File file1 = new File(new File(mc.gameDir, "saves"),WorldName);

        File file3 = file1;
        if(mc.player.dimension == 1)
            file3 = new File(file1,"DIM1");
        if(mc.player.dimension == -1)
            file3 = new File(file1,"DIM-1");

        AnvilChunkLoader loader = new AnvilChunkLoader(file3,mc.getDataFixer());



        try {
            loader.saveChunk(mc.world, c);
            System.out.println("Saving: "+Chunks.get(0));
        } catch (MinecraftException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    ArrayList<Chunk> Chunks = new ArrayList<Chunk>();

    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {

        if(WorldName == null)
        {
            this.disable();
            return;
        }


        for (int i = 0; i < saveSpeed; i++)
            if(Chunks.size() > 0)
            {

                saveChunk(Chunks.get(0));
                Chunks.remove(0);
            }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        for (Chunk c : Chunks) {
            EspUtil.chunkEsp(c, new Color(10,240,10), 1);
        }

        for(TileEntityLockableLoot o : savedChestsContent){
            EspUtil.boundingESPBox(new AxisAlignedBB(o.getPos()), new Color(10,240,10,200),1);


        }
    }

    public void addChunkToDownload(Chunk c){
          if(!Chunks.contains(c))
            Chunks.add(c);
    }


    @SubscribeEvent
    public void chunkLoad(ChunkLoadEvent.Load event) {
        addChunkToDownload(event.getChunk());


    }
    @SubscribeEvent
    public void chunkUnLoad(ChunkLoadEvent.Unload event) {
        if(Chunks.contains(event.getChunk()))
            Chunks.remove(event.getChunk());


    }



    @SubscribeEvent
    public void onPacketGet(PacketReceiveEvent e) {

        if(e.getPacket() instanceof SPacketSetSlot) {
            SPacketSetSlot packetIn = (SPacketSetSlot) e.getPacket();
            EntityPlayer entityplayer = mc.player;
            ItemStack itemstack = packetIn.getStack();
            int i = packetIn.getSlot();

            if (packetIn.getWindowId() == -1) {
            } else if (packetIn.getWindowId() == -2) {

            } else {
                boolean flag = false;

                if (mc.currentScreen instanceof GuiContainerCreative) {
                    GuiContainerCreative guicontainercreative = (GuiContainerCreative) mc.currentScreen;
                    flag = guicontainercreative.getSelectedTabIndex() != CreativeTabs.INVENTORY.getIndex();
                }

                if (packetIn.getWindowId() == 0 && packetIn.getSlot() >= 36 && i < 45) {
                } else if (packetIn.getWindowId() == entityplayer.openContainer.windowId && (packetIn.getWindowId() != 0 || !flag)) {
                    onContainerSetStack(i, itemstack);
                }
            }

        }

    }


    public ArrayList<TileEntityLockableLoot> savedChestsContent = new ArrayList<TileEntityLockableLoot>();
    public void onContainerSetStack(int slotID, ItemStack stack) {
        for(TileEntity o : mc.world.loadedTileEntityList){
            if(o.getPos().equals(mc.objectMouseOver.getBlockPos()))
            {

                if(o instanceof TileEntityLockableLoot)
                {
                    TileEntityLockableLoot tile = (TileEntityLockableLoot)o;
                    if(!savedChestsContent.contains(tile))
                        savedChestsContent.add(tile);
                    if(tile instanceof TileEntityChest){
                        TileEntityChest c = ((TileEntityChest)tile);

                        if(slotID >= 27)
                        {


                            slotID -= 27;

                            if(c.adjacentChestZNeg == null && c.adjacentChestXNeg == null)
                                return;


                        }else
                        {
                            if(!(c.adjacentChestZNeg == null && c.adjacentChestXNeg == null))
                                return;
                        }

                        if(stack != null && slotID < tile.getSizeInventory())
                            c.setInventorySlotContents(slotID, stack);
                    }
                    if(stack != null && slotID < tile.getSizeInventory())
                        tile.setInventorySlotContents(slotID, stack);
                    addChunkToDownload(mc.world.getChunk(tile.getPos()));
                    return;
                }
            }

        }

    }

}
