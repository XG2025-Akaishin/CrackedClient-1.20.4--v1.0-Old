package me.alpha432.oyvey.features.modules.render.fullbright;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;


public class Fullbright extends Module {
    private static Fullbright INSTANCE = new Fullbright();
    public final Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.Gamma));
    public Setting<Integer> brightness = this.register(new Setting<>("Brightness", 15, 0, 15));

    public Fullbright() {
        super("Fullbright", "Fullbright good", Category.MISC, true, false, false);
        this.setInstance();
    }

    public static Fullbright getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Fullbright();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public enum Mode {
        NightVision, Gamma
    }

    //@Override
    public String getMetaData() {
        return String.valueOf(mode.getValue());
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.player != null) {
            if (mode.getValue() == Mode.NightVision) {
                mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 999999999, 5));
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.player != null) {
            if (mode.getValue() == Mode.NightVision) {
                mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
            }
        }
    }
}
