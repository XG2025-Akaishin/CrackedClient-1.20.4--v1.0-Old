package me.alpha432.oyvey.features.modules.client;

import com.google.common.eventbus.Subscribe;
import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.event.impl.ClientEvent;
import me.alpha432.oyvey.features.commands.Command;
import me.alpha432.oyvey.features.gui.OyVeyGui;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class ClickGui
        extends Module {
    private static ClickGui INSTANCE = new ClickGui();
    public Setting<String> prefix = this.register(new Setting<String>("Prefix", "."));
    //Color
    public Setting<Integer> red = this.register(new Setting<>("Red", 0, 0, 255));
    public Setting<Integer> green = this.register(new Setting<>("Green", 186, 0, 255));
    public Setting<Integer> blue = this.register(new Setting<>("Blue", 242, 0, 255));
    public Setting<Integer> alpha = this.register(new Setting<>("HoverAlpha", 247, 0, 255));
    public Setting<Integer> hoverAlpha = this.register(new Setting<>("Alpha", 212, 0, 255));
    //Future
    public Setting<Boolean> future = this.register(new Setting<>("Future", false));
    //BackGround 
    public Setting<Boolean> background = this.register(new Setting<>("BackGround", true));
    public Setting<Integer> backgroundRed = this.register(new Setting<>("Red", 0, 0, 255, v -> this.background.getValue()));
    public Setting<Integer> backgroundGreen = this.register(new Setting<>("Green", 0, 0, 255, v -> this.background.getValue()));
    public Setting<Integer> backgroundBlue = this.register(new Setting<>("Blue", 0, 0, 255, v -> this.background.getValue()));
    public Setting<Integer> backgroundAlpha = this.register(new Setting<>("BGAlpha", 142, 0, 255, v -> this.background.getValue()));

    //FrameBackground
    public Setting<Boolean> frameBackground = this.register(new Setting<>("FrameBackGround", true));
    public Setting<Integer> frameBackgroundRed = this.register(new Setting<>("Red", 0, 0, 255, v -> this.frameBackground.getValue()));
    public Setting<Integer> frameBackgroundGreen = this.register(new Setting<>("Green", 70, 0, 255, v -> this.frameBackground.getValue()));
    public Setting<Integer> frameBackgroundBlue = this.register(new Setting<>("Blue", 137, 0, 255, v -> this.frameBackground.getValue()));
    public Setting<Integer> frameBackgroundAlpha = this.register(new Setting<>("FBGAlpha", 43, 0, 255, v -> this.frameBackground.getValue()));

    //PlusColor
    public Setting<Boolean> plusColor = this.register(new Setting<>("PlusColor", true));
    public Setting<Integer> plusRed = this.register(new Setting<>("PlusRed", 255, 0, 255,v -> this.plusColor.getValue()));
    public Setting<Integer> plusGreen = this.register(new Setting<>("PlusGreen", 255, 0, 255,v -> this.plusColor.getValue()));
    public Setting<Integer> plusBlue = this.register(new Setting<>("PlusBlue", 255, 0, 255,v -> this.plusColor.getValue()));
    public Setting<Integer> plusAlpha = this.register(new Setting<>("PlusAlpha", 255, 0, 255,v -> this.plusColor.getValue()));

    //textures
    public Setting<Boolean> pic1 = this.register(new Setting<>("Image", true));
    public Setting<Integer> pic1X = this.register(new Setting<>("ImageX", 255, (-700), 1200,v -> this.pic1.getValue()));
    public Setting<Integer> pic1Y = this.register(new Setting<>("ImageY", 255, (-700), 1200,v -> this.pic1.getValue()));
    public Setting<Integer> pic1height = this.register(new Setting<>("ImageHeight", 255, 0, 1000,v -> this.pic1.getValue()));
    public Setting<Integer> pic1width = this.register(new Setting<>("ImageWidth", 255, 0, 1000,v -> this.pic1.getValue()));

    public Setting<Boolean> pic2 = this.register(new Setting<>("Image1", true));
    public Setting<Integer> pic2X = this.register(new Setting<>("Image1X", 255, (-700), 1200,v -> this.pic2.getValue()));
    public Setting<Integer> pic2Y = this.register(new Setting<>("Image1Y", 255, (-700), 1200,v -> this.pic2.getValue()));
    public Setting<Integer> pic2height = this.register(new Setting<>("Image1Height", 255, 0, 1000,v -> this.pic2.getValue()));
    public Setting<Integer> pic2width = this.register(new Setting<>("Image1Width", 255, 0, 1000,v -> this.pic2.getValue()));

    public Setting<Boolean> pic3 = this.register(new Setting<>("Image2", true));
    public Setting<Integer> pic3X = this.register(new Setting<>("Image2X", 255, (-700), 1200,v -> this.pic3.getValue()));
    public Setting<Integer> pic3Y = this.register(new Setting<>("Image2Y", 255, (-700), 1200,v -> this.pic3.getValue()));
    public Setting<Integer> pic3height = this.register(new Setting<>("Image2Height", 255, 0, 1000,v -> this.pic3.getValue()));
    public Setting<Integer> pic3width = this.register(new Setting<>("Image2Width", 255, 0, 1000,v -> this.pic3.getValue()));

    //Otherd
    public Setting<Boolean> Snowing = this.register(new Setting<>("Snowing", true));
    public  Setting<Boolean> scaleFactorFix = this.register(new Setting<>("ScaleFactorFix", false));
    public  Setting<Float> scaleFactorFixValue = this.register(new Setting<>("ScaleFactorFixValue", 2f, 0f, 4f));

    private OyVeyGui click;

    public ClickGui() {
        super("ClickGui", "Opens the ClickGui", Module.Category.CLIENT, true, false, false);
        setBind(GLFW.GLFW_KEY_RIGHT_SHIFT);
        this.setInstance();
    }

    public static ClickGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGui();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    //@Override
    public void onUpdate() {
        //remove
    }

    @Subscribe
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
            if (event.getSetting().equals(this.prefix)) {
                CrackedClient.commandManager.setPrefix(this.prefix.getPlannedValue());
                Command.sendMessage("Prefix set to " + Formatting.DARK_GRAY + CrackedClient.commandManager.getPrefix());
            }
            CrackedClient.colorManager.setColor(this.red.getPlannedValue(), this.green.getPlannedValue(), this.blue.getPlannedValue(), this.hoverAlpha.getPlannedValue());
        }
    }

    @Override
    public void onEnable() {
        ClickGui.mc.setScreen((Screen)OyVeyGui.getClickGui());
        //mc.setScreen(OyVeyGui.getClickGui());
    }

    @Override
    public void onLoad() {
        CrackedClient.colorManager.setColor(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.hoverAlpha.getValue());
        CrackedClient.commandManager.setPrefix(this.prefix.getValue());
    }

    
    @Override
    public void onTick() {
        if (!(ClickGui.mc.currentScreen instanceof OyVeyGui)) {
            this.disable();
        }
    }

    @Override
    public void onDisable() {
        if (ClickGui.mc.currentScreen instanceof OyVeyGui) {
            ClickGui.mc.setScreen((Screen)null);
        }
    }
}