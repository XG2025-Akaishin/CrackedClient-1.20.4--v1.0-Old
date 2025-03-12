package me.alpha432.oyvey.mixin.flynew;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.authlib.GameProfile;

import me.alpha432.oyvey.features.modules.movement.FlightNew;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
	@Shadow
	private ClientPlayNetworkHandler networkHandler;
	
	public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}

	@Override
	protected float getOffGroundSpeed() {
		float speed = super.getOffGroundSpeed();
		if(FlightNew.getInstance().isEnabled()) {
			return (float)FlightNew.getInstance().getSpeed();
		}
		return speed;
	}
}
