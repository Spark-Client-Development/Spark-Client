package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.systems.module.modules.render.Wallhack;
import net.minecraft.client.renderer.BufferBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteOrder;
import java.nio.IntBuffer;

@Mixin(BufferBuilder.class)
public abstract class MixinBufferBuilder {
    @Shadow
    private boolean noColor;
    @Shadow
    private IntBuffer rawIntBuffer;

    @Shadow
    public abstract int getColorIndex(int vertexIndex);

    @Inject(method = "putColorMultiplier", at = @At("HEAD"), cancellable = true)
    public void putColorMultiplier(float red, float green, float blue, int vertexIndex, CallbackInfo info) {
        if (Wallhack.INSTANCE.isEnabled()) {
            info.cancel();
            int i = this.getColorIndex(vertexIndex);
            int j = -1;
            float newAlpha = Wallhack.INSTANCE.opacity.getFloatValue() / 0xFF;

            if (!this.noColor) {
                j = this.rawIntBuffer.get(i);

                if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                    int k = (int) ((float) (j & 255) * red);
                    int l = (int) ((float) (j >> 8 & 255) * green);
                    int i1 = (int) ((float) (j >> 16 & 255) * blue);
                    int alpha = (int) (((float) (j >> 24 & 255) * newAlpha));
                    j = alpha << 24 | i1 << 16 | l << 8 | k;
                } else {
                    int j1 = (int) ((float) (j >> 24 & 255) * red);
                    int k1 = (int) ((float) (j >> 16 & 255) * green);
                    int l1 = (int) ((float) (j >> 8 & 255) * blue);
                    int alpha = (int) (((float) (j & 255) * newAlpha));
                    j = j1 << 24 | k1 << 16 | l1 << 8 | alpha;
                }
            }

            this.rawIntBuffer.put(i, j);
        }

    }
}
