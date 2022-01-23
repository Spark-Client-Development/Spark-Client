package me.wallhacks.spark.mixin.mixins;

import me.wallhacks.spark.util.MC;
import net.minecraft.client.network.NetHandlerPlayClient;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={NetHandlerPlayClient.class})
public class MixinNetHandlerPlayClient implements MC {

}

