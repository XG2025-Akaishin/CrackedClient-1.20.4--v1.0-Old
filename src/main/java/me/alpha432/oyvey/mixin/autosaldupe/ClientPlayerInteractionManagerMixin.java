package me.alpha432.oyvey.mixin.autosaldupe;

import me.alpha432.oyvey.features.modules.misc.autosaldupe.utils.IClientPlayerInteractionManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin implements IClientPlayerInteractionManager {
    @Shadow private int blockBreakingCooldown;

    @Shadow protected abstract void syncSelectedSlot();

    @Shadow
    public abstract void clickSlot(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player);

    @Shadow
    @Final
    private ClientPlayNetworkHandler networkHandler;

    @Override
    public void syncSelected() {
        syncSelectedSlot();
    }
}
