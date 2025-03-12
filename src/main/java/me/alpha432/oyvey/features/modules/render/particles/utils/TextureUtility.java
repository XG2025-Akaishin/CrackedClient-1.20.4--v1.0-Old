package me.alpha432.oyvey.features.modules.render.particles.utils;

import net.minecraft.util.math.MathHelper;
import java.awt.*;
import net.minecraft.util.Identifier;

public class TextureUtility {

    public static TextureColorProgram TEXTURE_COLOR_PROGRAM;

    public static final Identifier star = new Identifier("textures/img/star.png");
    public static final Identifier heart = new Identifier("textures/img/heart.png");
    public static final Identifier dollar = new Identifier("textures/img/dollar.png");
    public static final Identifier snowflake = new Identifier("textures/img/snowflake.png");
    public static final Identifier capture = new Identifier("textures/img/capture.png");
    public static final Identifier firefly = new Identifier("textures/img/firefly.png");
    public static final Identifier arrow = new Identifier("textures/img/triangle.png");

    public static Color injectAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp(alpha, 0, 255));
    }
    public static void initShaders() {
        TEXTURE_COLOR_PROGRAM = new TextureColorProgram();
    }
}
