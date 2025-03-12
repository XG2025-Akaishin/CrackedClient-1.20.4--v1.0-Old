package me.alpha432.oyvey.manager.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL30C;
import me.alpha432.oyvey.manager.IManager;
import me.alpha432.oyvey.features.modules.render.shaders.Shaders;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.manager.shader.ShaderManager.RenderTask;
import me.alpha432.oyvey.manager.shader.ShaderManager.Shader;
import me.alpha432.oyvey.manager.shader.ShaderManager.ThunderHackFramebuffer;
import me.alpha432.oyvey.manager.shader.utilis.IShaderEffect;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ShaderManager implements IManager {
    private final static List<RenderTask> tasks = new ArrayList<>();
    private ThunderHackFramebuffer shaderBuffer;
    private int color;//Color :(
    public float time = 0;

    public static ManagedShaderEffect DEFAULT_OUTLINE;
    public static ManagedShaderEffect SMOKE_OUTLINE;
    public static ManagedShaderEffect GRADIENT_OUTLINE;
    public static ManagedShaderEffect SNOW_OUTLINE;

    public static ManagedShaderEffect DEFAULT;
    public static ManagedShaderEffect SMOKE;
    public static ManagedShaderEffect GRADIENT;
    public static ManagedShaderEffect SNOW;

    public void renderShader(Runnable runnable, Shader mode) {
        tasks.add(new RenderTask(runnable, mode));
    }

    public void renderShaders() {
        if (DEFAULT == null) {
            shaderBuffer = new ThunderHackFramebuffer(mc.getFramebuffer().textureWidth, mc.getFramebuffer().textureHeight);
            reloadShaders();
            
        }

        tasks.forEach(t -> applyShader(t.task(), t.shader()));
        tasks.clear();
    }

    public void applyShader(Runnable runnable, Shader mode) {
        Framebuffer MCBuffer = MinecraftClient.getInstance().getFramebuffer();
        RenderSystem.assertOnRenderThreadOrInit();
        if (shaderBuffer.textureWidth != MCBuffer.textureWidth || shaderBuffer.textureHeight != MCBuffer.textureHeight)
            shaderBuffer.resize(MCBuffer.textureWidth, MCBuffer.textureHeight, false);
        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, shaderBuffer.fbo);
        shaderBuffer.beginWrite(true);
        runnable.run();
        shaderBuffer.endWrite();
        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, MCBuffer.fbo);
        MCBuffer.beginWrite(false);
        ManagedShaderEffect shader = getShader(mode);
        Framebuffer mainBuffer = MinecraftClient.getInstance().getFramebuffer();
        PostEffectProcessor effect = shader.getShaderEffect();

        if (effect != null)
            ((IShaderEffect) effect).addFakeTargetHook("bufIn", shaderBuffer);

        Framebuffer outBuffer = shader.getShaderEffect().getSecondaryTarget("bufOut");
        setupShader(mode, shader);
        shaderBuffer.clear(false);
        mainBuffer.beginWrite(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        RenderSystem.backupProjectionMatrix();
        outBuffer.draw(outBuffer.textureWidth, outBuffer.textureHeight, false);
        RenderSystem.restoreProjectionMatrix();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    public ManagedShaderEffect getShader(@NotNull Shader mode) {
        return switch (mode) {
            case Gradient -> GRADIENT;
            case Smoke -> SMOKE;
            case Snow -> SNOW;
            default -> DEFAULT;
        };
    }

    public ManagedShaderEffect getShaderOutline(@NotNull Shader mode) {
        return switch (mode) {
            case Gradient -> GRADIENT_OUTLINE;
            case Smoke -> SMOKE_OUTLINE;
            case Snow -> SNOW_OUTLINE;
            default -> DEFAULT_OUTLINE;
        };
    }


    //Color Setting GL Color
    public int getRed() {
        return (color >> 16) & 0xFF;
    }

    public int getGreen() {
        return (color >> 8) & 0xFF;
    }

    public int getBlue() {
        return (color) & 0xFF;
    }

    public float getGlRed() {
        return getRed() / 255f;
    }

    public float getGlBlue() {
        return getBlue() / 255f;
    }

    public float getGlGreen() {

        return getGreen() / 255f;
    }

    public float getGlAlpha() {
        return getAlpha() / 255f;
    }

    public int getAlpha() {
        return (color >> 24) & 0xff;
    }

    public void setupShader(Shader shader, ManagedShaderEffect effect) {
        //Color outlineColor = Setting.Shaders.getInstance().colors.withParent(null);
        //if (Shaders.getInstance().colors.getValue()) {
        //Color outlineColor = new Color(Shaders.getInstance().outlineColorRed.getValue(),Shaders.getInstance().outlineColorGreen.getValue(),Shaders.getInstance().outlineColorBlue.getValue(),Shaders.getInstance().outlineColorAlpha.getValue());
        //Color outlineColor1 = new Color(Shaders.getInstance().outlineColor1Red.getValue(),Shaders.getInstance().outlineColor1Green.getValue(),Shaders.getInstance().outlineColor1Blue.getValue(),Shaders.getInstance().outlineColor1Alpha.getValue());
       // Color outlineColor2 = new Color(Shaders.getInstance().outlineColor2Red.getValue(),Shaders.getInstance().outlineColor2Green.getValue(),Shaders.getInstance().outlineColor2Blue.getValue(),Shaders.getInstance().outlineColor2Alpha.getValue());

        //Color fillColor1 = new Color(Shaders.getInstance().fillColor1Red.getValue(),Shaders.getInstance().fillColor1Green.getValue(),Shaders.getInstance().fillColor1Blue.getValue(),Shaders.getInstance().fillColor1Alpha.getValue());
        //Color fillColor2 = new Color(Shaders.getInstance().fillColor2Red.getValue(),Shaders.getInstance().fillColor2Green.getValue(),Shaders.getInstance().fillColor2Blue.getValue(),Shaders.getInstance().fillColor2Alpha.getValue());
        //Color fillColor3 = new Color(Shaders.getInstance().fillColor3Red.getValue(),Shaders.getInstance().fillColor3Green.getValue(),Shaders.getInstance().fillColor3Blue.getValue(),Shaders.getInstance().fillColor3Alpha.getValue());
        
        //Alpha
        float outlineColorAlpha = Shaders.getInstance().outlineColorAlpha.getValue();
        float outlineColorAlphas = (getGlAlpha() + outlineColorAlpha) / 255f;
        //RED
        float outlineColorRed = Shaders.getInstance().outlineColorRed.getValue();
        float outlineColorReds = (getGlRed() + outlineColorRed ) / 255f;
        //BLUE
        float outlineColorBlue = Shaders.getInstance().outlineColorBlue.getValue();
        float outlineColorBlues = (getGlBlue() + outlineColorBlue) / 255f;
        //GREEN
        float outlineColorGreen = Shaders.getInstance().outlineColorGreen.getValue();
        float outlineColorGreens = (getGlGreen() + outlineColorGreen) / 255f;


        //Alpha
        float outlineColor1Alpha = Shaders.getInstance().outlineColor1Alpha.getValue();
        float outlineColor1Alphas = (getGlAlpha() + outlineColor1Alpha) / 255f;
        //RED
        float outlineColor1Red = Shaders.getInstance().outlineColor1Red.getValue();
        float outlineColor1Reds = (getGlRed() + outlineColor1Red ) / 255f;
        //BLUE
        float outlineColor1Blue = Shaders.getInstance().outlineColor1Blue.getValue();
        float outlineColor1Blues = (getGlBlue() + outlineColor1Blue) / 255f;
        //GREEN
        float outlineColor1Green = Shaders.getInstance().outlineColorGreen.getValue();
        float outlineColor1Greens = (getGlGreen() + outlineColor1Green) / 255f;


        //Alpha
        float outlineColor2Alpha = Shaders.getInstance().outlineColor2Alpha.getValue();
        float outlineColor2Alphas = (getGlAlpha() + outlineColor2Alpha) / 255f;
        //RED
        float outlineColor2Red = Shaders.getInstance().outlineColor2Red.getValue();
        float outlineColor2Reds = (getGlRed() + outlineColor2Red ) / 255f;
        //BLUE
        float outlineColor2Blue = Shaders.getInstance().outlineColor2Blue.getValue();
        float outlineColor2Blues = (getGlBlue() + outlineColor2Blue) / 255f;
        //GREEN
        float outlineColor2Green = Shaders.getInstance().outlineColor2Green.getValue();
        float outlineColor2Greens = (getGlGreen() + outlineColor2Green) / 255f;


        //Alpha
        float fillColor1Alpha = Shaders.getInstance().fillColor1Alpha.getValue();
        float fillColor1Alphas = (getGlAlpha() + fillColor1Alpha) / 255f;
        //RED
        float fillColor1Red = Shaders.getInstance().fillColor1Red.getValue();
        float fillColor1Reds = (getGlRed() + fillColor1Red ) / 255f;
        //BLUE
        float fillColor1Blue = Shaders.getInstance().fillColor1Blue.getValue();
        float fillColor1Blues = (getGlBlue() + fillColor1Blue) / 255f;
        //GREEN
        float fillColor1Green = Shaders.getInstance().fillColor1Green.getValue();
        float fillColor1Greens = (getGlGreen() + fillColor1Green) / 255f;

        //Alpha
        float fillColor2Alpha = Shaders.getInstance().fillColor2Alpha.getValue();
        float fillColor2Alphas = (getGlAlpha() + fillColor2Alpha) / 255f;
        //RED
        float fillColor2Red = Shaders.getInstance().fillColor2Red.getValue();
        float fillColor2Reds = (getGlRed() + fillColor2Red ) / 255f;
        //BLUE
        float fillColor2Blue = Shaders.getInstance().fillColor2Blue.getValue();
        float fillColor2Blues = (getGlBlue() + fillColor2Blue) / 255f;
        //GREEN
        float fillColor2Green = Shaders.getInstance().fillColor2Green.getValue();
        float fillColor2Greens = (getGlGreen() + fillColor2Green) / 255f;

        //Alpha
        float fillColor3Alpha = Shaders.getInstance().fillColor3Alpha.getValue();
        float fillColor3Alphas = (getGlAlpha() + fillColor3Alpha) / 255f;
        //RED
        float fillColor3Red = Shaders.getInstance().fillColor3Red.getValue();
        float fillColor3Reds = (getGlRed() + fillColor3Red ) / 255f;
        //BLUE
        float fillColor3Blue = Shaders.getInstance().fillColor3Blue.getValue();
        float fillColor3Blues = (getGlBlue() + fillColor3Blue) / 255f;
        //GREEN
        float fillColor3Green = Shaders.getInstance().fillColor3Green.getValue();
        float fillColor3Greens = (getGlGreen() + fillColor3Green) / 255f;


        //Shaders shaders = ModuleManager.shaders;
        if (shader == Shader.Gradient) {
            effect.setUniformValue("alpha0", Shaders.getInstance().glow.getValue() ? -1.0f : outlineColorAlphas / 255.0f);
            effect.setUniformValue("alpha1", Shaders.getInstance().fillAlpha.getValue() / 255f);
            effect.setUniformValue("alpha2", Shaders.getInstance().alpha2.getValue() / 255f);
            effect.setUniformValue("lineWidth", Shaders.getInstance().lineWidth.getValue());
            effect.setUniformValue("oct", Shaders.getInstance().octaves.getValue());
            effect.setUniformValue("quality", Shaders.getInstance().quality.getValue());
            effect.setUniformValue("factor", Shaders.getInstance().factor.getValue());
            effect.setUniformValue("moreGradient", Shaders.getInstance().gradient.getValue());
            effect.setUniformValue("resolution", (float) mc.getWindow().getScaledWidth(), (float) mc.getWindow().getScaledHeight());
            effect.setUniformValue("time", time);
            effect.render(mc.getTickDelta());
            time += 0.008f;
        } else if (shader == Shader.Smoke) {
            effect.setUniformValue("alpha0", Shaders.getInstance().glow.getValue() ? -1.0f : outlineColorAlphas / 255.0f);
            effect.setUniformValue("alpha1", Shaders.getInstance().fillAlpha.getValue() / 255f);
            effect.setUniformValue("lineWidth", Shaders.getInstance().lineWidth.getValue());
            effect.setUniformValue("quality", Shaders.getInstance().quality.getValue());
            effect.setUniformValue("first", outlineColorReds, outlineColorGreens, outlineColorBlues, outlineColorAlphas);
            effect.setUniformValue("second", outlineColor1Reds, outlineColor1Greens, outlineColor1Blues);
            effect.setUniformValue("third", outlineColor2Reds, outlineColor2Greens, outlineColor2Blues);
            effect.setUniformValue("ffirst", fillColor1Reds, fillColor1Greens, fillColor1Blues, fillColor1Alphas);
            effect.setUniformValue("fsecond", fillColor2Reds, fillColor2Greens, fillColor2Blues);
            effect.setUniformValue("fthird", fillColor3Reds, fillColor3Greens, fillColor3Blues);
            effect.setUniformValue("oct", Shaders.getInstance().octaves.getValue());
            effect.setUniformValue("resolution", (float) mc.getWindow().getScaledWidth(), (float) mc.getWindow().getScaledHeight());
            effect.setUniformValue("time", time);
            effect.render(mc.getTickDelta());
            time += 0.008f;
        } else if (shader == Shader.Default) {
            effect.setUniformValue("alpha0", Shaders.getInstance().glow.getValue() ? -1.0f : outlineColorAlphas / 255.0f);
            effect.setUniformValue("lineWidth", Shaders.getInstance().lineWidth.getValue());
            effect.setUniformValue("quality", Shaders.getInstance().quality.getValue());
            effect.setUniformValue("color", fillColor1Reds, fillColor1Greens, fillColor1Blues, fillColor1Alphas);
            effect.setUniformValue("outlinecolor", outlineColorReds, outlineColorGreens, outlineColorBlues, outlineColorAlphas);
            effect.render(mc.getTickDelta());
        } else if (shader == Shader.Snow) {
            effect.setUniformValue("color", fillColor1Reds, fillColor1Greens, fillColor1Blues, fillColor1Alphas);
            effect.setUniformValue("quality", Shaders.getInstance().quality.getValue());
            effect.setUniformValue("resolution", (float) mc.getWindow().getScaledWidth(), (float) mc.getWindow().getScaledHeight());
            effect.setUniformValue("time", time);
            effect.render(mc.getTickDelta());
            time += 0.008f;
        }
    //}
    }

    public void reloadShaders() {
        DEFAULT = ShaderEffectManager.getInstance().manage(new Identifier("minecraft", "shaders/post/outline.json"));
        SMOKE = ShaderEffectManager.getInstance().manage(new Identifier("minecraft", "shaders/post/smoke.json"));
        GRADIENT = ShaderEffectManager.getInstance().manage(new Identifier("minecraft", "shaders/post/gradient.json"));
        SNOW = ShaderEffectManager.getInstance().manage(new Identifier("minecraft", "shaders/post/snow.json"));

        DEFAULT_OUTLINE = ShaderEffectManager.getInstance().manage(new Identifier("minecraft", "shaders/post/outline.json"), managedShaderEffect -> {
            PostEffectProcessor effect = managedShaderEffect.getShaderEffect();
            if (effect == null) return;

            ((IShaderEffect) effect).addFakeTargetHook("bufIn", mc.worldRenderer.getEntityOutlinesFramebuffer());
            ((IShaderEffect) effect).addFakeTargetHook("bufOut", mc.worldRenderer.getEntityOutlinesFramebuffer());
        });

        SMOKE_OUTLINE = ShaderEffectManager.getInstance().manage(new Identifier("minecraft", "shaders/post/smoke.json"), managedShaderEffect -> {
            PostEffectProcessor effect = managedShaderEffect.getShaderEffect();
            if (effect == null) return;

            ((IShaderEffect) effect).addFakeTargetHook("bufIn", mc.worldRenderer.getEntityOutlinesFramebuffer());
            ((IShaderEffect) effect).addFakeTargetHook("bufOut", mc.worldRenderer.getEntityOutlinesFramebuffer());
        });

        GRADIENT_OUTLINE = ShaderEffectManager.getInstance().manage(new Identifier("minecraft", "shaders/post/gradient.json"), managedShaderEffect -> {
            PostEffectProcessor effect = managedShaderEffect.getShaderEffect();
            if (effect == null) return;

            ((IShaderEffect) effect).addFakeTargetHook("bufIn", mc.worldRenderer.getEntityOutlinesFramebuffer());
            ((IShaderEffect) effect).addFakeTargetHook("bufOut", mc.worldRenderer.getEntityOutlinesFramebuffer());
        });

        SNOW_OUTLINE = ShaderEffectManager.getInstance().manage(new Identifier("minecraft", "shaders/post/snow.json"), managedShaderEffect -> {
            PostEffectProcessor effect = managedShaderEffect.getShaderEffect();
            if (effect == null) return;

            ((IShaderEffect) effect).addFakeTargetHook("bufIn", mc.worldRenderer.getEntityOutlinesFramebuffer());
            ((IShaderEffect) effect).addFakeTargetHook("bufOut", mc.worldRenderer.getEntityOutlinesFramebuffer());
        });
    }

    public static class ThunderHackFramebuffer extends Framebuffer {
        public ThunderHackFramebuffer(int width, int height) {
            super(false);
            RenderSystem.assertOnRenderThreadOrInit();
            resize(width, height, true);
            setClearColor(0f, 0f, 0f, 0f);
        }
    }

    public boolean fullNullCheck() {
        if (GRADIENT == null || SMOKE == null || DEFAULT == null) {
            shaderBuffer = new ThunderHackFramebuffer(mc.getFramebuffer().textureWidth, mc.getFramebuffer().textureHeight);
            reloadShaders();
            return true;
        }

        return false;
    }

    public record RenderTask(Runnable task, Shader shader) {
    }

    public enum Shader {
        Default,
        Smoke,
        Gradient,
        Snow
    }
}
