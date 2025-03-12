package me.alpha432.oyvey.mixin.fakeplayerfuture;

import static me.alpha432.oyvey.util.traits.Util.EVENT_BUS;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.alpha432.oyvey.event.impl.fakeplayerfuture.AttackEntityEvent;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo info) {
        AttackEntityEvent ae = new AttackEntityEvent();
        EVENT_BUS.post(ae);
        if (ae.isCancelled()) info.cancel();
    }
}
