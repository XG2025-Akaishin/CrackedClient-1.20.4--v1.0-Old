package me.alpha432.oyvey.mixin.timer;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.alpha432.oyvey.features.modules.misc.Timer;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.render.RenderTickCounter;

@Mixin(RenderTickCounter.class)
public class RenderTickCounterMixin {
	@Shadow
	private float lastFrameDuration;
	

	@Inject(at = {@At(value = "FIELD", target = "Lnet/minecraft/client/render/RenderTickCounter;prevTimeMillis:J", 
			opcode = Opcodes.PUTFIELD, ordinal = 0) }, method = {"beginRenderTick(J)I" })
	public void onBeginRenderTick(long long_1, CallbackInfoReturnable<Integer> cir) {
		if(Timer.getInstance().isEnabled()) {
			lastFrameDuration *= Timer.getInstance().getMultiplier();
		}
	}
}
