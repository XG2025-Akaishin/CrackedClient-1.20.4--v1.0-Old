package me.alpha432.oyvey.features.modules.misc.fakevanilla;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;

public class FakeVanilla extends Module {

    private static FakeVanilla INSTANCE = new FakeVanilla();

    public FakeVanilla() {
        super("FakeVanilla", "Fake Vanilla", Category.MISC, true, false, false);
        this.setInstance();
    }

    public static FakeVanilla getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakeVanilla();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    private final Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.Vanilla));
    private final Setting<String> custom = this.register(new Setting<>("Client", "feather", v-> mode.getValue() == Mode.Custom));

    public enum Mode {
        Vanilla, Cracked, Future, Custom, Null
    }

    @Override
    public String getDisplayInfo() {
        return mode.getValue().name();
    }

    public String getClientName() {
        switch (mode.getValue()) {
            case Vanilla -> {
                return "vanilla";
            }
            case Cracked -> {
                return "Cracked:1.20.6";
            }
            case Future -> {
                return "Future:1.20.4";
            }
            case Custom -> {
                return (String) custom.getValue();
            }
            default ->
            {
                return null;
            }
        }
    }
}