package me.alpha432.oyvey.features.modules.misc.fakegamemode;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.world.GameMode;


public class GameModeFake extends Module {

    public final Setting<GameMode> gamemode = this.register(new Setting<GameMode>("GameMode", GameMode.CREATIVE, "GameMode Creative Opped."));

    public GameModeFake() {
        super("GameMode", "GameModeFake GooD", Module.Category.MISC, true, false ,false);
    }

    private GameMode oldGamemode = GameMode.SURVIVAL;

    @Override
    public void onEnable() {
        oldGamemode = mc.interactionManager.getCurrentGameMode();
        if (gamemode.getValue() == GameMode.SURVIVAL) {
            mc.interactionManager.setGameMode(GameMode.SURVIVAL);
        } else if (gamemode.getValue() == GameMode.CREATIVE) {
            mc.interactionManager.setGameMode(GameMode.CREATIVE);
        } else if (gamemode.getValue() == GameMode.ADVENTURE) {
            mc.interactionManager.setGameMode(GameMode.ADVENTURE);
        } else if (gamemode.getValue() == GameMode.SPECTATOR) {
            mc.interactionManager.setGameMode(GameMode.SPECTATOR);
        }
    }

    @Override
    public void onDisable() {
        mc.interactionManager.setGameMode(oldGamemode);
    }
}
