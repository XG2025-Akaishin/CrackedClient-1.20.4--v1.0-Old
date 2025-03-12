package me.alpha432.oyvey.features.modules.render.particles.utils;

import net.minecraft.util.math.*;
import static me.alpha432.oyvey.features.modules.render.particles.utils.Util.mc;

public class TextureOthers {

    public static Vec3d interpolatePos(float prevposX, float prevposY, float prevposZ, float posX, float posY, float posZ) {
        double x = prevposX + ((posX - prevposX) * mc.getTickDelta()) - mc.getEntityRenderDispatcher().camera.getPos().getX();
        double y = prevposY + ((posY - prevposY) * mc.getTickDelta()) - mc.getEntityRenderDispatcher().camera.getPos().getY();
        double z = prevposZ + ((posZ - prevposZ) * mc.getTickDelta()) - mc.getEntityRenderDispatcher().camera.getPos().getZ();
        return new Vec3d(x, y, z);
    }
}
