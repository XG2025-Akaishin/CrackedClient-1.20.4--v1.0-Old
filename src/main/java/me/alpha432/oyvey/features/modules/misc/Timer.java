package me.alpha432.oyvey.features.modules.misc;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.Cape.CapeMode;
import me.alpha432.oyvey.features.settings.Setting;

public class Timer extends Module {
    private static Timer INSTANCE = new Timer();
    public final Setting<Float> multiplier = this.register(new Setting<>("Frame", 1f, 0.1f, 5f, "wow."));

    public Timer() {
        super("FameCustom", "FameCustom", Module.Category.MISC, true, false, false);
        this.setInstance();
    }

    public static Timer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Timer();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

	public float getMultiplier() {
		return this.multiplier.getValue();
	}

}
