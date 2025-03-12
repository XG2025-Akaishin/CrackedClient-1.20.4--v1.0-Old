package me.alpha432.oyvey.mixin.fullbright;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.alpha432.oyvey.features.modules.render.fullbright.Fullbright;
import me.alpha432.oyvey.features.modules.render.fullbright.Fullbright.Mode;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends Entity> {

    @Inject(method = "getSkyLight", at = @At("RETURN"), cancellable = true)
    private void onGetSkyLight(CallbackInfoReturnable<Integer> cir) {
        if(Fullbright.getInstance().isEnabled() && Fullbright.getInstance().mode.getValue() == Mode.Gamma)
            cir.setReturnValue(Fullbright.getInstance().brightness.getValue());
    }
}