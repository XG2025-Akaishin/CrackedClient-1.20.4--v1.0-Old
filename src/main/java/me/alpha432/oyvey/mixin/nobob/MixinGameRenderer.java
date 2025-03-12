package me.alpha432.oyvey.mixin.nobob;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.alpha432.oyvey.features.modules.render.nobob.NoBob;
import me.alpha432.oyvey.features.modules.Module;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Shadow
    @Final
    MinecraftClient client;

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void bobViewHook(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (Module.fullNullCheck()) return;
        if (NoBob.getInstance().isEnabled()) {
            NoBob.getInstance().bobView(matrices, tickDelta);
            ci.cancel();
            return;
        }
        if (NoBob.getInstance().customBob.getValue()) {
            Module.bobView(matrices, tickDelta);
            ci.cancel();
        }
    }
}