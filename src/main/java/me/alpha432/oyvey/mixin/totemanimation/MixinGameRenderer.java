package me.alpha432.oyvey.mixin.totemanimation;

import me.alpha432.oyvey.features.modules.render.totemanimation.TotemAnimation;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    /*
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
    private float viewDistance;*/

    @Inject(method = "showFloatingItem", at = @At("HEAD"), cancellable = true)
    private void showFloatingItemHook(ItemStack floatingItem, CallbackInfo info) {
        if (TotemAnimation.getInstance().isOn()) {
            TotemAnimation.getInstance().showFloatingItem(floatingItem);
            info.cancel();
        }
    }
    @Inject(method = "renderFloatingItem", at = @At("HEAD"), cancellable = true)
    private void renderFloatingItemHook(int scaledWidth, int scaledHeight, float tickDelta, CallbackInfo ci) {
        if (TotemAnimation.getInstance().isOn()) {
            TotemAnimation.getInstance().renderFloatingItem(scaledWidth, scaledHeight, tickDelta);
            ci.cancel();
        }
    }
}