package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.util.objects.FreecamEntity;
import me.wallhacks.spark.util.render.CameraUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.debug.DebugRendererChunkBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DebugRendererChunkBorder.class)
public class MixinDebugRendererChunkBorder
{
    @Redirect(method = "render", at = @At(value = "FIELD", opcode = 180, target = "Lnet/minecraft/client/Minecraft;player:Lnet/minecraft/client/entity/EntityPlayerSP;"))
    private EntityPlayerSP useCameraEntity(Minecraft mc) {
        FreecamEntity camera = CameraUtil.getCamera();
        if (camera != null)
            return (EntityPlayerSP) camera;

        return mc.player;
    }
}
