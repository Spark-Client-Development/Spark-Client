package me.wallhacks.spark.mixin.mixins;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.player.AttackEvent;
import me.wallhacks.spark.event.player.PlayerProcessRightClickEvent;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ PlayerControllerMP.class })
public class MixinPlayerControllerMP {



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
    public void processRightClick(EntityPlayer player, World worldIn, EnumHand hand, CallbackInfo info) {
        PlayerProcessRightClickEvent event = new PlayerProcessRightClickEvent(player,worldIn,hand);

        Spark.eventBus.post(event);


    }


}
