package me.wallhacks.spark.mixin.mixins.spark.block;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.module.modules.render.Trajectories;
import me.wallhacks.spark.systems.module.modules.render.Wallhack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ Block.class })
public abstract class MixinBlock {
    @Inject(method = "shouldSideBeRendered", at = @At("HEAD"), cancellable = true)
    public void shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> callback) {
        if (Wallhack.INSTANCE.isXrayBlock(blockState.getBlock())) callback.setReturnValue(true);
    }

    @Inject(method = "getRenderLayer", at = @At("HEAD"), cancellable = true)
    public void getRenderLayer(CallbackInfoReturnable<BlockRenderLayer> callback) {
        if (Wallhack.INSTANCE.isEnabled() && !Wallhack.INSTANCE.isXrayBlock((Block)(Object) this)) {
            callback.cancel();
            callback.setReturnValue(BlockRenderLayer.TRANSLUCENT);
        }
    }

    @Inject(method = "getLightValue", at = @At("HEAD"), cancellable = true)
    public void getLightValue(CallbackInfoReturnable<Integer> callback) {
        if (Wallhack.INSTANCE.isXrayBlock((Block)(Object)this)) {
            callback.setReturnValue(1000);
        }
    }
}
