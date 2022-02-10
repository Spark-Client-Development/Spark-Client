package me.wallhacks.spark.util.render;

import me.wallhacks.spark.systems.module.modules.player.Freecam;
import me.wallhacks.spark.util.MC;
import me.wallhacks.spark.util.objects.FreecamEntity;

public class CameraUtil implements MC {
    public static boolean freecamEnabled() {
        return Freecam.INSTANCE != null && Freecam.INSTANCE.isEnabled();
    }

    public static FreecamEntity getCamera() {
        return freecamEnabled() ? Freecam.INSTANCE.getCamera() : null;
    }

    public static void markChunksForRebuildOnDeactivation(int lastChunkX, int lastChunkZ) {
        net.minecraft.client.multiplayer.ChunkProviderClient provider = mc.world.getChunkProvider();
        net.minecraft.entity.Entity entity = mc.getRenderViewEntity();
        final int viewDistance = mc.gameSettings.renderDistanceChunks;
        final int chunkX = entity.chunkCoordX;
        final int chunkZ = entity.chunkCoordZ;

        final int minCameraCX = lastChunkX - viewDistance;
        final int maxCameraCX = lastChunkX + viewDistance;
        final int minCameraCZ = lastChunkZ - viewDistance;
        final int maxCameraCZ = lastChunkZ + viewDistance;
        final int minCX = chunkX - viewDistance;
        final int maxCX = chunkX + viewDistance;
        final int minCZ = chunkZ - viewDistance;

        final int maxCZ = chunkZ + viewDistance;
        for (int cz = minCZ; cz <= maxCZ; ++cz) {
            for (int cx = minCX; cx <= maxCX; ++cx) {
                if ((cx < minCameraCX || cx > maxCameraCX || cz < minCameraCZ || cz > maxCameraCZ) && provider.isChunkGeneratedAt(cx, cz)) {
                    int x = cx << 4;
                    int z = cz << 4;
                    mc.world.markBlockRangeForRenderUpdate(x, 0, z, x, 255, z);
                }
            }
        }
    }
}
