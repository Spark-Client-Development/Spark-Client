package me.wallhacks.spark.systems.module.modules;

import me.wallhacks.spark.Spark;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import me.wallhacks.spark.event.client.ThreadEvent;
import me.wallhacks.spark.event.player.ChunkLoadEvent;
import me.wallhacks.spark.event.player.PacketReceiveEvent;
import me.wallhacks.spark.event.player.PlayerLivingTickEvent;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.WorldUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SearchChunksModule<T extends BlockPos> extends Module {


    CopyOnWriteArrayList<ChunkPos> chunksToSearch = new CopyOnWriteArrayList<>();


    protected ConcurrentHashMap<Chunk, CopyOnWriteArrayList<T>> found = new ConcurrentHashMap<Chunk,CopyOnWriteArrayList<T>>();


    protected boolean needsAdjacentChunks() {
        return false;
    }

    @Override
    public void onEnable() {
        chunksToSearch.clear();
        found.clear();

        if(mc.player == null || mc.world == null)
            return;
        for (Chunk c : mc.world.getChunkProvider().loadedChunks.values()) {
            addedChunk(c.getPos());
        }
    }
    @SubscribeEvent
    public void onWorld(WorldEvent.Load event) {
        chunksToSearch.clear();
        found.clear();
    }
    @SubscribeEvent
    public void onUpdate(PlayerLivingTickEvent event) {

    }
    @SubscribeEvent
    public void onThread(ThreadEvent event) {
        if(chunksToSearch.size() > 0)
        {

            Chunk c = mc.world.getChunk(chunksToSearch.get(0).x,chunksToSearch.get(0).z);
            if(chunksToSearch.get(0) != null && c != null)
            {
                if(found.containsKey(c))
                    found.remove(c);
                searchChunk(c);
            }
            chunksToSearch.remove(chunksToSearch.get(0));
        }
    }



    protected void searchChunk(Chunk chunk) {
    }



    protected void addFound(T add) {
        Chunk c = mc.world.getChunk(add);

        if(!found.containsKey(c))
            found.put(c,new CopyOnWriteArrayList<>());

        if(found.get(c).contains(add))
            found.get(c).remove(add);
        found.get(c).add(add);
    }
    protected void removeFound(BlockPos remove) {
        Chunk c = mc.world.getChunk(remove);

        if(found.containsKey(c) && found.get(c).contains(remove))
        {
            found.get(c).remove(remove);
        }
    }


    @SubscribeEvent
    public void chunkLoad(ChunkLoadEvent.Load event) {
        ChunkPos[] chunks = WorldUtils.getAdjacentChunks(event.getChunk().getPos());
        for (ChunkPos c : chunks)
            addedChunk(c);
        addedChunk(event.getChunk().getPos());

    }
    @SubscribeEvent
    public void chunkUnLoad(ChunkLoadEvent.Unload event) {
        if(found.containsKey(event.getChunk()))
            found.remove(event.getChunk());

        if(chunksToSearch.contains(event.getChunk().getPos()))
            chunksToSearch.remove(event.getChunk().getPos());

        if(needsAdjacentChunks())
        {
            ChunkPos[] chunks = WorldUtils.getAdjacentChunks(event.getChunk().getPos());
            for (ChunkPos c : chunks)
                if(chunksToSearch.contains(c))
                    chunksToSearch.remove(c);
        }

    }

    @SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof SPacketMultiBlockChange) {
            for (SPacketMultiBlockChange.BlockUpdateData pos : ((SPacketMultiBlockChange) event.getPacket()).getChangedBlocks()) {
                blockChanged(pos.getPos());
            }
        } else if (event.getPacket() instanceof SPacketBlockChange) {
            blockChanged(((SPacketBlockChange) event.getPacket()).getBlockPosition());
        }
    }

    protected void blockChanged(BlockPos pos) {

    }

    void addedChunk(ChunkPos pos) {
        if(chunksToSearch.contains(pos))
            return;



        if(needsAdjacentChunks())
        {
            ChunkPos[] chunks = WorldUtils.getAdjacentChunks(pos);


            for (ChunkPos c : chunks) {
                if(mc.world.getChunkProvider().provideChunk(c.x,c.z) == mc.world.getChunkProvider().blankChunk)
                    return;
            }



        }



        chunksToSearch.add(pos);
    }
}
