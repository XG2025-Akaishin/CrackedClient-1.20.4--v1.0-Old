package me.alpha432.oyvey.mixin.holesp;

import me.alpha432.oyvey.features.modules.render.holesp.utilshole.Render3DEngine;
import me.alpha432.oyvey.features.modules.render.shaders.Shaders;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.features.modules.render.holesp.utilshole.MSAAFramebuffer;
import me.alpha432.oyvey.features.modules.render.storagesp.utils.UtilRenderESP;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    @Shadow
    public abstract void render(float tickDelta, long startTime, boolean tick);

    @Shadow
    private float zoom;

    @Shadow
    private float zoomX;

    @Shadow
    private float zoomY;

    @Shadow
    private float viewDistance;

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z", opcode = Opcodes.GETFIELD, ordinal = 0), method = "renderWorld")
    void render3dHook(float tickDelta, long limitTime, @NotNull MatrixStack matrix, CallbackInfo ci) {
        MSAAFramebuffer.use(false, () -> {
            //OyVey.moduleManager.onRender3D(matrix);
            //BlockAnimationUtility.onRender(matrix);
            Render3DEngine.onRender3D(matrix); // <- не двигать
            UtilRenderESP.onRender3D(matrix);

        });
    }
}
