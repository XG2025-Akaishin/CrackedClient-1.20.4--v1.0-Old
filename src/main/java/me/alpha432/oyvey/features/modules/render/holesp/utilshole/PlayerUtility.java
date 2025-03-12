package me.alpha432.oyvey.features.modules.render.holesp.utilshole;

import net.minecraft.util.math.*;
import org.jetbrains.annotations.NotNull;

import static me.alpha432.oyvey.features.modules.render.holesp.utilshole.Util.mc;

public final class PlayerUtility {

    public static float squaredDistance2d(@NotNull Vec2f point) {
        if (mc.player == null) return 0f;

        double d = mc.player.getX() - point.x;
        double f = mc.player.getZ() - point.y;
        return (float) (d * d + f * f);
    }
    public static float squaredDistance2d(double x, double z) {
        if (mc.player == null) return 0f;

        double d = mc.player.getX() - x;
        double f = mc.player.getZ() - z;
        return (float) (d * d + f * f);
    }
}
