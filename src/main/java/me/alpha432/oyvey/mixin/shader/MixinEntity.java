package me.alpha432.oyvey.mixin.shader;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import me.alpha432.oyvey.features.modules.render.shaders.Shaders;
import me.alpha432.oyvey.manager.shader.utilis.IEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(Entity.class)
public abstract class MixinEntity implements IEntity {

    @Shadow
    protected abstract BlockPos getVelocityAffectingPos();

    @Shadow
    private Box boundingBox;


    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    public void isGlowingHook(CallbackInfoReturnable<Boolean> cir) {
        if (Shaders.getInstance().isEnabled()) {
            cir.setReturnValue(Shaders.getInstance().shouldRender((Entity) (Object) this));
        }
    }
}