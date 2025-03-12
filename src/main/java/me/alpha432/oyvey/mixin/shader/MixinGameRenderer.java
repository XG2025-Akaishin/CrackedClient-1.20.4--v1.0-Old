package me.alpha432.oyvey.mixin.shader;

import me.alpha432.oyvey.CrackedClient;

import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    @Shadow
    public abstract void render(float tickDelta, long startTime, boolean tick);


    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderHand(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/Camera;F)V", shift = At.Shift.AFTER))
    public void postRender3dHook(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        CrackedClient.shaderManager.renderShaders();
    }
}
