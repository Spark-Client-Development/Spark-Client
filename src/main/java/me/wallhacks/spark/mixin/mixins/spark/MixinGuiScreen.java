package me.wallhacks.spark.mixin.mixins.spark;

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
import me.wallhacks.spark.systems.module.modules.mics.ChestStealer;

@Mixin({ GuiScreen.class })
public class MixinGuiScreen {


    @Inject(method = "initGui", at = @At("HEAD"))
    public void initGui(final CallbackInfo callbackInfo) {
        Minecraft mc = Minecraft.getMinecraft();

        if(mc.currentScreen instanceof GuiShulkerBox || mc.currentScreen instanceof GuiChest)
        {
            GuiContainer screen = (GuiContainer)mc.currentScreen;

            ChestStealer instance = ChestStealer.getInstance();
            if(instance.isEnabled() && instance.isAuto())
                instance.StartSteal();

            if(instance.isEnabled() && !instance.isAuto())
                screen.buttonList.add(new GuiButton(66, screen.width/2+5, (screen.height-screen.getYSize())/2 - 6, 40, 12, "Steal"));

        }

    }

    @Inject(method = "actionPerformed", at = @At("RETURN"))
    public void actionPerformed(GuiButton button, final CallbackInfo callbackInfo){
        Minecraft mc = Minecraft.getMinecraft();
        if(mc.currentScreen instanceof GuiShulkerBox || mc.currentScreen instanceof GuiChest)
        if(button.id == 66){
            ChestStealer.getInstance().StartSteal();
        }
    }

}
