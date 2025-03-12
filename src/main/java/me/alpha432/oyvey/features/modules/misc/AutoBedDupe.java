package me.alpha432.oyvey.features.modules.misc;

import com.google.common.eventbus.Subscribe;

import me.alpha432.oyvey.event.impl.freelook.TickEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class AutoBedDupe
        extends Module {
    private final Setting<Boolean> shulkersOnly  = this.register(new Setting<Boolean>("ShulkersOnly", true));
    private final Setting<Integer> range  = this.register(new Setting<Integer>("Range", 5, 0, 6));
    private final Setting<Integer> turns  = this.register(new Setting<Integer>("Turns", 1, 0, 5));
    private final Setting<Integer> ticks  = this.register(new Setting<Integer>("Ticks", 10, 1, 20));
    private int timeoutTicks = 0;

    public AutoBedDupe() {
        super("AutoBedDupe", "Best on 5b5t.org and 6b6t.org", Module.Category.MISC, true, false ,false);
    }
    
    @Subscribe
    public void onUpdate(TickEvent.Pre I) {
        if (mc.player != null && mc.world != null) {

            if (shulkersOnly.getValue()) {
                int shulker_slot = getShulkerSlot();
                if (shulker_slot != -1) {
                    mc.player.getInventory().selectedSlot = shulker_slot;
                }
            }

        for (int x = Math.max((int) mc.player.getX() - range.getValue(), 0); x < Math.min((int) mc.player.getX() + range.getValue(), 256); x++) {
            for (int y = Math.max((int) mc.player.getY() - range.getValue(), 0); y < Math.min((int) mc.player.getY() + range.getValue(), 256); y++) {
                for (int z = Math.max((int) mc.player.getZ() - range.getValue(), 0); z < Math.min((int) mc.player.getZ() + range.getValue(), 256); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = mc.world.getBlockState(pos);
                    if (state.getBlock() instanceof BedBlock) {
                        if (timeoutTicks >= ticks.getValue()) {
                            for (int i = 0; i < turns.getValue(); i++) {
                                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), Direction.DOWN, pos, true));
                            }
                            timeoutTicks = 0;
                        }
                        ++timeoutTicks;
                    }
                }
            }
        }
    }
}

    private int getShulkerSlot() {
        int shulker_slot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.player.getInventory().getStack(i);
            Item item = itemStack.getItem();
            if (
                item == Items.SHULKER_BOX &&
                item == Items.RED_SHULKER_BOX &&
                item == Items.BLUE_SHULKER_BOX &&
                item == Items.CYAN_SHULKER_BOX &&
                item == Items.GRAY_SHULKER_BOX &&
                item == Items.LIME_SHULKER_BOX &&
                item == Items.PINK_SHULKER_BOX &&
                item == Items.BLACK_SHULKER_BOX &&
                item == Items.BROWN_SHULKER_BOX &&
                item == Items.GREEN_SHULKER_BOX &&
                item == Items.WHITE_SHULKER_BOX &&
                item == Items.ORANGE_SHULKER_BOX &&
                item == Items.PURPLE_SHULKER_BOX &&
                item == Items.YELLOW_SHULKER_BOX &&
                item == Items.MAGENTA_SHULKER_BOX &&
                item == Items.LIGHT_BLUE_SHULKER_BOX &&
                item == Items.LIGHT_GRAY_SHULKER_BOX
                ) 
                shulker_slot = i;
        }
        return shulker_slot;
    }
}
