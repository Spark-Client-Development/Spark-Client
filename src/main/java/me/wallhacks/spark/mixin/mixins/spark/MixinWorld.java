package me.wallhacks.spark.mixin.mixins.spark;


import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.block.BlockChangeEvent;
import me.wallhacks.spark.event.entity.LiquidPushEvent;
import me.wallhacks.spark.event.player.EntityAddEvent;
import me.wallhacks.spark.manager.SystemManager;
import me.wallhacks.spark.systems.module.modules.movement.Jesus;
import me.wallhacks.spark.systems.module.modules.world.ClientTime;
import me.wallhacks.spark.systems.module.modules.world.ClientWeather;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ World.class })
public class MixinWorld {




    @Inject(method = "onEntityAdded", at = @At("HEAD"), cancellable = true)
    public void onEntityAdded(Entity entityIn, final CallbackInfo info) {

        EntityAddEvent event = new EntityAddEvent(entityIn);
        Spark.eventBus.post(event);
    }

    @Inject(method = "getThunderStrength", at = @At("HEAD"), cancellable = true)
    public void getThunderStrength(final CallbackInfoReturnable<Float> callbackInfo) {

        ClientWeather weather = SystemManager.getModule(ClientWeather.class);
        if(weather.isEnabled())
        {
            callbackInfo.setReturnValue(weather.Thunder.getFloatValue());
            callbackInfo.cancel();
        }
    }


    @Inject(method = "getRainStrength", at = @At("HEAD"), cancellable = true)
    public void getRainStrength(final CallbackInfoReturnable<Float> callbackInfo) {

        ClientWeather weather = SystemManager.getModule(ClientWeather.class);
        if(weather.isEnabled())
        {
            callbackInfo.setReturnValue(weather.Rain.getFloatValue());
            callbackInfo.cancel();
        }
    }

    @Inject(method = "getWorldTime", at = @At("HEAD"), cancellable = true)
    public void getWorldTime(final CallbackInfoReturnable<Long> callbackInfo) {

        ClientTime time = SystemManager.getModule(ClientTime.class);
        if(time.isEnabled())
        {
            callbackInfo.setReturnValue(time.getTime());
            callbackInfo.cancel();
        }
    }

    @Redirect(method={"handleMaterialAcceleration"}, at=@At(value="INVOKE", target="Lnet/minecraft/entity/Entity;isPushedByWater()Z"))
    public boolean isPushedbyWaterHook(Entity entity) {
        LiquidPushEvent event = new LiquidPushEvent(entity);
        Spark.eventBus.post(event);
        return entity.isPushedByWater() && !event.isCanceled();
    }


    @Inject(method = "setBlockState", at = @At("RETURN"))
    public void setBlockStatePost(BlockPos pos, IBlockState newState, int flags, final CallbackInfoReturnable<Boolean> callbackInfo) {
        if(!this.equals(Minecraft.getMinecraft().world))
            return;

        BlockChangeEvent event = new BlockChangeEvent(pos);
        Spark.eventBus.post(event);

    }

}
