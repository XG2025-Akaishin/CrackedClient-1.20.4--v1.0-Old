package me.alpha432.oyvey.mixin.hud;

import org.spongepowered.asm.mixin.Shadow;
import me.alpha432.oyvey.features.modules.client.hud.utils.FrameRateCounter;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    @Shadow
    public abstract void render(float tickDelta, long startTime, boolean tick);

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V", shift = At.Shift.BEFORE), method = "render")
    void postHudRenderHook(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        FrameRateCounter.INSTANCE.recordFrame();
    }
}
