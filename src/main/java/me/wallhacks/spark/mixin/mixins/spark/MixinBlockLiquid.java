package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.block.LiquidCollisionBBEvent;
import me.wallhacks.spark.event.player.ChunkLoadEvent;
import me.wallhacks.spark.util.MC;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockLiquid.class)
public class MixinBlockLiquid implements MC {




    @Inject(method = "getCollisionBoundingBox", at = @At("HEAD"), cancellable = true)
    public void getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, final CallbackInfoReturnable<AxisAlignedBB> callbackInfoReturnable)
    {
        LiquidCollisionBBEvent event = new LiquidCollisionBBEvent(pos);
        Spark.eventBus.post(event);
        if (event.isCanceled())
        {

            callbackInfoReturnable.setReturnValue(event.getBoundingBox());
            callbackInfoReturnable.cancel();
        }
    }


}

