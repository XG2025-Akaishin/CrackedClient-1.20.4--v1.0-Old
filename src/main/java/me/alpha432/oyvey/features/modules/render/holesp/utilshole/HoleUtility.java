package me.alpha432.oyvey.features.modules.render.holesp.utilshole;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static me.alpha432.oyvey.features.modules.render.holesp.utilshole.Util.mc;

public final class HoleUtility {
    public static final Vec3i[] VECTOR_PATTERN = {
            new Vec3i(0, 0, 1),
            new Vec3i(0, 0, -1),
            new Vec3i(1, 0, 0),
            new Vec3i(-1, 0, 0)
    };

    public static boolean validIndestructible(@NotNull BlockPos pos) {
        return !validBedrock(pos)
                && (isIndestructible(pos.add(0, -1, 0)) || isBedrock(pos.add(0, -1, 0)))
                && (isIndestructible(pos.add(1, 0, 0)) || isBedrock(pos.add(1, 0, 0)))
                && (isIndestructible(pos.add(-1, 0, 0)) || isBedrock(pos.add(-1, 0, 0)))
                && (isIndestructible(pos.add(0, 0, 1)) || isBedrock(pos.add(0, 0, 1)))
                && (isIndestructible(pos.add(0, 0, -1)) || isBedrock(pos.add(0, 0, -1)))
                && isReplaceable(pos)
                && isReplaceable(pos.add(0, 1, 0))
                && isReplaceable(pos.add(0, 2, 0));
    }

    public static boolean validBedrock(@NotNull BlockPos pos) {
        return isBedrock(pos.add(0, -1, 0))
                && isBedrock(pos.add(1, 0, 0))
                && isBedrock(pos.add(-1, 0, 0))
                && isBedrock(pos.add(0, 0, 1))
                && isBedrock(pos.add(0, 0, -1))
                && isReplaceable(pos)
                && isReplaceable(pos.add(0, 1, 0))
                && isReplaceable(pos.add(0, 2, 0));
    }

    public static boolean validTwoBlockBedrock(@NotNull BlockPos pos) {
        if (!isReplaceable(pos)) return false;
        Vec3i addVec = getTwoBlocksDirection(pos);

        // If addVec not found -> hole incorrect
        if (addVec == null)
            return false;

        BlockPos[] checkPoses = new BlockPos[]{pos, pos.add(addVec)};
        // Check surround poses of checkPoses
        for (BlockPos checkPos : checkPoses) {
            BlockPos downPos = checkPos.down();
            if (!isBedrock(downPos))
                return false;

            for (Vec3i vec : VECTOR_PATTERN) {
                BlockPos reducedPos = checkPos.add(vec);
                if (!isBedrock(reducedPos) && !reducedPos.equals(pos) && !reducedPos.equals(pos.add(addVec)))
                    return false;
            }
        }

        return true;
    }

    public static boolean validTwoBlockIndestructible(@NotNull BlockPos pos) {
        if (!isReplaceable(pos)) return false;
        Vec3i addVec = getTwoBlocksDirection(pos);

        // If addVec not found -> hole incorrect
        if (addVec == null)
            return false;

        BlockPos[] checkPoses = new BlockPos[]{pos, pos.add(addVec)};
        // Check surround poses of checkPoses
        boolean wasIndestrictible = false;
        for (BlockPos checkPos : checkPoses) {
            BlockPos downPos = checkPos.down();
            if (isIndestructible(downPos))
                wasIndestrictible = true;
            else if (!isBedrock(downPos))
                return false;

            for (Vec3i vec : VECTOR_PATTERN) {
                BlockPos reducedPos = checkPos.add(vec);

                if (isIndestructible(reducedPos)) {
                    wasIndestrictible = true;
                    continue;
                }
                if (!isBedrock(reducedPos) && !reducedPos.equals(pos) && !reducedPos.equals(pos.add(addVec)))
                    return false;
            }
        }

        return wasIndestrictible;
    }

    private static @Nullable Vec3i getTwoBlocksDirection(BlockPos pos) {
        // Try to get direction
        for (Vec3i vec : VECTOR_PATTERN) {
            if (isReplaceable(pos.add(vec)))
                return vec;
        }

        return null;
    }

