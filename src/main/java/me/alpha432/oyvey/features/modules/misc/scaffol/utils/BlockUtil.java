package me.alpha432.oyvey.features.modules.misc.scaffol.utils;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;

import static me.alpha432.oyvey.util.traits.Util.mc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.alpha432.oyvey.features.modules.misc.scaffol.Scaffold.Placement;
import me.alpha432.oyvey.features.modules.misc.scaffol.Scaffold.SwingSide;
import me.alpha432.oyvey.features.modules.misc.scaffol.Scaffold;
import me.alpha432.oyvey.mixin.scaffol.IClientWorld;

public class BlockUtil {

    public static final List<Block> shiftBlocks = Arrays.asList(
            Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE,
            Blocks.BIRCH_TRAPDOOR, Blocks.BAMBOO_TRAPDOOR, Blocks.DARK_OAK_TRAPDOOR, Blocks.CHERRY_TRAPDOOR,
            Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER,
            Blocks.ACACIA_TRAPDOOR, Blocks.ENCHANTING_TABLE, Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX,
            Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX,
            Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX,
            Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX
    );

    public static boolean clientCanPlace(BlockPos pos) {
        return clientCanPlace(pos, false);
    }
    public static boolean clientCanPlace(BlockPos pos, boolean ignoreCrystal) {
        if (!canReplace(pos)) return false;
        return !hasEntity(pos, ignoreCrystal);
    }
    public static boolean canReplace(BlockPos pos) {
        return getState(pos).isReplaceable();
    }

    public static boolean hasEntity(BlockPos pos, boolean ignoreCrystal) {
        for (Entity entity : mc.world.getNonSpectatingEntities(Entity.class, new Box(pos))) {
            if (!entity.isAlive() || entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof ExperienceBottleEntity || entity instanceof ArrowEntity || ignoreCrystal && entity instanceof EndCrystalEntity || entity instanceof ArmorStandEntity && Scaffold.getInstance().obsMode.getValue())
                continue;
            return true;
        }
        return false;
    }

