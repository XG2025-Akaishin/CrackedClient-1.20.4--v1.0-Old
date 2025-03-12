package me.alpha432.oyvey.mixin.shader;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.alpha432.oyvey.features.modules.render.shaders.Shaders;

@Mixin(InGameOverlayRenderer.class)
public class MixinInGameOverlayRenderer {

    @Inject(method = "renderUnderwaterOverlay", at = @At("HEAD"), cancellable = true)
    private static void renderUnderwaterOverlayHook(MinecraftClient minecraftClient, MatrixStack matrixStack, CallbackInfo ci) {
        if (Shaders.getInstance().isEnabled())
            ci.cancel();
    }

    @Inject(method = "renderInWallOverlay", at = @At("HEAD"), cancellable = true)
    private static void renderInWallOverlayHook(Sprite sprite, MatrixStack matrices, CallbackInfo ci) {
        if (Shaders.getInstance().isEnabled())
            ci.cancel();
    }
}