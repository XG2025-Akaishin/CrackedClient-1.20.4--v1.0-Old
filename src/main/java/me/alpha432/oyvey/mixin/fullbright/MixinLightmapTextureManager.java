package me.alpha432.oyvey.mixin.fullbright;

import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import me.alpha432.oyvey.features.modules.render.fullbright.Fullbright;
import me.alpha432.oyvey.features.modules.render.fullbright.Fullbright.Mode;

@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager {
    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;setColor(III)V"))
    private void update(Args args) {
        if (Fullbright.getInstance().isEnabled() && Fullbright.getInstance().mode.getValue() == Mode.Gamma) {
            args.set(2, 0xFFFFFFFF);
        }
    }
}