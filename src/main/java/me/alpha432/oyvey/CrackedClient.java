package me.alpha432.oyvey;

import me.alpha432.oyvey.features.account.altmanager.AltManager;
import me.alpha432.oyvey.features.modules.client.RPC;
import me.alpha432.oyvey.features.modules.render.particles.utils.TextureUtility;
import me.alpha432.oyvey.features.notifications.notificationd.NotificationManager;
import me.alpha432.oyvey.manager.*;
import me.alpha432.oyvey.manager.futuremanager.ColorFutureClient;
import me.alpha432.oyvey.manager.shader.ShaderManager;
import me.alpha432.oyvey.manager.tntmanager.PlayerManager;
import me.alpha432.oyvey.manager.tntmanager.TntCManager;
import me.alpha432.oyvey.util.MeteorExecutor;
import me.alpha432.oyvey.util.soundutil.SoundUtill;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrackedClient implements ModInitializer, ClientModInitializer {
    public static final String NAME = "Cracked";
    public static final String VERSION = "2.0 - 1.20.4";
    public static float TIMER = 1f;
    //time cout
    public static long initTime;
    //public static final IEventBus EVENT_BUS = (IEventBus) new EventBus();
    public static final Logger LOGGER = LogManager.getLogger("Cracked");
    public static ServerManager serverManager;
    public static ColorManager colorManager;
    public static RotationManager rotationManager;
    public static PositionManager positionManager;
    public static HoleManager holeManager;
    public static EventManager eventManager;
    public static SpeedManager speedManager;
    public static CommandManager commandManager;
    public static FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static ConfigManager configManager;
    //TNT
    public static PlayerManager playerManager;
    public static TntCManager tntCManager;

    //Totem AutoCrystal
    public static AsyncManager asyncManager;
    public static CombatManager combatManager;
    public static HudManager hudManager;
    //Imagen
    //DesktopNotifier
    //Shaders
    public static ShaderManager shaderManager;
    //Notifier
    public static NotificationManager notificationManager;
    //ColorFuture
    public static ColorFutureClient colorFutureClient;
    //AltManager
    public static AltManager altManager;
    MinecraftClient mc = MinecraftClient.getInstance();

    //totempop
    public static PopManager popManager;

    @Override 
    public void onInitialize() {
        initTime = System.currentTimeMillis();
        colorFutureClient = new ColorFutureClient();
        notificationManager = new NotificationManager();
        shaderManager = new ShaderManager();
        combatManager = new CombatManager();
        asyncManager = new AsyncManager();
        hudManager = new HudManager();
        //StorageRender
        tntCManager = new TntCManager();
        playerManager = new PlayerManager();
        eventManager = new EventManager();
        serverManager = new ServerManager();
        rotationManager = new RotationManager();
        positionManager = new PositionManager();
        friendManager = new FriendManager();
        colorManager = new ColorManager();
        commandManager = new CommandManager();
        moduleManager = new ModuleManager();
        speedManager = new SpeedManager();
        holeManager = new HoleManager();
        popManager = new PopManager();
        //AltManager
        altManager = new AltManager(mc);
        //Particles Shader
        TextureUtility.initShaders();
        //Discord RPC
        if (isOnWindows())
            RPC.getInstance().startRpc();
    }

    public static boolean isOnWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }

    @Override 
    public void onInitializeClient() {
        eventManager.init();
        moduleManager.init();
        configManager = new ConfigManager();
        configManager.load();
        colorManager.init();
        colorFutureClient = new ColorFutureClient();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> configManager.save()));//ConfigManager
        MeteorExecutor.init();
    }

	public static void endClient() {
		try {
			altManager.saveAlts();
			moduleManager.modules.forEach(s -> s.onDisable());
            configManager.save();
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("[Cracked] Shutting down...");
	}
}
