package me.alpha432.oyvey.features.modules.render.breakhighlight;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import me.alpha432.oyvey.mixin.breakhighlight.IWorldRenderer;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.render.holesp.utilshole.Render3DEngine;
import me.alpha432.oyvey.features.modules.render.storagesp.utils.UtilRenderESP;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.features.modules.Module;

import java.awt.*;

public class BreakHighLight extends Module {
    public BreakHighLight() {
        super("BreakHighLight", "BreakHighLight", Category.RENDER, true, false, false);
    }

    private final Setting<Mode> mode = this.register(new Setting("Mode", Mode.Shrink));

    private final Setting<Integer> colorRed = this.register(new Setting<>("ColorRed", 120, 0, 255));
    private final Setting<Integer> colorGreen = this.register(new Setting<>("ColorGreen", 120, 0, 255));
    private final Setting<Integer> colorBlue = this.register(new Setting<>("ColorBlue", 120, 0, 255));
    private final Setting<Integer> colorAlpha = this.register(new Setting<>("ColorAlpha", 120, 0, 255));

    private final Setting<Integer> color2Red = this.register(new Setting<>("Color2Red", 120, 0, 255));
    private final Setting<Integer> color2Green = this.register(new Setting<>("Color2Green", 120, 0, 255));
    private final Setting<Integer> color2Blue = this.register(new Setting<>("Color2Blue", 120, 0, 255));
    private final Setting<Integer> color2Alpha = this.register(new Setting<>("Color2Alpha", 120, 0, 255));

    private final Setting<Integer> ocolorRed = this.register(new Setting<>("OutlineColorRed", 120, 0, 255));
    private final Setting<Integer> ocolorGreen = this.register(new Setting<>("OutlineColorGreen", 120, 0, 255));
    private final Setting<Integer> ocolorBlue = this.register(new Setting<>("OutlineColorBlue", 120, 0, 255));
    private final Setting<Integer> ocolorAlpha = this.register(new Setting<>("OutlineColorAlpha", 120, 0, 255));

    private final Setting<Integer> ocolor2Red = this.register(new Setting<>("OutlineColor2Red", 120, 0, 255));
    private final Setting<Integer> ocolor2Green = this.register(new Setting<>("OutlineColor2Green", 120, 0, 255));
    private final Setting<Integer> ocolor2Blue = this.register(new Setting<>("OutlineColor2Blue", 120, 0, 255));
    private final Setting<Integer> ocolor2Alpha = this.register(new Setting<>("OutlineColor2Alpha", 120, 0, 255));

    //private final Setting<ColorSetting> textColor = new Setting<>("TextColor", new ColorSetting(0xFFFFFFFF));

    private final Setting<Float> lineWidth = this.register(new Setting<>("LineWidth", 2F, 0f, 5F));
    private final Setting<Boolean> otherPlayer = this.register(new Setting<>("OtherPlayer", true));

    private float prevProgress;

