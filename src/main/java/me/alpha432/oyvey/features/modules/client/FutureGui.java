package me.alpha432.oyvey.features.modules.client;

import com.google.common.eventbus.Subscribe;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.event.impl.ClientEvent;
import me.alpha432.oyvey.features.futuregui.FutureGuiOpen;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.gui.screen.Screen;

public class FutureGui extends Module {
    public static FutureGui INSTANCE;
           public Setting<Boolean> colorSync = this.register(new Setting("Sync", false));
           public Setting<Integer> red = this.register(new Setting("Red", 79, 0, 255));
           public Setting<Integer> green = this.register(new Setting("Green", 42, 0, 255));
           public Setting<Integer> blue = this.register(new Setting("Blue", 205, 0, 255));
           public Setting<Integer> alpha = this.register(new Setting("Alpha", 159, 0, 255));
           public Setting<Integer> hoverAlpha = this.register(new Setting("HoverAlpha", 151, 0, 255));
           public Setting<Boolean> future = (Setting<Boolean>)this.register(new Setting("Future", true, "like future"));
           public Setting<Boolean> backgroundScreen = (Setting<Boolean>)this.register(new Setting("BackgroundScreen", true));
           public Setting<Boolean> logoWaifu = (Setting<Boolean>)this.register(new Setting("Waifu", true));
           public Setting<Float> xWaifu = (Setting<Float>)this.register(new Setting("X", 728.0f, 0.0f, 1000.0f, v -> this.logoWaifu.getValue()));
           public Setting<Float> yWaifu = (Setting<Float>)this.register(new Setting("Y", 255.0f, 0.0f, 1000.0f, v -> this.logoWaifu.getValue()));
           public Setting<Integer> heightWaifu = (Setting<Integer>)this.register(new Setting("Height",267, 0, 1000, v -> this.logoWaifu.getValue()));
           public Setting<Integer> widthWaifu = (Setting<Integer>)this.register(new Setting("Width", 207, 0, 1000, v -> this.logoWaifu.getValue()));
    private FutureGuiOpen clickguiopen;
    public FutureGui() {
        super("FutureGui", "Opens the ClickGui", Category.CLIENT, true, false, false);
         this.setInstance();
    }
    
    public static FutureGui getInstance() {
        if (FutureGui.INSTANCE == null) {
            FutureGui.INSTANCE = new FutureGui();
        }
        return FutureGui.INSTANCE;
    }
    
    private void setInstance() {
        FutureGui.INSTANCE = this;
    }
    
    @Override
    public void onUpdate() {
        /*if (this.customFov.getValue()) {
            FutureGui.mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, (float)this.fov.getValue());
        }*/
    }
    
    //@SubscribeEvent
    @Subscribe
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
            CrackedClient.colorFutureClient.setColor(this.red.getPlannedValue(), this.green.getPlannedValue(), this.blue.getPlannedValue(), this.hoverAlpha.getPlannedValue());

    }
}
    
    @Override
    public void onEnable() {
        FutureGui.mc.setScreen((Screen)FutureGuiOpen.getFutureGuiPro());
       /*  if (this.blurEffect.getValue()) {
            FutureGui.mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
        }*/
    }
    
    @Override
    public void onLoad() {
        if (this.colorSync.getValue()) {
            CrackedClient.colorFutureClient.setColor(Colors.INSTANCE.getCurrentColor().getRed(), Colors.INSTANCE.getCurrentColor().getGreen(), Colors.INSTANCE.getCurrentColor().getBlue(), this.hoverAlpha.getValue());
        }
        else {
            CrackedClient.colorFutureClient.setColor(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.hoverAlpha.getValue());
        }
        //Cracked.commandManager.setPrefix(this.prefix.getValue());
    }
    
    @Override
    public void onTick() {
        if (!(FutureGui.mc.currentScreen instanceof FutureGuiOpen)) {
            this.disable();
            //if (FutureGui.mc.entityRenderer.getShaderGroup() != null) {
            //    FutureGui.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
            //}
        }
    }
    
    @Override
    public void onDisable() {
        if (FutureGui.mc.currentScreen instanceof FutureGuiOpen) {
            FutureGui.mc.setScreen((Screen)null);
        }
    }
    
    static {
        FutureGui.INSTANCE = new FutureGui();
    }
}
