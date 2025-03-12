package me.alpha432.oyvey.features.modules.render.popchams.utils;

public class AnimateUtil {
    public static double animate(double current, double endPoint, double speed) {
        if (speed >= 1) return endPoint;
        if (speed == 0) return current;
        return thunder(current, endPoint, speed);
    }
    public static double thunder(double current, double endPoint, double speed) {
        boolean shouldContinueAnimation = endPoint > current;

        double dif = Math.max(endPoint, current) - Math.min(endPoint, current);
        if (Math.abs(dif) <= 0.001) return endPoint;
        double factor = dif * speed;
        return current + (shouldContinueAnimation ? factor : -factor);
    }
}