    public static BlockState getState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }

    public static boolean canPlace(BlockPos pos, double distance) {
        if (getPlaceSide(pos, distance) == null) return false;
        if (!canReplace(pos)) return false;
        return !hasEntity(pos, false);
    }

    public static Direction getPlaceSide(BlockPos pos) {
        return getPlaceSide(pos, Scaffold.getInstance().placement.getValue() == Placement.Strict, Scaffold.getInstance().placement.getValue() == Placement.Legit);
    }

    public static boolean canPlace(BlockPos pos) {
        return canPlace(pos, 1000);
    }

    public static Direction getPlaceSide(BlockPos pos, boolean strict, boolean legit) {
        double dis = 114514;
        Direction side = null;
        for (Direction i : Direction.values()) {
            if (canClick(pos.offset(i)) && !canReplace(pos.offset(i))) {
                if (legit) {
                    if (!EntityUtil.canSee(pos.offset(i), i.getOpposite())) continue;
                }
                if (strict) {
                    if (!isStrictDirection(pos.offset(i), i.getOpposite())) continue;
                }
                double vecDis = mc.player.squaredDistanceTo(pos.toCenterPos().add(i.getVector().getX() * 0.5, i.getVector().getY() * 0.5, i.getVector().getZ() * 0.5));
                if (side == null || vecDis < dis) {
                    side = i;
                    dis = vecDis;
                }
            }
        }
        if (side == null && airPlace()) {
            for (Direction i : Direction.values()) {
                if (mc.world.isAir(pos.offset(i))) {
                    return i;
                }
            }
        }
        return side;
    }

    public static Direction getPlaceSide(BlockPos pos, double distance) {
        double dis = 114514;
        Direction side = null;
        for (Direction i : Direction.values()) {
            if (canClick(pos.offset(i)) && !canReplace(pos.offset(i))) {
                if (Scaffold.getInstance().placement.getValue() == Placement.Legit) {
                    if (!EntityUtil.canSee(pos.offset(i), i.getOpposite())) continue;
                } else if (Scaffold.getInstance().placement.getValue() == Placement.Strict) {
                    if (!isStrictDirection(pos.offset(i), i.getOpposite())) continue;
                }
                double vecDis = mc.player.squaredDistanceTo(pos.toCenterPos().add(i.getVector().getX() * 0.5, i.getVector().getY() * 0.5, i.getVector().getZ() * 0.5));
                if (MathHelper.sqrt((float) vecDis) > distance) {
                    continue;
                }
                if (side == null || vecDis < dis) {
                    side = i;
                    dis = vecDis;
                }
            }
        }
        if (side == null && airPlace()) {
            for (Direction i : Direction.values()) {
                if (mc.world.isAir(pos.offset(i))) {
                    return i;
                }
            }
        }
        return side;
    }

    public static boolean canClick(BlockPos pos) {
        return mc.world.getBlockState(pos).isSolid() && (!(shiftBlocks.contains(getBlock(pos)) || getBlock(pos) instanceof BedBlock) || mc.player.isSneaking());
    }

    public static boolean isStrictDirection(BlockPos pos, Direction side) {
        BlockState blockState = mc.world.getBlockState(pos);
        boolean isFullBox = blockState.getBlock() == Blocks.AIR || blockState.isFullCube(mc.world, pos) || getBlock(pos) == Blocks.COBWEB;
        return isStrictDirection(pos, side, isFullBox);
    }

    public static boolean isStrictDirection(BlockPos pos, Direction side, boolean isFullBox) {
        if (EntityUtil.getPlayerPos().getY() - pos.getY() >= 0 && side == Direction.DOWN) return false;
        if (getBlock(pos.offset(side)) == Blocks.OBSIDIAN || getBlock(pos.offset(side)) == Blocks.BEDROCK || getBlock(pos.offset(side)) == Blocks.RESPAWN_ANCHOR) return false;
        Vec3d eyePos = EntityUtil.getEyesPos();
        Vec3d blockCenter = pos.toCenterPos();
        ArrayList<Direction> validAxis = new ArrayList<>();
        validAxis.addAll(checkAxis(eyePos.x - blockCenter.x, Direction.WEST, Direction.EAST, !isFullBox));
        validAxis.addAll(checkAxis(eyePos.y - blockCenter.y, Direction.DOWN, Direction.UP, true));
        validAxis.addAll(checkAxis(eyePos.z - blockCenter.z, Direction.NORTH, Direction.SOUTH, !isFullBox));
        return validAxis.contains(side);
    }

    public static ArrayList<Direction> checkAxis(double diff, Direction negativeSide, Direction positiveSide, boolean bothIfInRange) {
        ArrayList<Direction> valid = new ArrayList<>();
        if (diff < -0.5) {
            valid.add(negativeSide);
        }
        if (diff > 0.5) {
            valid.add(positiveSide);
        }
        if (bothIfInRange) {
            if (!valid.contains(negativeSide)) valid.add(negativeSide);
            if (!valid.contains(positiveSide)) valid.add(positiveSide);
        }
        return valid;
    }

    public static boolean airPlace() {
        return Scaffold.getInstance().placement.getValue() == Placement.AirPlace;
    }

    public static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    public static boolean needSneak(Block in) {
        return shiftBlocks.contains(in);
    }

    public static final ArrayList<BlockPos> placedPos = new ArrayList<>();
    public static void placeBlock(BlockPos pos, boolean rotate) {
        placeBlock(pos, rotate, Scaffold.getInstance().packetPlace.getValue());
    }

    public static void placeBlock(BlockPos pos, boolean rotate, boolean packet) {

        if (airPlace()) {
            for (Direction i : Direction.values()) {
                if (mc.world.isAir(pos.offset(i))) {
                    clickBlock(pos, i, rotate, Hand.MAIN_HAND, packet);
                    return;
                }
            }
        }
        Direction side = getPlaceSide(pos);
        if (side == null) return;
        placedPos.add(pos);

        clickBlock(pos.offset(side), side.getOpposite(), rotate, Hand.MAIN_HAND, packet);

    }

    public static void placeBlock(BlockPos pos, boolean rotate, boolean packet ,boolean bypass) {
        if (airPlace()) {
            for (Direction i : Direction.values()) {
                if (mc.world.isAir(pos.offset(i))) {
                    clickBlock(pos, i, rotate, Hand.MAIN_HAND, packet);
                    return;
                }
            }
        }
        Direction side = getPlaceSide(pos);
        if (side == null) return;
        Vec3d directionVec = new Vec3d(pos.getX() + 0.5 + side.getVector().getX() * 0.5, pos.getY() + 0.5 + side.getVector().getY() * 0.5, pos.getZ() + 0.5 + side.getVector().getZ() * 0.5);
        /*if (rotate) {
            EntityUtil.faceVector(directionVec);
        }*/
        EntityUtil.swingHand(Hand.MAIN_HAND, Scaffold.getInstance().swingMode.getValue());
        BlockHitResult result = new BlockHitResult(directionVec, side, pos, false);
         placedPos.add(pos);
        boolean sprint = false;
        if (mc.player != null) {
            sprint = mc.player.isSprinting();
        }
        boolean sneak = false;
        if (mc.world != null) {
            sneak = needSneak(mc.world.getBlockState(result.getBlockPos()).getBlock()) && !mc.player.isSneaking();
        }
        if (sprint)
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
        if (sneak)
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));

        clickBlock(pos.offset(side), side.getOpposite(), rotate, Hand.MAIN_HAND, packet);

        if (sneak)
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        if (sprint)
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
        if (bypass)
            EntityUtil.swingHand(Hand.MAIN_HAND, Scaffold.getInstance().swingMode.getValue());
    }

    public static void clickBlock(BlockPos pos, Direction side, boolean rotate) {
        clickBlock(pos, side, rotate, Hand.MAIN_HAND);
    }

    public static void clickBlock(BlockPos pos, Direction side, boolean rotate, Hand hand) {
        clickBlock(pos, side, rotate, hand, Scaffold.getInstance().packetPlace.getValue());
    }

    public static void clickBlock(BlockPos pos, Direction side, boolean rotate, boolean packet) {
        clickBlock(pos, side, rotate, Hand.MAIN_HAND, packet);
    }

    public static void clickBlock(BlockPos pos, Direction side, boolean rotate, Hand hand, boolean packet) {
        Vec3d directionVec = new Vec3d(pos.getX() + 0.5 + side.getVector().getX() * 0.5, pos.getY() + 0.5 + side.getVector().getY() * 0.5, pos.getZ() + 0.5 + side.getVector().getZ() * 0.5);
        /*if (rotate) {
            EntityUtil.faceVector(directionVec);
        }*/
        EntityUtil.swingHand(hand, Scaffold.getInstance().swingMode.getValue());
        BlockHitResult result = new BlockHitResult(directionVec, side, pos, false);
        if (packet) {
            mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, result, getWorldActionId(mc.world)));
        } else {
            mc.interactionManager.interactBlock(mc.player, hand, result);
        }
    }

    public static int getWorldActionId(ClientWorld world) {
        PendingUpdateManager pum = getUpdateManager(world);
        int p = pum.getSequence();
        pum.close();
        return p;
    }

    public static PendingUpdateManager getUpdateManager(ClientWorld world) {
        return ((IClientWorld) world).acquirePendingUpdateManager();
    }

}
