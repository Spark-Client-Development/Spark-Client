package me.wallhacks.spark.mixin.mixins;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.client.InputEvent;
import me.wallhacks.spark.event.client.RunTickEvent;
import me.wallhacks.spark.systems.module.modules.mics.InventoryManager;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({ Minecraft.class })
public class MixinMinecraft {
    @Inject(method = {"runTickKeyboard"}, at={@At(value="FIELD", target="Lnet/minecraft/client/Minecraft;currentScreen:Lnet/minecraft/client/gui/GuiScreen;", ordinal=0)}, locals= LocalCapture.CAPTURE_FAILSOFT)
    private void onRunTickKeyboard(CallbackInfo ci, int i) {
        if (Keyboard.getEventKeyState() && Spark.eventBus != null) {
            Spark.eventBus.post(new InputEvent(i));
        }
    }



    @Inject(method = "runTick", at = @At("HEAD"))
    public void runTick(CallbackInfo ci) {
        Spark.eventBus.post(new RunTickEvent());
    }

    @Inject(method = "crashed", at = @At("HEAD"))
    public void crashed(CrashReport crash, CallbackInfo callbackInfo) {
        Spark.configManager.Save();
        Spark.socialManager.SaveFriends();
        InventoryManager.instance.SaveKits();
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    public void shutdown(CallbackInfo callbackInfo) {
        Spark.configManager.Save();
        Spark.socialManager.SaveFriends();
        InventoryManager.instance.SaveKits();
    }
}
