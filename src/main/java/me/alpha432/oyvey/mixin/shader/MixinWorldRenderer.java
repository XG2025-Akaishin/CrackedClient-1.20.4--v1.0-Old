package me.alpha432.oyvey.mixin.shader;

import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.features.modules.render.shaders.Shaders;
import me.alpha432.oyvey.manager.shader.ShaderManager;

import static me.alpha432.oyvey.util.traits.Util.mc;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {
    @Shadow
    public abstract void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix);

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/PostEffectProcessor;render(F)V", ordinal = 0))
    void replaceShaderHook(PostEffectProcessor instance, float tickDelta) {
        ShaderManager.Shader shaders = Shaders.getInstance().mode.getValue();
        if (Shaders.getInstance().isEnabled() && mc.world != null) {
            if (CrackedClient.shaderManager.fullNullCheck()) return;
            CrackedClient.shaderManager.setupShader(shaders, CrackedClient.shaderManager.getShaderOutline(shaders));
        } else {
            instance.render(tickDelta);
        }
    }
}
