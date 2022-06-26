package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.Spark;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiMainMenu.class})
public class MixinGuiMainMenu {

    //we need this for some reason
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo info) {
        if(Minecraft.getMinecraft().world != null)
            return;
    }
    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
    protected void keyTyped(char typedChar, int keyCode, CallbackInfo info) {
        if(Minecraft.getMinecraft().world != null)
            return;
    }
}
