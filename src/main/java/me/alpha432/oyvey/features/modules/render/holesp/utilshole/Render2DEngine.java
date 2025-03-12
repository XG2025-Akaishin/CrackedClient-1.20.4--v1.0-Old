package me.alpha432.oyvey.features.modules.render.holesp.utilshole;

import net.minecraft.util.math.MathHelper;
import java.awt.*;

public class Render2DEngine {

    public static Color injectAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp(alpha, 0, 255));
    }
}
