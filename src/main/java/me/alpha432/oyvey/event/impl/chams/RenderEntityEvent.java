package me.alpha432.oyvey.event.impl.chams;


import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

import java.util.List;

import me.alpha432.oyvey.event.Event;

public class RenderEntityEvent<T extends LivingEntity> extends Event {
    public final LivingEntity entity;

    public final float f;
    public final float g;
    public final MatrixStack matrixStack;
    public final VertexConsumerProvider vertexConsumerProvider;
    public final int i;
    public final EntityModel model;
    //
    public final List<FeatureRenderer<T, EntityModel<T>>> features;

    /**
     *
     * @param entity
     * @param f
     * @param g
     * @param matrixStack
     * @param vertexConsumerProvider
     * @param i
     * @param model
     * @param features
     */
    public RenderEntityEvent(LivingEntity entity, float f, float g,
                             MatrixStack matrixStack,
                             VertexConsumerProvider vertexConsumerProvider,
                             int i, EntityModel model,
                             List<FeatureRenderer<T, EntityModel<T>>> features)
    {
        this.entity = entity;
        this.f = f;
        this.g = g;
        this.matrixStack = matrixStack;
        this.vertexConsumerProvider = vertexConsumerProvider;
        this.i = i;
        this.model = model;
        this.features = features;
    }
}
