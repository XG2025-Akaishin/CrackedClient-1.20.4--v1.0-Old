package me.alpha432.oyvey.features.modules.render.particles.utils;

import java.util.concurrent.ThreadLocalRandom;


public final class MathUtility {
    public static double random(double min, double max) {
        return ThreadLocalRandom.current().nextDouble() * (max - min) + min;
    }

    public static float random(float min, float max) {
        return (float) (Math.random() * (max - min) + min);
    }

}
