package me.alpha432.oyvey.features.modules.render.nametags.util;

import me.alpha432.oyvey.util.traits.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.*;

public class EntityUtil implements Util {
    public static BlockPos getPlayerPos() {
        return null;
    }
    public static Vec3d getEyesPos() {
        return mc.player.getEyePos();
    }
}
