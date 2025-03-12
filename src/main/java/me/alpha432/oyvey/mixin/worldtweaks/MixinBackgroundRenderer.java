package me.alpha432.oyvey.mixin.worldtweaks;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;

import java.awt.Color;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.alpha432.oyvey.features.modules.render.worldtweaks.WorldTweaks;
import me.alpha432.oyvey.features.modules.render.worldtweaks.colorutil.ColorUtils;

@Mixin(BackgroundRenderer.class)
public class MixinBackgroundRenderer {

    @Inject(method = "applyFog", at = @At("TAIL"), cancellable = true)
    private static void onApplyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo info) {

        if (WorldTweaks.getInstance().isEnabled()/*  && NoRender.fog.getValue()*/) {
            if (fogType == BackgroundRenderer.FogType.FOG_TERRAIN) {
                RenderSystem.setShaderFogStart(viewDistance * 4);
                RenderSystem.setShaderFogEnd(viewDistance * 4.25f);
            }
        }
        //Color color = new Color(WorldTweaks.getInstance().red.getValue(), WorldTweaks.getInstance().green.getValue(), WorldTweaks.getInstance().blue.getValue(), WorldTweaks.getInstance().alpha.getValue());
                //Alpha
        float alpha = WorldTweaks.getInstance().alpha.getValue();
        float alphas = (ColorUtils.getGlAlpha() + alpha) / 255f;//No use lol
        //RED
        float red = WorldTweaks.getInstance().red.getValue();
        float reds = (ColorUtils.getGlRed() + red ) / 255f;
        //BLUE
        float blue = WorldTweaks.getInstance().blue.getValue();
        float blues = (ColorUtils.getGlBlue() + blue) / 255f;
        //GREEN
        float green = WorldTweaks.getInstance().green.getValue();
        float greens = (ColorUtils.getGlGreen() + green) / 255f;


        if(WorldTweaks.getInstance().isEnabled() && WorldTweaks.getInstance().fogModify.getValue()/* .isEnabled()*/) {
            RenderSystem.setShaderFogStart(WorldTweaks.getInstance().fogStart.getValue());
            RenderSystem.setShaderFogEnd(WorldTweaks.getInstance().fogEnd.getValue());
            RenderSystem.setShaderFogColor(reds, greens, blues);
        }
    }
    @Inject(method = "getFogModifier(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/client/render/BackgroundRenderer$StatusEffectFogModifier;", at = @At("HEAD"), cancellable = true)
    private static void onGetFogModifier(Entity entity, float tickDelta, CallbackInfoReturnable<Object> info) {
        /*if (ModuleManager.noRender.isEnabled() && NoRender.blindness.getValue())*/ info.setReturnValue(null);
    }


}