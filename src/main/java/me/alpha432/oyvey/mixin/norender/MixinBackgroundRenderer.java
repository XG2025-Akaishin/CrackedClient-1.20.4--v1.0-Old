package me.alpha432.oyvey.mixin.norender;

import com.mojang.blaze3d.systems.RenderSystem;
import me.alpha432.oyvey.features.modules.render.norender.NoRender;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BackgroundRenderer.class)
public class MixinBackgroundRenderer {
    @Inject(method = "applyFog", at = @At("TAIL"))
    private static void onApplyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo info) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().fog.getValue()) {
            if (fogType == BackgroundRenderer.FogType.FOG_TERRAIN) {
                RenderSystem.setShaderFogStart(viewDistance * 4);
                RenderSystem.setShaderFogEnd(viewDistance * 4.25f);
            }
        }
    }

    @Inject(method = "getFogModifier(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/client/render/BackgroundRenderer$StatusEffectFogModifier;", at = @At("HEAD"), cancellable = true)
    private static void onGetFogModifier(Entity entity, float tickDelta, CallbackInfoReturnable<Object> info) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().blindness.getValue()) info.setReturnValue(null);
    }
}
