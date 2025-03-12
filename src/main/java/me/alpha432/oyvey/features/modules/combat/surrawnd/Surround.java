package me.alpha432.oyvey.features.modules.combat.surrawnd;

import com.google.common.collect.Sets;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import me.alpha432.oyvey.features.modules.combat.surrawnd.utilsd.InventoryUtil;
import me.alpha432.oyvey.features.modules.combat.surrawnd.utilsd.WorldUtil;

import java.util.Set;

public class Surround extends Module {
    private final Setting<Boolean> center = this.register(new Setting<>("center", true));
    private final Setting<Boolean> autoDisable = this.register(new Setting<>("center", true));
    private final Setting<Boolean> offhand = this.register(new Setting<>("offhand", true));
    private final Setting<Boolean> swinghand = this.register(new Setting<>("swinghand", false));
    private final Setting<Boolean> checkEntities = this.register(new Setting<>("checkEntities", true));
    public Surround() {
        super("Surround", "Surround Test", Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        this.center.setValue(true);//enabled on enabled module
    }

    @Override
    public void onUpdate() {
        if (mc.player == null) {
            return;
        }

        place();

        if (this.center.getValue()) {
            Vec3d centerPos = Vec3d.ofBottomCenter(mc.player.getBlockPos());
            mc.player.updatePosition(centerPos.x, centerPos.y, centerPos.z);
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(centerPos.x, centerPos.y, centerPos.z, mc.player.isOnGround()));
            this.center.setValue(false);//disable
            if(autoDisable.getValue()) {
                setEnabled(false);//disabled module
            }
        }
    }

    private static final Set<Block> SURROUND_BLOCKS = Sets.newHashSet(Blocks.OBSIDIAN, Blocks.ENDER_CHEST, Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL, Blocks.CRYING_OBSIDIAN, Blocks.NETHERITE_BLOCK, Blocks.ANCIENT_DEBRIS, Blocks.RESPAWN_ANCHOR, Blocks.DIRT_PATH, Blocks.DIRT);

    private void place() {
        if (mc.player == null) {
            return;
        }

        int slot = InventoryUtil.getSlot(offhand.getValue(), i -> SURROUND_BLOCKS.contains(Block.getBlockFromItem(mc.player.getInventory().getStack(i).getItem())));

        BlockPos playerPos = mc.player.getBlockPos();

        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos offset = new BlockPos(direction.getOffsetX(), 0, direction.getOffsetZ());
            WorldUtil.place(playerPos.add(offset), Hand.MAIN_HAND, slot, swinghand.getValue(), checkEntities.getValue());
        }
    }
}