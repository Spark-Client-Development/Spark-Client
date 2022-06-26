package me.wallhacks.spark;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.gui.ClickGui;
import me.wallhacks.spark.manager.*;
import me.wallhacks.spark.systems.module.modules.player.InventoryManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import me.wallhacks.spark.systems.command.CommandHandler;
import me.wallhacks.spark.util.MC;

import java.io.File;

@Mod(modid = Spark.MODID, name = Spark.NAME, version = Spark.VERSION)
public class Spark implements MC {
    public static final String MODID = "sprk";
    public static final String NAME = "Spark";
    public static final String VERSION = "1.1";
    public static final Logger logger = LogManager.getLogger(MODID);

    public static File ParentPath;

    public static EventBus eventBus;

    public static RPCManager rpcManager;
    public static SystemManager systemManager;
    public static ConfigManager configManager;
    public static AutoConfigManager autoConfigManager;
    public static RotationManager rotationManager;
    //do not check this class nothing to see there totally no skidding involved
    public static TickManager tickManager;
    public static FadeManager fadeManager;
    public static KeyManager keyManager;
    public static FontManager fontManager;
    public static CapeManager capeManager;
    public static CommandManager commandManager;

    public static CombatManager popManager;
    public static ClickGui clickGuiScreen;
    public static PositionManager positionManager;
    public static WaypointManager waypointManager;
    public static BreakManager breakManager;
    public static ThreadManager threadManager;
    public static SwitchManager switchManager;
    public static MapManager mapManager;
    public static PotionManager potionManager;
    public static ItemTrackerManager dataTrackingManager;
    public static SeedManager seedManager;

    public static SocialManager socialManager;

    @Mod.Instance
    public static Spark instance;


    @EventHandler
    public void init(FMLInitializationEvent event) {
        ParentPath = new File(Minecraft.getMinecraft().gameDir.getParent(), Spark.MODID);
        Display.setTitle(NAME + " | v" + VERSION);
        logger.info("Loading spark client...");
        eventBus = MinecraftForge.EVENT_BUS;
        eventBus.register(new CommandHandler());
        configManager = new ConfigManager();
        keyManager = new KeyManager();
        rotationManager = new RotationManager();
        fontManager = new FontManager();
        systemManager = new SystemManager();
        tickManager = new TickManager();
        popManager = new CombatManager();
        commandManager = new CommandManager();
        potionManager = new PotionManager();
        mapManager = new MapManager();
        seedManager = new SeedManager();
        breakManager = new BreakManager();
        fadeManager = new FadeManager();
        clickGuiScreen = new ClickGui();
        threadManager = new ThreadManager();
        switchManager = new SwitchManager();
        capeManager = new CapeManager();
        socialManager = new SocialManager();
        dataTrackingManager = new ItemTrackerManager();
        waypointManager = new WaypointManager();
        configManager.Load();
        rpcManager = new RPCManager();
        positionManager = new PositionManager();
        autoConfigManager = new AutoConfigManager();
        logger.info("Spark client loaded successfully");
    }

    public static void save() {
        Spark.configManager.Save();
        Spark.socialManager.SaveFriends();
        InventoryManager.instance.SaveKits();
        Spark.waypointManager.Save();
        Spark.seedManager.SAVESEEDS();
    }


    public static void sendInfo(String msg) {
        sendInfo(new TextComponentString(ChatFormatting.GRAY + msg));
    }

    public static void sendInfo(TextComponentString msg) {
        mc.player.sendMessage(new TextComponentString(ChatFormatting.DARK_PURPLE + "[Spark] ").appendSibling(msg));
    }
    
    public static void sendError(String msg) {
    	sendInfo(CommandManager.ErrorColor + msg);
    }
}
