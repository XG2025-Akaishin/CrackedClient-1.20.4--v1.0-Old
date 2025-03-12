package me.alpha432.oyvey.mixin.norender;

import com.mojang.authlib.GameProfile;

import me.alpha432.oyvey.event.impl.norender.UpdateWalkingEvent;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.alpha432.oyvey.event.StageEvent;
import me.alpha432.oyvey.event.Stage;
import static me.alpha432.oyvey.util.traits.Util.EVENT_BUS;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

	public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}

	@Inject(method = {"sendMovementPackets"}, at = {@At(value = "HEAD")})
	private void preMotion(CallbackInfo info) {
		UpdateWalkingEvent event = new UpdateWalkingEvent(Stage.PRE);
		EVENT_BUS.post(event);
	}

	@Inject(method = {"sendMovementPackets"}, at = {@At(value = "RETURN")})
	private void postMotion(CallbackInfo info) {
		UpdateWalkingEvent event = new UpdateWalkingEvent(Stage.POST);
		EVENT_BUS.post(event);
	}
}
