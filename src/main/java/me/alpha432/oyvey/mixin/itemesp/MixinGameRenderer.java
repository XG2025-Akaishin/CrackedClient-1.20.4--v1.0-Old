package me.alpha432.oyvey.mixin.itemesp;

import com.mojang.blaze3d.systems.RenderSystem;
import me.alpha432.oyvey.features.modules.render.itemesp.utils.RenderUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    @Shadow
    public abstract void render(float tickDelta, long startTime, boolean tick);

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z", opcode = Opcodes.GETFIELD, ordinal = 0), method = "renderWorld")
    void render3dHook(float tickDelta, long limitTime, @NotNull MatrixStack matrix, CallbackInfo ci) {
        RenderUtils.lastProjMat.set(RenderSystem.getProjectionMatrix());
        RenderUtils.lastModMat.set(RenderSystem.getModelViewMatrix());
        RenderUtils.lastWorldSpaceMatrix.set(matrix.peek().getPositionMatrix());
    }

}
