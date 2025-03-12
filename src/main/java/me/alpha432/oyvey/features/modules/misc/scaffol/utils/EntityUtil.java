package me.alpha432.oyvey.features.modules.misc.scaffol.utils;

import me.alpha432.oyvey.features.modules.combat.autototem.AutoTotem;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import static me.alpha432.oyvey.util.traits.Util.mc;
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
import me.alpha432.oyvey.features.modules.misc.scaffol.Scaffold;
import me.alpha432.oyvey.features.modules.misc.scaffol.Scaffold.OFFMain;
import me.alpha432.oyvey.features.modules.misc.scaffol.Scaffold.SwingSide;

public class EntityUtil {
    public static boolean rotating = false;
    public static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{mc.player.getYaw() + MathHelper.wrapDegrees(yaw - mc.player.getYaw()), mc.player.getPitch() + MathHelper.wrapDegrees(pitch - mc.player.getPitch())};
    }
    public static Vec3d getEyesPos() {
        return mc.player.getEyePos();
    }
        public static BlockPos getPlayerPos() {
        return null;
    }
    public static boolean canSee(BlockPos pos, Direction side) {
        Vec3d testVec = pos.toCenterPos().add(side.getVector().getX() * 0.5, side.getVector().getY() * 0.5, side.getVector().getZ() * 0.5);
        HitResult result = mc.world.raycast(new RaycastContext(getEyesPos(), testVec, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player));
        return result == null || result.getType() == HitResult.Type.MISS;
    }

    public static void swingHand(Hand hand, SwingSide side) {
        boolean isCustom = Scaffold.getInstance().customhand.getValue();


        if(!isCustom) {
            switch (side) {
                case All -> mc.player.swingHand(hand);
                case Client -> mc.player.swingHand(hand, false);
                case Server -> mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(hand));
            }
        }else{
            Hand custom=null;
            if(Scaffold.getInstance().modeMain.getValue()== OFFMain.MAIN_HAND){
                custom=Hand.MAIN_HAND;
            }
            else if(Scaffold.getInstance().modeMain.getValue()== OFFMain.OFF_HAND){
                custom=Hand.OFF_HAND;
            }
            if(custom==null){
                return;
            }
            switch (side) {
                case All -> mc.player.swingHand(custom);
                case Client -> mc.player.swingHand(custom, false);
                case Server -> mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(custom));
            }
        }
    }
}
