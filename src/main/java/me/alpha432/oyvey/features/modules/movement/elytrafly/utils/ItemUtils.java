package me.alpha432.oyvey.features.modules.movement.elytrafly.utils;

import net.minecraft.screen.slot.SlotActionType;
import static me.alpha432.oyvey.util.traits.Util.mc;

public class ItemUtils {
    public static void Move(int slot_from, int slot_to) {
        if (mc.player == null || mc.interactionManager == null) return;
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot_from, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot_to, 0, SlotActionType.PICKUP, mc.player);
    }
}