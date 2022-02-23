package me.wallhacks.spark.systems.module.modules.misc;

import java.util.List;

import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.systems.setting.settings.BooleanSetting;
import me.wallhacks.spark.systems.setting.settings.IntSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

@Module.Registration(name = "AutoReconnect", description = "Automatically reconnects")
public class AutoReconnect extends Module {
    IntSetting time = new IntSetting("Time in seconds",this,4,0,180);
    BooleanSetting NoReconnectOnAutoLog = new BooleanSetting("Don't reconnect after AutoLog", this, true);

    boolean inAutoReconnectScreen = false;
    ServerData server = null;

    GuiScreen gui;
    int ticks = 0;
    int seconds = 0;

    @SubscribeEvent
    public void clientTick(ClientTickEvent event) {
        if (inAutoReconnectScreen) {
            ticks++;

            if (ticks % 40 == 0) { //  every second
                seconds--;
                List<GuiButton> buttons = gui.buttonList;
                if (buttons.size() > 0)
                    buttons.get(0).displayString = "Reconnecting in " + String.valueOf(seconds);

                if (seconds == 0) {
                    FMLClientHandler.instance().connectToServer(new GuiMainMenu(), server);
                    ticks = 0;
                    inAutoReconnectScreen = false;
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        server = Minecraft.getMinecraft().getCurrentServerData();
    }

    @SubscribeEvent
    public void onGuiInit(final InitGuiEvent.Post event) {
        if (event.getGui() instanceof GuiDisconnected) {
            // If the server is offline worldUnload is never triggered, but you can ask for the server in GuiDisconnected (this doesn't seem to work when a normal kick happens)
            ServerData tempServer = Minecraft.getMinecraft().getCurrentServerData();
            if (tempServer != null)
                server = tempServer;

            if ((AutoLog.allowAutoReconnect || !NoReconnectOnAutoLog.getValue()) && server != null) {
                inAutoReconnectScreen = true;
                ticks = 1;
                seconds = time.getValue();
                gui = event.getGui();

                List<GuiButton> buttons = gui.buttonList;
                if (buttons.size() > 0)
                    buttons.get(0).displayString = "Reconnecting in " + String.valueOf(seconds);
            }
        } else {
            inAutoReconnectScreen = false;
        }
    }
}
