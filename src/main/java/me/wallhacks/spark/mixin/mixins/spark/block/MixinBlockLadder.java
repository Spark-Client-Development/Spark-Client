package me.wallhacks.spark.mixin.mixins.spark.block;

import me.wallhacks.spark.systems.module.modules.render.Wallhack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.util.BlockRenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockLadder.class)
public class MixinBlockLadder {
    @Inject(method = {"getRenderLayer"}, at = {@At("HEAD")}, cancellable = true)
    public void getRenderLayer(final CallbackInfoReturnable<BlockRenderLayer> callback) {
        if (Wallhack.INSTANCE.isEnabled() && !Wallhack.INSTANCE.isXrayBlock((Block)(Object) this)) {
            callback.cancel();
            callback.setReturnValue(BlockRenderLayer.TRANSLUCENT);
        }
    }
}
