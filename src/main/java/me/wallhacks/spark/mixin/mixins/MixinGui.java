package me.wallhacks.spark.mixin.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ Gui.class })
public class MixinGui {


    @Inject(method = "drawGradientRect", at = @At("HEAD"), cancellable = true)
    private void drawGradientRect(final CallbackInfo callbackInfo) {
        //if(Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu)
          //  callbackInfo.cancel();
    }

}
