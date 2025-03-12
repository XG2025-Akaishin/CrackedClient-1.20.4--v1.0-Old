package me.alpha432.oyvey.mixin.fullbright;

import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

import me.alpha432.oyvey.features.modules.render.fullbright.Fullbright.Mode;
import me.alpha432.oyvey.features.modules.render.fullbright.Fullbright;


@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {
    @Shadow
    public abstract void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix);

    @ModifyVariable(method = "getLightmapCoordinates(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)I", at = @At(value = "STORE"), ordinal = 0)
    private static int getLightmapCoordinatesModifySkyLight(int sky) {
        if (Fullbright.getInstance().isEnabled() && Fullbright.getInstance().mode.getValue() == Mode.Gamma)
            return (Fullbright.getInstance().brightness.getValue());
        return sky;
    }
}