    public static boolean validQuadIndestructible(@NotNull BlockPos pos) {
        List<BlockPos> checkPoses = getQuadDirection(pos);
        // If checkPoses not found -> hole incorrect
        if (checkPoses == null)
            return false;

        boolean wasIndestrictible = false;
        for (BlockPos checkPos : checkPoses) {
            BlockPos downPos = checkPos.down();
            if (isIndestructible(downPos)) {
                wasIndestrictible = true;
            } else if (!isBedrock(downPos)) {
                return false;
            }

            for (Vec3i vec : VECTOR_PATTERN) {
                BlockPos reducedPos = checkPos.add(vec);

                if (isIndestructible(reducedPos)) {
                    wasIndestrictible = true;
                    continue;
                }
                if (!isBedrock(reducedPos) && !checkPoses.contains(reducedPos)) {
                    return false;
                }
            }
        }

        return wasIndestrictible;
    }

    public static boolean validQuadBedrock(@NotNull BlockPos pos) {
        List<BlockPos> checkPoses = getQuadDirection(pos);
        // If checkPoses not found -> hole incorrect
        if (checkPoses == null)
            return false;

        for (BlockPos checkPos : checkPoses) {
            BlockPos downPos = checkPos.down();
            if (!isBedrock(downPos)) {
                return false;
            }

            for (Vec3i vec : VECTOR_PATTERN) {
                BlockPos reducedPos = checkPos.add(vec);
                if (!isBedrock(reducedPos) && !checkPoses.contains(reducedPos)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static @Nullable List<BlockPos> getQuadDirection(@NotNull BlockPos pos) {
        // Try to get direction
        List<BlockPos> dirList = new ArrayList<>();
        dirList.add(pos);

        if (!isReplaceable(pos))
            return null;

        if (isReplaceable(pos.add(1, 0, 0)) && isReplaceable(pos.add(0, 0, 1)) && isReplaceable(pos.add(1, 0, 1))) {
            dirList.add(pos.add(1, 0, 0));
            dirList.add(pos.add(0, 0, 1));
            dirList.add(pos.add(1, 0, 1));
        }
        if (isReplaceable(pos.add(-1, 0, 0)) && isReplaceable(pos.add(0, 0, -1)) && isReplaceable(pos.add(-1, 0, -1))) {
            dirList.add(pos.add(-1, 0, 0));
            dirList.add(pos.add(0, 0, -1));
            dirList.add(pos.add(-1, 0, -1));
        }
        if (isReplaceable(pos.add(1, 0, 0)) && isReplaceable(pos.add(0, 0, -1)) && isReplaceable(pos.add(1, 0, -1))) {
            dirList.add(pos.add(1, 0, 0));
            dirList.add(pos.add(0, 0, -1));
            dirList.add(pos.add(1, 0, -1));
        }
        if (isReplaceable(pos.add(-1, 0, 0)) && isReplaceable(pos.add(0, 0, 1)) && isReplaceable(pos.add(-1, 0, 1))) {
            dirList.add(pos.add(-1, 0, 0));
            dirList.add(pos.add(0, 0, 1));
            dirList.add(pos.add(-1, 0, 1));
        }

        if (dirList.size() != 4)
            return null;

        return dirList;
    }

    private static boolean isIndestructible(BlockPos bp) {
        if (mc.world == null) return false;

        return mc.world.getBlockState(bp).getBlock() == Blocks.OBSIDIAN
                || mc.world.getBlockState(bp).getBlock() == Blocks.NETHERITE_BLOCK
                || mc.world.getBlockState(bp).getBlock() == Blocks.CRYING_OBSIDIAN
                || mc.world.getBlockState(bp).getBlock() == Blocks.RESPAWN_ANCHOR;
    }

    private static boolean isBedrock(BlockPos bp) {
        if (mc.world == null) return false;

        return mc.world.getBlockState(bp).getBlock() == Blocks.BEDROCK;
    }

    private static boolean isReplaceable(BlockPos bp) {
        if (mc.world == null) return false;

        return mc.world.getBlockState(bp).isReplaceable();
    }
}
