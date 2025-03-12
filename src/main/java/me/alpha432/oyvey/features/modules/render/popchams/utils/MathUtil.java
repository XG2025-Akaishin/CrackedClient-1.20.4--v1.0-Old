package me.alpha432.oyvey.features.modules.render.popchams.utils;

public class MathUtil {
    public static float clamp(float num, float min, float max) {
        return num < min ? min : Math.min(num, max);
    }
    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        return Math.min(value, max);
    }
    public static float rad(float angle) {
        return (float) (angle * Math.PI / 180);
    }
}
