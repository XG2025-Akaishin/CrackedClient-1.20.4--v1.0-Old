package me.alpha432.oyvey.features.modules.misc.scaffol.utils;

import static me.alpha432.oyvey.util.traits.Util.mc;

import me.alpha432.oyvey.mixin.scaffol.IVec3d;

public class MovementUtil {
    public static void setMotionX(double x) {
        ((IVec3d) mc.player.getVelocity()).setX(x);
    }
    public static void setMotionY(double y) {
        ((IVec3d) mc.player.getVelocity()).setY(y);
    }
    public static void setMotionZ(double z) {
        ((IVec3d) mc.player.getVelocity()).setZ(z);
    }

    public static boolean isMoving() {
        return mc.player.input.movementForward != 0.0 || mc.player.input.movementSideways != 0.0 /*|| HoleSnap.INSTANCE.isOn()*/;
    }
}
