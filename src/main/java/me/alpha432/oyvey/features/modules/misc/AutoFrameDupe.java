package me.alpha432.oyvey.features.modules.misc;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class AutoFrameDupe 
        extends Module {
    private final Setting<Boolean> shulkersOnly  = this.register(new Setting<Boolean>("ShulkersOnly", true));
    private final Setting<Integer> range  = this.register(new Setting<Integer>("Range", 5, 0, 6));
    private final Setting<Integer> turns  = this.register(new Setting<Integer>("Turns", 1, 0, 5));
    private final Setting<Integer> ticks  = this.register(new Setting<Integer>("Ticks", 10, 1, 20));
    private int timeoutTicks = 0;

    public AutoFrameDupe() {
        super("AutoFrameDupe", "Best on 5b5t.org and 6b6t.org", Module.Category.MISC, true, false ,false);
    }

    public void onUpdate() {
        if (mc.player != null && mc.world != null) {

            if (shulkersOnly.getValue()) {
                int shulker_slot = getShulkerSlot();
                if (shulker_slot != -1) {
                    mc.player.getInventory().selectedSlot = shulker_slot;
                }
            }

            for (Entity frame : mc.world.getEntities()) {
                if (frame instanceof ItemFrameEntity) {
                    if (mc.player.distanceTo(frame) <= range.getValue()) {
                        if (timeoutTicks >= ticks.getValue()) {
                            if (((ItemFrameEntity) frame).getHeldItemStack().isEmpty()) {
                                mc.interactionManager.interactEntity(mc.player, frame, Hand.MAIN_HAND);
                            }
                            if (!((ItemFrameEntity) frame).getHeldItemStack().isEmpty()) {
                                for (int i = 0; i < turns.getValue(); i++) {
                                    mc.interactionManager.interactEntity(mc.player, frame, Hand.MAIN_HAND);
                                }
                                mc.player.attack(frame);
                                mc.interactionManager.attackEntity(mc.player, frame);
                                timeoutTicks = 0;
                            }
                        }
                        ++timeoutTicks;
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
