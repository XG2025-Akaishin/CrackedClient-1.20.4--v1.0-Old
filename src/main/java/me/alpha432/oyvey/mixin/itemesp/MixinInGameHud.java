package me.alpha432.oyvey.mixin.itemesp;

import me.alpha432.oyvey.CrackedClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {

    @Inject(at = @At(value = "HEAD"), method = "render")
    public void render(DrawContext context, float tickDelta, CallbackInfo ci) {
            CrackedClient.moduleManager.onPreRender2D(context);
        }
}
