package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.manager.SystemManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import me.wallhacks.spark.systems.module.modules.render.Xray;

@Mixin({ Block.class })
public class MixinBlock {


    @Inject(method = "shouldSideBeRendered", at = @At("HEAD"), cancellable = true)
    public void shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side, final CallbackInfoReturnable<Boolean> callbackInfo)
    {
        Xray xray = SystemManager.getModule(Xray.class);

        if(xray.isEnabled())
        {
            if(!xray.isXrayBlock(blockState.getBlock()))
                callbackInfo.setReturnValue(false);
            else
                callbackInfo.setReturnValue(!xray.isXrayBlock(blockAccess.getBlockState(pos.offset(side)).getBlock()));

            callbackInfo.cancel();

        }
    }
    @Inject(method = "getLightValue", at = @At("HEAD"), cancellable = true)
    public void getLightValue(final CallbackInfoReturnable<Integer> callbackInfo) {

        Xray xray = SystemManager.getModule(Xray.class);
        if(xray.isEnabled())
        {
            callbackInfo.setReturnValue(1000);
            callbackInfo.cancel();
        }
    }

}
