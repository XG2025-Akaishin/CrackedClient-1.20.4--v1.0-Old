package me.alpha432.oyvey.mixin.norender;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.alpha432.oyvey.features.modules.render.norender.NoRender;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"))
    private float applyCameraTransformationsMathHelperLerpProxy(float delta, float first, float second) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().nausea.getValue()) return 0;
        return MathHelper.lerp(delta, first, second);
    }

    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void tiltViewWhenHurtHook(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().hurtCam.getValue()) {
            ci.cancel();
        }
    }

    @Inject(method = "showFloatingItem", at = @At("HEAD"), cancellable = true)
    private void onShowFloatingItem(ItemStack floatingItem, CallbackInfo ci) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().totem.getValue()) {
            ci.cancel();
        }
    }
}
