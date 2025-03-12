package me.alpha432.oyvey.mixin.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.resource.ResourceFactory;
import com.mojang.datafixers.util.Pair;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gl.ShaderStage;
import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.features.modules.render.particles.utils.GlProgram;

import java.util.List;
import java.util.function.Consumer;


@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    @Shadow
    public abstract void render(float tickDelta, long startTime, boolean tick);

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z", opcode = Opcodes.GETFIELD, ordinal = 0), method = "renderWorld")
    void render3dHook(float tickDelta, long limitTime, @NotNull MatrixStack matrix, CallbackInfo ci) {
        CrackedClient.moduleManager.onPreRender3D(matrix);
    }

    @Inject(method = "loadPrograms", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    void loadAllTheShaders(ResourceFactory factory, CallbackInfo ci, List<ShaderStage> stages, List<Pair<ShaderProgram, Consumer<ShaderProgram>>> shadersToLoad) {
        GlProgram.forEachProgram(loader -> shadersToLoad.add(new Pair<>(loader.getLeft().apply(factory), loader.getRight())));
    }

}
