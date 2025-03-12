package me.alpha432.oyvey.mixin.norender;

import me.alpha432.oyvey.features.modules.render.norender.NoRender;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Entity.class)
public class MixinEntity {

	@Inject(method = "isOnFire", at = @At("HEAD"), cancellable = true)
	void isOnFireHook(CallbackInfoReturnable<Boolean> cir) {
		if (NoRender.getInstance().isOn() && NoRender.getInstance().fireEntity.getValue()) {
			cir.setReturnValue(false);
		}
	}
}
