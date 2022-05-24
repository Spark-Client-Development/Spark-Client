package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.client.InputEvent;
import me.wallhacks.spark.event.client.RunTickEvent;
import me.wallhacks.spark.event.player.RightClickEvent;
import me.wallhacks.spark.event.world.WorldLoadEvent;
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
        Spark.save();
    }

    @Inject(method = "rightClickMouse", at = @At("HEAD"),cancellable = true)
    public void rightClickMouse(CallbackInfo callbackInfo) {
        RightClickEvent event = new RightClickEvent();
        Spark.eventBus.post(event);

        if (event.isCanceled())
            callbackInfo.cancel();

    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    public void shutdown(CallbackInfo callbackInfo) {
        Spark.save();

    }

    @Inject(method = "loadWorld", at = @At("RETURN"))
    public void loadWorld(CallbackInfo callbackInfo) {
        Spark.eventBus.post(new WorldLoadEvent());
    }

}
