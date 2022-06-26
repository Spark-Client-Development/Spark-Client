package me.wallhacks.spark.mixin.mixins.spark;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.systems.module.modules.player.InventoryManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ GuiScreen.class })
public class MixinGuiScreen {



    private long Time = System.nanoTime();

    @Inject(method = "drawScreen", at = @At("RETURN"))
    public void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo info) {
        if(Minecraft.getMinecraft().world != null)
            return;
        float deltaTime = (System.nanoTime()-Time)/1000000f;
        Time=System.nanoTime();
    }

    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
    protected void keyTyped(char typedChar, int keyCode, CallbackInfo info) {
        if(Minecraft.getMinecraft().world != null)
            return;
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo info) {
        if(Minecraft.getMinecraft().world != null)
            return;
    }




    @Inject(method = "initGui", at = @At("HEAD"))
    public void initGui(final CallbackInfo callbackInfo) {
        Minecraft mc = Minecraft.getMinecraft();

        if(mc.currentScreen instanceof GuiShulkerBox || mc.currentScreen instanceof GuiChest)
        {
            GuiContainer screen = (GuiContainer)mc.currentScreen;

            InventoryManager instance = InventoryManager.instance;
            if(instance.isEnabled() && instance.isAuto(screen))
                instance.StartSteal();

            if(instance.isEnabled() && !instance.isAuto(screen))
                screen.buttonList.add(new GuiButton(66, screen.width/2+5, (screen.height-screen.getYSize())/2 - 6, 40, 12, "Steal"));

        }

    }

    @Inject(method = "actionPerformed", at = @At("RETURN"))
    public void actionPerformed(GuiButton button, final CallbackInfo callbackInfo){
        Minecraft mc = Minecraft.getMinecraft();
        if(mc.currentScreen instanceof GuiShulkerBox || mc.currentScreen instanceof GuiChest)
        if(button.id == 66){
            InventoryManager.instance.StartSteal();
        }
    }

}
