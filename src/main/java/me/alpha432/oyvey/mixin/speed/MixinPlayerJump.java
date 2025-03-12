package me.alpha432.oyvey.mixin.speed;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import static me.alpha432.oyvey.util.traits.Util.EVENT_BUS;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.alpha432.oyvey.event.impl.speed.PlayerJumpEvent;

import static me.alpha432.oyvey.util.traits.Util.mc;

@Mixin({PlayerEntity.class})
public abstract class MixinPlayerJump extends LivingEntity {

    public MixinPlayerJump(World worldIn) {
        super(EntityType.PLAYER, worldIn);
    }

    @Inject(method = {"jump"}, at = {@At("HEAD")}, cancellable = true)
    private void jump(CallbackInfo callback) {
        if (mc.player == null) return;

        //SalHackMod.NORBIT_EVENT_BUS.post(new PlayerJumpEvent());
        PlayerJumpEvent event = new PlayerJumpEvent();
        EVENT_BUS.post(event);
    }
}