    public void onPreRender3D(MatrixStack stack) {
        if (mc.interactionManager.isBreakingBlock() && mc.crosshairTarget != null && mc.crosshairTarget instanceof BlockHitResult bhr && !mc.world.isAir(bhr.getBlockPos())) {
            Box shrunkMineBox = new Box(bhr.getBlockPos().getX(), bhr.getBlockPos().getY(), bhr.getBlockPos().getZ(), bhr.getBlockPos().getX(), bhr.getBlockPos().getY(), bhr.getBlockPos().getZ());

            float noom; //ам ням не ебался

            switch (mode.getValue()) {
                case Grow -> noom = interpolateFloat(prevProgress, clamp(mc.interactionManager.currentBreakingProgress, 0f, 1f), mc.getTickDelta());
                case Shrink -> noom = 1f - interpolateFloat(prevProgress, mc.interactionManager.currentBreakingProgress, mc.getTickDelta());
                default -> noom = 1;
            }

            Color color = new Color(colorRed.getValue(), colorGreen.getValue(), colorBlue.getValue(), colorAlpha.getValue());
            Color color2 = new Color(color2Red.getValue(), color2Green.getValue(), color2Blue.getValue(), color2Alpha.getValue());
            UtilRenderESP.drawFilledBox(
                    stack,
                    shrunkMineBox.shrink(noom, noom, noom).offset(0.5 + noom * 0.5, 0.5 + noom * 0.5, 0.5 + noom * 0.5),
                    interpolateColorC(color,color2,noom)
            );
            Color ocolor = new Color(ocolorRed.getValue(), ocolorGreen.getValue(), ocolorBlue.getValue(), ocolorAlpha.getValue());
            Color ocolor2 = new Color(ocolor2Red.getValue(), ocolor2Green.getValue(), ocolor2Blue.getValue(), ocolor2Alpha.getValue());
            UtilRenderESP.drawBoxOutline(
                    shrunkMineBox.shrink(noom, noom, noom).offset(0.5 + noom * 0.5, 0.5 + noom * 0.5, 0.5 + noom * 0.5),
                    interpolateColorC(ocolor,ocolor2,noom),
                    lineWidth.getValue()
            );

            switch (mode.getValue()) {
                case Grow -> prevProgress = noom;
                case Shrink -> prevProgress = 1 - noom;
                default -> prevProgress = 1f;
            }
        }
        //Color textColor = new Color(ocolorRed.getValue(), ocolorGreen.getValue(), ocolorBlue.getValue(), ocolorAlpha.getValue());
        ((IWorldRenderer) mc.worldRenderer).getBlockBreakingInfos().forEach(((integer, destroyBlockProgress) -> {
            Entity object = mc.world.getEntityById(integer);
            if (object != null && otherPlayer.getValue() && !object.getName().equals(mc.player.getName())) {
                BlockPos pos = destroyBlockProgress.getPos();
                //UtilRenderESP.drawTextIn3D(String.valueOf(object.getName().getString()),pos.toCenterPos(),0,0.1,0,textColor);
                //stack.drawTextWithShadow(mc.textRenderer, String.valueOf(object.getName().getString()) , (int) (0), (int) ( 0.1 ), textColor);
                //sendMessage("El Bloque: "String.valueOf(object.getName().getString()) );
                Box shrunkMineBox = new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());

                float noom;
                switch (mode.getValue()) {
                    case Grow -> noom = clamp((destroyBlockProgress.getStage() / 10f), 0f, 1f);
                    case Shrink -> noom = 1f - (destroyBlockProgress.getStage() / 10f);
                    default -> noom = 1;
                }
                Color color = new Color(colorRed.getValue(), colorGreen.getValue(), colorBlue.getValue(), colorAlpha.getValue());
                Color color2 = new Color(color2Red.getValue(), color2Green.getValue(), color2Blue.getValue(), color2Alpha.getValue());
                UtilRenderESP.drawFilledBox(
                        stack,
                        shrunkMineBox.shrink(noom, noom, noom).offset(0.5 + noom * 0.5, 0.5 + noom * 0.5, 0.5 + noom * 0.5),
                        interpolateColorC(color,color2,noom)
                );
                Color ocolor = new Color(ocolorRed.getValue(), ocolorGreen.getValue(), ocolorBlue.getValue(), ocolorAlpha.getValue());
                Color ocolor2 = new Color(ocolor2Red.getValue(), ocolor2Green.getValue(), ocolor2Blue.getValue(), ocolor2Alpha.getValue());
                UtilRenderESP.drawBoxOutline(
                        shrunkMineBox.shrink(noom, noom, noom).offset(0.5 + noom * 0.5, 0.5 + noom * 0.5, 0.5 + noom * 0.5),
                        interpolateColorC(ocolor,ocolor2,noom),
                        lineWidth.getValue()
                );
            }
        }));
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));
        return new Color(interpolateInt(color1.getRed(), color2.getRed(), amount), interpolateInt(color1.getGreen(), color2.getGreen(), amount), interpolateInt(color1.getBlue(), color2.getBlue(), amount), interpolateInt(color1.getAlpha(), color2.getAlpha(), amount));
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue) {
        return (int) interpolate(oldValue, newValue, (float) interpolationValue);
    }

    public static double interpolate(double oldValue, double newValue, double interpolationValue) {
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

    public static float interpolateFloat(float oldValue, float newValue, double interpolationValue) {
        return (float) interpolate(oldValue, newValue, (float) interpolationValue);
    }

    public static int clamp(int num, int min, int max) {
        return num < min ? min : Math.min(num, max);
    }

    public static float clamp(float num, float min, float max) {
        return num < min ? min : Math.min(num, max);
    }

    public static double clamp(double num, double min, double max) {
        return num < min ? min : Math.min(num, max);
    }

    private enum Mode {
        Grow, Shrink, Static
    }
}