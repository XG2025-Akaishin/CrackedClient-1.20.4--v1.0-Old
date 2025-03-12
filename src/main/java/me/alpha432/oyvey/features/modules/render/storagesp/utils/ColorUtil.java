package me.alpha432.oyvey.features.modules.render.storagesp.utils;

import java.awt.Color;

import net.minecraft.util.math.MathHelper;

public class ColorUtil {
    public static Color injectAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp(alpha, 0, 255));
    }
}
