package me.alpha432.oyvey.features.modules.misc.music;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.soundutil.SoundUtill;
import net.minecraft.util.Identifier;

public class MusicSound 
            extends Module {

    private final Setting<ModeAudio> modeds  = this.register(new Setting<ModeAudio>("ModeAudio", ModeAudio.Music1));
    private final Setting<Integer> volumen  = this.register(new Setting<Integer>("Range", 50, 0, 100));

    public MusicSound() {
        super("MusicSound", "Best MusicSound", Module.Category.MISC, true, false ,false);
    }

    @Override
    public void onEnable() {
    }

    public boolean ModeVer(ModeAudio m1, ModeAudio m2) {
        return m1 == m2;
    }

    @Override
    public String getDisplayInfo() {
        return modeds.getValue().name();
    }

    @Override
    public void onDisable() {

    }

    public enum ModeAudio {
        Music1,
        Music2,
        Music3,
        Music4,
        Music5,
        Music6,
        Music7,
        Music8,
        Music9,
        Music10
    }

}