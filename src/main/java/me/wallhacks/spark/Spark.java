package me.wallhacks.spark;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.wallhacks.spark.manager.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import me.wallhacks.spark.gui.clickGui.ClickGuiMenuBase;
import me.wallhacks.spark.systems.command.CommandHandler;
import me.wallhacks.spark.util.MC;

@Mod(modid = Spark.MODID, name = Spark.NAME, version = Spark.VERSION)
public class Spark implements MC {
    public static final String MODID = "sprk";
    public static final String NAME = "Spark";
    public static final String VERSION = "1.0";
    public static final Logger logger = LogManager.getLogger(MODID);

    public static EventBus eventBus;

    public static SystemManager systemManager;
    public static ConfigManager configManager;
    public static RotationManager rotationManager;
    public static FadeManager fadeManager;
    public static KeyManager keyManager;
    public static FontManager fontManager;
    public static CommandManager commandManager;
    public static PopManager popManager;
    public static ClickGuiMenuBase clickGuiScreen;
    public static AltManager altManager;
    public static ThreadManager threadManager;

    public static SocialManager socialManager;

    public static boolean runInShitWay = true;

    public static Spark instance;


    public Spark() {
    	if(!isModLoadedTwice()) {
	        Display.setTitle(NAME+" | v" + VERSION);
            logger.info("Loading spark client...");
	        eventBus = MinecraftForge.EVENT_BUS;

	        eventBus.register(new CommandHandler());

	        configManager = new ConfigManager();
	        keyManager = new KeyManager();
	        rotationManager = new RotationManager();
	        fontManager = new FontManager();
	        systemManager = new SystemManager();
            popManager = new PopManager();
	        commandManager = new CommandManager();
            fadeManager = new FadeManager();
	        clickGuiScreen = new ClickGuiMenuBase();
            threadManager = new ThreadManager();
            altManager = new AltManager();
            socialManager = new SocialManager();
	        configManager.Load();
            logger.info("Spark client loaded successfully");
    	}


    }

    //I don't know why... I don't know how... I don't want to know why... but unless i do this check the mod get loaded twice on eclipse...
    public boolean isModLoadedTwice() {
    	for(ModContainer mod : Loader.instance().getActiveModList()) {
    		if(mod.getMod() instanceof Spark) {
    			return true;
    		}
    	}
    	return false;
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        if(!runInShitWay)
            if(instance == null)
                instance = new Spark();

    }

	public static void sendInfo(String msg) {
		mc.player.sendMessage(new TextComponentString(ChatFormatting.DARK_PURPLE+"[Spark.Sex]"+ChatFormatting.GRAY+" " + msg));
	}
}
