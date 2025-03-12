package me.alpha432.oyvey.mixin.chams;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.alpha432.oyvey.event.impl.chams.RenderEntityEvent;

import static me.alpha432.oyvey.util.traits.Util.EVENT_BUS;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> {

    @Shadow
    protected M model;
    @Shadow
    @Final
    protected List<FeatureRenderer<T, M>> features;

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet" +
            "/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/" +
            "render/VertexConsumerProvider;I)V", at = @At(value = "HEAD"), cancellable = true)
    private void hookRender(LivingEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
        RenderEntityEvent renderEntityEvent = new RenderEntityEvent(livingEntity, f, g, matrixStack, vertexConsumerProvider, i, model, features);
        EVENT_BUS.post(renderEntityEvent);
        if (renderEntityEvent.isCancelled()) {
            ci.cancel();
        }
    }
}
