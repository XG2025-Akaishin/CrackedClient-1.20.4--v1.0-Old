package me.alpha432.oyvey.features.modules.render.entityesp.utilsd;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import static me.alpha432.oyvey.util.traits.Util.mc;

public class Utild {
        public static Vec3d getMotionVec(Entity entity, int ticks, boolean collision) {
        double dX = entity.getX() - entity.prevX;
        double dY = entity.getY() - entity.prevY;
        double dZ = entity.getZ() - entity.prevZ;
        double entityMotionPosX = 0;
        double entityMotionPosY = 0;
        double entityMotionPosZ = 0;
        if (collision) {
            for (double i = 1; i <= ticks; i = i + 0.5) {
                if (!mc.world.canCollide(entity, entity.getBoundingBox().offset(new Vec3d(dX * i, 0, dZ * i)))) {
                    entityMotionPosX = dX * i;
                    entityMotionPosZ = dZ * i;
                } else {
                    break;
                }
            }
            for (double i = 1; i <= 3; i = i + 0.5) {
                if (!mc.world.canCollide(entity, entity.getBoundingBox().offset(new Vec3d(0, dY * i, 0)))) {
                    entityMotionPosY = dY * i;
                } else {
                    break;
                }
            }
        } else {
            entityMotionPosX = dX * ticks;
            entityMotionPosY = dY * ticks;
            entityMotionPosZ = dZ * ticks;
        }

        return new Vec3d(entityMotionPosX, entityMotionPosY, entityMotionPosZ);
    }
}
