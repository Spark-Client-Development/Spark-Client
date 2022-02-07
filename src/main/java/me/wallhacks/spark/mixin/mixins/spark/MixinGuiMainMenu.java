package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.Spark;
import net.minecraft.client.gui.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiMainMenu.class})
public class MixinGuiMainMenu {
    private long Time = System.nanoTime();

    @Inject(method = "drawScreen", at = @At("RETURN"))
    public void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo info) {
        float deltaTime = (System.nanoTime()-Time)/1000000f;
        Spark.altManager.render(mouseX, mouseY, deltaTime);
        Time=System.nanoTime();
    }

    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
    protected void keyTyped(char typedChar, int keyCode, CallbackInfo info) {
        Spark.altManager.keyTyped(keyCode, typedChar);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo info) {
        if (Spark.altManager.isMouseIn(mouseX)) info.cancel();
    }
}
