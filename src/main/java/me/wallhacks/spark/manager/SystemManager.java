package me.wallhacks.spark.manager;

import me.wallhacks.spark.Spark;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import me.wallhacks.spark.event.client.InputEvent;
import me.wallhacks.spark.event.player.PlayerUpdateEvent;
import me.wallhacks.spark.systems.SettingsHolder;
import me.wallhacks.spark.systems.clientsetting.ClientSetting;
import me.wallhacks.spark.systems.clientsetting.clientsettings.GuiSettings;
import me.wallhacks.spark.systems.command.commands.ModuleCommand;
import me.wallhacks.spark.systems.hud.HudElement;
import me.wallhacks.spark.systems.module.Module;
import me.wallhacks.spark.util.ReflectionUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public class SystemManager {
    private static final LinkedHashMap<Class<? extends Module>,Module> modules = new LinkedHashMap<>();
    private static final LinkedHashMap<Class<? extends ClientSetting>,ClientSetting> clientSettings = new LinkedHashMap<>();
    private static final LinkedHashMap<Class<? extends HudElement>, HudElement> hudModules = new LinkedHashMap<>();


    public SystemManager() {

        Spark.eventBus.register(this);

        //load mods
        try {
            for (Module.Category category: Module.Category.values()) {
                ArrayList<Class<?>> modClasses = ReflectionUtil.getClassesForPackage("me.wallhacks.spark.systems.module.modules."+category.getName().toLowerCase());
                modClasses.spliterator().forEachRemaining(aClass -> {
                    if(Module.class.isAssignableFrom(aClass)) {
                        try {
                            Module module = (Module) aClass.getConstructor().newInstance();
                            module.setCategory(category);
                            modules.put(module.getClass(),module);
                            new ModuleCommand(module);
                            if(module.getIsAlwaysListening() || module.isEnabled())
                                Spark.eventBus.register(module);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //load client settings
        try {
            ArrayList<Class<?>> modClasses = ReflectionUtil.getClassesForPackage("me.wallhacks.spark.systems.clientsetting.clientsettings");
            modClasses.spliterator().forEachRemaining(aClass -> {
                if(ClientSetting.class.isAssignableFrom(aClass)) {
                    try {
                        ClientSetting module = (ClientSetting) aClass.getConstructor().newInstance();
                        clientSettings.put(module.getClass(),module);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //load huds
        try {
            ArrayList<Class<?>> modClasses = ReflectionUtil.getClassesForPackage("me.wallhacks.spark.systems.hud.huds");
            modClasses.spliterator().forEachRemaining(aClass -> {
                if(HudElement.class.isAssignableFrom(aClass)) {
                    try {
                        HudElement module = (HudElement) aClass.getConstructor().newInstance();
                        hudModules.put(module.getClass(),module);

                        if(module.isEnabled())
                            Spark.eventBus.register(module);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Spark.logger.info("Loaded mods: "+ modules.size());
        Spark.logger.info("Loaded client settings: "+ clientSettings.size());
    }

    public static List<SettingsHolder> getSystems() {
        ArrayList<SettingsHolder> l = new ArrayList<SettingsHolder>();
        l.addAll(getClientSettings());
        l.addAll(getModules());
        l.addAll(getHudModules());
        return l;
    }

    public static Collection<HudElement> getHudModules() {
        return hudModules.values();
    }
    public static Collection<Module> getModules() {
        return modules.values();
    }
    public static Collection<ClientSetting> getClientSettings() {
        return clientSettings.values();
    }

    @SuppressWarnings("unchecked")
	public static <T extends Module> T getModule(Class<T> clazz) {
        return (T) modules.get(clazz);
    }
    @SuppressWarnings("unchecked")
	public static <T extends ClientSetting> T getClientSetting(Class<T> clazz) {
        return (T) clientSettings.get(clazz);
    }



    @SubscribeEvent
    public void onUpdateEvent(PlayerUpdateEvent event) {
        if(Minecraft.getMinecraft().currentScreen == null)
        for (Module module : getModules()) {
            if (module.isHold()) {
                if (module.getBind() >= 0) {
                    if (Keyboard.isKeyDown(module.getBind())) {
                        if (!module.isEnabled()) {
                            module.enable();
                        }
                    } else {
                        if (module.isEnabled()) {
                            module.disable();
                        }
                    }
                } else {
                    // a bit cleaner than wallhacks code
                    // only a bit though
                    for (int i = 0; i < 6; i++)
                        if (module.getBind() == -(2+i)) handleHold(Mouse.isButtonDown(i), module);

                }
            }
        }
    }

    @SubscribeEvent
    public void onInputEvent(InputEvent event) {
        for (Module module : getModules()) {
            if (!module.isHold() && module.getBind() == event.getKey()) {
                module.setEnabled(!module.isEnabled());
            }
        }
        if(event.getKey() == getClientSetting(GuiSettings.class).getBind())
            Minecraft.getMinecraft().displayGuiScreen(Spark.clickGuiScreen);
    }


    private void handleHold(boolean down, Module module) {
        module.setEnabled(down);
    }
}
