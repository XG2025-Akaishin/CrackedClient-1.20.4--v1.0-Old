package me.alpha432.oyvey.features.modules.misc.fakeplayercracked.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import static me.alpha432.oyvey.util.traits.Util.mc;

public class BlockUtil {
    public static BlockState getState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }

    public static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }
}
