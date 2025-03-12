package me.alpha432.oyvey.mixin.worldtweaks;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.alpha432.oyvey.features.modules.render.worldtweaks.WorldTweaks;
import me.alpha432.oyvey.features.modules.render.worldtweaks.colorutil.ColorUtils;

@Mixin(ClientWorld.class)
public class MixinClientWorld {
    @Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true)
    public void getSkyColorHook(Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        if (WorldTweaks.getInstance().isEnabled() && WorldTweaks.getInstance().fogModify.getValue()/*.isEnabled()*/) {

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

            //Color color = new Color(reds, greens, blues, alphas);
        //int color = ColorUtil.toARGB(WorldTweaks.getInstance().colorRed.getValue(), WorldTweaks.getInstance().colorGreen.getValue(), WorldTweaks.getInstance().colorBlue.getValue(), WorldTweaks.getInstance().colorAlpha.getValue());
            //ColorSetting c = WorldTweaks.getInstance().fogColor.getValue();
            //Float color = (float) ColorUtil.toARGB(WorldTweaks.getInstance().red.getValue() / ColorRenderUtil.getRed(),WorldTweaks.getInstance().green.getValue() + ColorRenderUtil.getGreen(),WorldTweaks.getInstance().blue.getValue() + ColorRenderUtil.getBlue());
            cir.setReturnValue(new Vec3d(reds, greens, blues));
        }
    }
}
