package me.wallhacks.spark.manager;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.event.world.WorldLoadEvent;
import me.wallhacks.spark.util.FileUtil;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.WorldUtils;
import me.wallhacks.spark.util.objects.MCStructures;
import net.minecraft.block.material.Material;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGeneratorEnd;
import net.minecraft.world.gen.ChunkGeneratorHell;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.*;

public class SeedManager implements MC {

    public SeedManager() {
        instance = this;
        Spark.eventBus.register(this);

        LOADSEEDS();
    }
    public static SeedManager instance;

    public static IntegratedServer integratedServer;

    HashMap<String,String> seeds = new HashMap<>();

    public Collection<String> servers() {
        return seeds.keySet();
    }

    public String seedForServer(String s) {
        return seeds.getOrDefault(s,null);
    }

    @SubscribeEvent
    public void worldLoadEvent(WorldLoadEvent event) {

        reload();

    }

    public boolean setSeed(String seed) {
        if(mc.isSingleplayer())
            return false;
        setSeed(mc.getCurrentServerData().serverIP,seed);
        return true;
    }

    public void setSeed(String server,String seed) {

        seeds.put(server,seed);
        if(mc.getCurrentServerData() != null && seed.equals(mc.getCurrentServerData().serverIP))
            reload();

    }



    public void reload() {

        if(mc.world == null || (!mc.isSingleplayer() && (mc.getCurrentServerData() == null || !seeds.containsKey(mc.getCurrentServerData().serverIP))))
        {
            integratedServer = null;
            return;
        }

        long seed = (!mc.isSingleplayer()) ? WorldUtils.getSeed(mc.getCurrentServerData().serverIP) : mc.integratedServer.getEntityWorld().getSeed();

        YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(mc.proxy, UUID.randomUUID().toString());
        MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
        GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
        PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(mc.gameDir, MinecraftServer.USER_CACHE_FILE.getName()));
        TileEntitySkull.setProfileCache(playerprofilecache);
        TileEntitySkull.setSessionService(minecraftsessionservice);
        PlayerProfileCache.setOnlineMode(false);




        WorldSettings worldSettings = new WorldSettings(seed, GameType.CREATIVE,true,false, WorldType.CUSTOMIZED);
        integratedServer = new IntegratedServer(mc, "lol1", "lol1", worldSettings, yggdrasilauthenticationservice, minecraftsessionservice, gameprofilerepository, playerprofilecache);



        ((ChunkGeneratorEnd)integratedServer.getWorld(1).getChunkProvider().chunkGenerator).generateChunk(1,1);
        ((ChunkGeneratorOverworld)integratedServer.getWorld(0).getChunkProvider().chunkGenerator).generateChunk(1,1);
        ((ChunkGeneratorHell)integratedServer.getWorld(-1).getChunkProvider().chunkGenerator).generateChunk(1,1);

    }





    String getKitsFile(){
        return Spark.ParentPath.getAbsolutePath() + ""+System.getProperty("file.separator")+"seeds.sex";
    }

    public void LOADSEEDS() {
         try {
            String[] list = FileUtil.read(getKitsFile()).split("\n");

             for (String s : list) {
                 if(s.split(":").length >= 2)
                    seeds.put(s.split(":")[0],s.split(":")[1]);
             }
        } catch (Exception e) {
            Spark.logger.info("Failed to load seeds");
            e.printStackTrace();
        }
    }


    public void SAVESEEDS() {
        try {
            String content = "";
            for(String e : seeds.keySet())
                content = content + e+":"+seeds.get(seeds) + "\n";

            FileUtil.write(getKitsFile(),content);

        } catch (Exception e) {
            Spark.logger.info("Failed to save seeds");
            e.printStackTrace();
        }
    }

    public ChunkGeneratorEnd getChunkGeneratorEnd() {
        return ((ChunkGeneratorEnd)integratedServer.getWorld(1).getChunkProvider().chunkGenerator);
    }
    public ChunkGeneratorOverworld getChunkGeneratorOverworld() {
        return ((ChunkGeneratorOverworld)integratedServer.getWorld(0).getChunkProvider().chunkGenerator);
    }
    public ChunkGeneratorHell getChunkGeneratorHell() {
        return ((ChunkGeneratorHell)integratedServer.getWorld(-1).getChunkProvider().chunkGenerator);
    }
    public WorldServer getOverworld() {
        return integratedServer.getWorld(0);
    }

    public MCStructures getStructure(int chunkX, int chunkY,int dim) {
        if(integratedServer == null)
            return null;
        if(dim == 1)
        {
            if(SeedManager.instance.getChunkGeneratorEnd().endCityGen.canSpawnStructureAtCoords(chunkX,chunkY))
                return MCStructures.EndCity;
        }
        else if(dim == -1)
        {

            if(getChunkGeneratorHell().genNetherBridge.canSpawnStructureAtCoords(chunkX,chunkY))
                return MCStructures.NetherFortress;
        }
        else if(dim == 0)
        {
            ChunkGeneratorOverworld generatorOverworld = getChunkGeneratorOverworld();


            if(generatorOverworld.villageGenerator.canSpawnStructureAtCoords(chunkX,chunkY))
                return MCStructures.Village;

            if(generatorOverworld.woodlandMansionGenerator.canSpawnStructureAtCoords(chunkX,chunkY))
                return MCStructures.Mansion;
            //if(generatorOverworld.mineshaftGenerator.canSpawnStructureAtCoords(chunkX,chunkY))
            //    return MCStructures.Mineshaft;
            if(generatorOverworld.strongholdGenerator.canSpawnStructureAtCoords(chunkX,chunkY))
                return MCStructures.Stronghold;
            if(generatorOverworld.oceanMonumentGenerator.canSpawnStructureAtCoords(chunkX,chunkY))
                return MCStructures.OceanMonument;
            if(generatorOverworld.scatteredFeatureGenerator.canSpawnStructureAtCoords(chunkX,chunkY))
            {
                Biome biomeIn = getOverworld().getBiome(new BlockPos(chunkX * 16 + 8, 0, chunkY * 16 + 8));

                if (biomeIn != Biomes.JUNGLE && biomeIn != Biomes.JUNGLE_HILLS) {
                    if (biomeIn == Biomes.SWAMPLAND) {
                        return MCStructures.WitchHut;
                    } else if (biomeIn != Biomes.DESERT && biomeIn != Biomes.DESERT_HILLS) {
                        if (biomeIn == Biomes.ICE_PLAINS || biomeIn == Biomes.COLD_TAIGA) {
                            return MCStructures.Igloo;
                        }
                    } else {
                        return MCStructures.DesertTemple;
                    }
                } else {
                    return MCStructures.JungleTemple;
                }
            }

        }
        return null;
    }







}
