package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.AttackEvent;
import me.wallhacks.spark.event.player.PlayerDamageBlockEvent;
import me.wallhacks.spark.event.player.PlayerProcessRightClickEvent;
import me.wallhacks.spark.util.MC;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP implements MC {



    @Inject(method = "onPlayerDamageBlock", at = @At("HEAD"), cancellable = true)
    public void onPlayerDamageBlockPre(BlockPos posBlock, EnumFacing directionFacing, final CallbackInfoReturnable<Boolean> callbackInfoReturnable)
    {
        PlayerDamageBlockEvent.Pre event = new PlayerDamageBlockEvent.Pre(posBlock,directionFacing);
        Spark.eventBus.post(event);


        if (event.isCanceled())
        {


            callbackInfoReturnable.setReturnValue(event.getReturnValue());
            callbackInfoReturnable.cancel();
        }
    }
    @Inject(method = "onPlayerDamageBlock",at = @At(value = "RETURN"))
    public void onPlayerDamageBlockPost(BlockPos posBlock, EnumFacing directionFacing, final CallbackInfoReturnable<Boolean> callbackInfoReturnable)
    {
        PlayerDamageBlockEvent.Post event = new PlayerDamageBlockEvent.Post(posBlock,directionFacing);
        Spark.eventBus.post(event);

    }



    @Inject(method = "attackEntity", at = @At(value = "HEAD"),cancellable = true)
    public void attackEntityPre(EntityPlayer playerIn, Entity targetEntity,CallbackInfo info) {
        AttackEvent.Pre event = new AttackEvent.Pre(targetEntity);

        Spark.eventBus.post(event);

        if(event.isCanceled())
            info.cancel();
    }

    @Inject(method = "attackEntity", at = @At(value = "RETURN"))
    public void attackEntityPost(EntityPlayer playerIn, Entity targetEntity,CallbackInfo info) {
        AttackEvent.Post event = new AttackEvent.Post(targetEntity);

        Spark.eventBus.post(event);


    }


    @Inject(method = "processRightClick", at = @At(value = "HEAD"))
    public void processRightClick(EntityPlayer player, World worldIn, EnumHand hand, CallbackInfoReturnable<EnumActionResult> info) {

        PlayerProcessRightClickEvent event = new PlayerProcessRightClickEvent(player,worldIn,hand);
        Spark.eventBus.post(event);


    }

    @Inject(method = "resetBlockRemoving", at = @At(value = "HEAD"), cancellable = true)
    public void resetBlockRemoving(CallbackInfo info) {
        if(Spark.breakManager.block != null)
            info.cancel();
    }

}
