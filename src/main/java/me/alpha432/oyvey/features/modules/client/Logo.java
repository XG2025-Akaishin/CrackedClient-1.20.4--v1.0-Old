package me.alpha432.oyvey.features.modules.client;

import net.minecraft.util.*;
import me.alpha432.oyvey.event.impl.Render2DEvent;
import me.alpha432.oyvey.features.*;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.*;
import net.minecraft.client.gui.*;

public class Logo extends Module {

    private final Identifier logoresource = new Identifier("textures/logo-64.png");
    public Setting<Integer> imageX = this.register(new Setting("logoX", 0, (-700), 1200));
    public Setting<Integer> imageY = this.register(new Setting("logoY", 0, (-700), 1200));//Test negative
    public Setting<Integer> imageWidth = this.register(new Setting("logoWidth", 97, 0, 1000));
    public Setting<Integer> imageHeight = this.register(new Setting("logoHeight", 97, 0, 1000));
    
    public Logo() {
        super("Logo", "Puts a logo there (there)", Category.CLIENT, false, false, false);
    }
    
    public void renderLogo(DrawContext context){
        float hx = mc.getWindow().getScaledWidth() / 2f;
        float hy = mc.getWindow().getScaledHeight() / 2f;
        context.drawTexture(logoresource, (int) hx - imageX.getValue(), (int) hy + imageY.getValue(), 0, 0, imageWidth.getValue(), imageHeight.getValue(), imageWidth.getValue(), imageHeight.getValue());
    }
    
    //@Override
    public void onRender2D(final Render2DEvent event) {
        if (!Feature.fullNullCheck()) {
                this.renderLogo(event.getContext());
        }
    }
    
}
