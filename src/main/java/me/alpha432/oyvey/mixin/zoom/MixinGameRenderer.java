package me.alpha432.oyvey.mixin.zoom;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.alpha432.oyvey.features.modules.render.ZoomPro;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Shadow
    @Final
    MinecraftClient client;
    @Shadow
    private float zoom;
    @Shadow
    private float zoomX;
    @Shadow
    private float zoomY;
    @Shadow
    private float viewDistance;

    @Inject(method = "getFov(Lnet/minecraft/client/render/Camera;FZ)D", at = @At("TAIL"), cancellable = true)
    public void getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cb) {
        if (cb.getReturnValue() == 70) return;
        if (ZoomPro.getInstance().on) {
            cb.setReturnValue(Math.min(Math.max(cb.getReturnValue() - ZoomPro.getInstance().currentFov, 1), 177));
        }
    }

}
