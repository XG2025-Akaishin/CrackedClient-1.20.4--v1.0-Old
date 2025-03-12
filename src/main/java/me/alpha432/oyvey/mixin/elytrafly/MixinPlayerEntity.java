package me.alpha432.oyvey.mixin.elytrafly;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.alpha432.oyvey.event.impl.elytrafly.PlayerTravelEvent;
import static me.alpha432.oyvey.util.traits.Util.EVENT_BUS;

@Mixin({PlayerEntity.class})
public abstract class MixinPlayerEntity extends LivingEntity {

    public MixinPlayerEntity(World worldIn) {
        super(EntityType.PLAYER, worldIn);
    }

    @Inject(method = {"travel"}, at = {@At("HEAD")}, cancellable = true)
    private void travel(Vec3d movement, CallbackInfo info) {
        PlayerTravelEvent l_Event = new PlayerTravelEvent(movement.getX(), movement.getY(), movement.getZ());
        EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled()) info.cancel();
    }
}