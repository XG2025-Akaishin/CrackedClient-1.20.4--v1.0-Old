package me.alpha432.oyvey.features.modules.render.chams;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

import me.alpha432.oyvey.event.impl.chams.RenderCrystalEvent;
import me.alpha432.oyvey.event.impl.chams.RenderEntityEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.render.cameraclip.NoCameraClip;
import me.alpha432.oyvey.features.modules.render.chams.util.EntityUtil;
import me.alpha432.oyvey.features.settings.Setting;
import java.awt.*;

public class Chams extends Module {
    private static Chams INSTANCE = new Chams();
    public Setting<ChamsMode> modeConfig = this.register(new Setting<ChamsMode>("Mode", ChamsMode.NORMAL));
    public Setting<Boolean> handsConfig = this.register(new Setting<Boolean>("Hands", true));
    public Setting<Boolean> selfConfig = this.register(new Setting<Boolean>("Self", true));
    public Setting<Boolean> playersConfig = this.register(new Setting<Boolean>("Players", true));
    public Setting<Boolean> monstersConfig = this.register(new Setting<Boolean>("Monsters", true));
    public Setting<Boolean> animalsConfig = this.register(new Setting<Boolean>("Animals", true));
    public Setting<Boolean> otherConfig = this.register(new Setting<Boolean>("Others", true));
    public Setting<Boolean> invisiblesConfig = this.register(new Setting<Boolean>("Invisibles", true));

    public Setting<Integer> r = this.register(new Setting<Integer>("Red", 1, 0, 255));
    public Setting<Integer> g = this.register(new Setting<Integer>("Green", 210, 0, 255));
    public Setting<Integer> b = this.register(new Setting<Integer>("Blue", 1, 0, 255));
    public Setting<Integer> a = this.register(new Setting<Integer>("Alpha", 60, 0, 255));
    // Config<Boolean> textureConfig = new BooleanConfig("Texture", "Renders" +
    //        " the entity texture beneath the chams", false);


    public Chams() {
        super("Chams", "Renders entity models through walls", Module.Category.RENDER, true, false, false);
        this.setInstance();
    }

    public static Chams getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Chams();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    //@EventListener
    @Subscribe
    public void onRenderEntity(RenderEntityEvent event) {
        if (!checkChams(event.entity)) {
            return;
        }
        RenderSystem.enableBlend();
        // RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexConsumer = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        RenderSystem.lineWidth(2.0f);
        vertexConsumer.begin(modeConfig.getValue() == ChamsMode.NORMAL ? VertexFormat.DrawMode.QUADS :
                VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);
        Color color = new Color(r.getValue(),g.getValue(),b.getValue(),a.getValue());//Modules.COLORS.getColor();
        float n;
        Direction direction;
        event.matrixStack.push();
        RenderSystem.setShaderColor(color.getRed() / 255.0f, color.getGreen() / 255.0f,
                color.getBlue() / 255.0f, 60 / 255.0f);
        event.model.handSwingProgress = event.entity.getHandSwingProgress(event.g);
        event.model.riding = ((Entity) event.entity).hasVehicle();
        event.model.child = ((LivingEntity) event.entity).isBaby();
        float h = MathHelper.lerpAngleDegrees(event.g, ((LivingEntity) event.entity).prevBodyYaw, ((LivingEntity) event.entity).bodyYaw);
        float j = MathHelper.lerpAngleDegrees(event.g, ((LivingEntity) event.entity).prevHeadYaw, ((LivingEntity) event.entity).headYaw);
        float k = j - h;
        if (((Entity) event.entity).hasVehicle() && ((Entity) event.entity).getVehicle() instanceof LivingEntity) {
            LivingEntity livingEntity2 = (LivingEntity)((Entity) event.entity).getVehicle();
            h = MathHelper.lerpAngleDegrees(event.g, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
            k = j - h;
            float l = MathHelper.wrapDegrees(k);
            if (l < -85.0f) {
                l = -85.0f;
            }
            if (l >= 85.0f) {
                l = 85.0f;
            }
            h = j - l;
            if (l * l > 2500.0f) {
                h += l * 0.2f;
            }
            k = j - h;
        }
        float m = MathHelper.lerp(event.g, ((LivingEntity) event.entity).prevPitch, ((Entity) event.entity).getPitch());
        if (LivingEntityRenderer.shouldFlipUpsideDown(event.entity)) {
            m *= -1.0f;
            k *= -1.0f;
        }
        if (((Entity) event.entity).isInPose(EntityPose.SLEEPING) && (direction = ((LivingEntity) event.entity).getSleepingDirection()) != null) {
            n = ((Entity) event.entity).getEyeHeight(EntityPose.STANDING) - 0.1f;
            event.matrixStack.translate((float)(-direction.getOffsetX()) * n, 0.0f, (float)(-direction.getOffsetZ()) * n);
        }
        float l = event.entity.age + event.g;
        setupTransforms(event.entity, event.matrixStack, l, h, event.g);
        event.matrixStack.scale(-1.0f, -1.0f, 1.0f);
        event.matrixStack.scale(0.9375f, 0.9375f, 0.9375f);
        event.matrixStack.translate(0.0f, -1.501f, 0.0f);
        n = 0.0f;
        float o = 0.0f;
        if (!((Entity) event.entity).hasVehicle() && ((LivingEntity) event.entity).isAlive()) {
            n = ((LivingEntity) event.entity).limbAnimator.getSpeed(event.g);
            o = ((LivingEntity) event.entity).limbAnimator.getPos(event.g);
            if (((LivingEntity) event.entity).isBaby()) {
                o *= 3.0f;
            }
            if (n > 1.0f) {
                n = 1.0f;
            }
        }
        event.model.animateModel(event.entity, o, n, event.g);
        event.model.setAngles(event.entity, o, n, l, k, m);
        boolean bl = !event.entity.isInvisible();
        boolean bl2 = !bl && !((Entity) event.entity).isInvisibleTo(mc.player);
        int p = LivingEntityRenderer.getOverlay(event.entity, 0);
        event.model.render(event.matrixStack, vertexConsumer, event.i, p, 1.0f, 1.0f, 1.0f, bl2 ? 0.15f : 1.0f);
        if (!((Entity) event.entity).isSpectator())
        {
            for (Object featureRenderer : event.features)
            {
                ((FeatureRenderer) featureRenderer).render(event.matrixStack, event.vertexConsumerProvider, event.i,
                        event.entity, o, n, event.g, l, k, m);
            }
        }
        tessellator.draw();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        event.matrixStack.pop();
        event.cancel();
    }

    protected void setupTransforms(LivingEntity entity, MatrixStack matrices, float animationProgress,
                                   float bodyYaw, float tickDelta)
    {
        if (entity.isFrozen()) {
            bodyYaw += (float)(Math.cos((double)((LivingEntity) entity).age * 3.25) * Math.PI * (double)0.4f);
        }
        if (!((Entity) entity).isInPose(EntityPose.SLEEPING)) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - bodyYaw));
        }
        if (((LivingEntity) entity).deathTime > 0) {
            float f = ((float)((LivingEntity) entity).deathTime + tickDelta - 1.0f) / 20.0f * 1.6f;
            if ((f = MathHelper.sqrt(f)) > 1.0f) {
                f = 1.0f;
            }
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * 90.0f));
        } else if (((LivingEntity) entity).isUsingRiptide()) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0f - ((Entity) entity).getPitch()));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(((float)((LivingEntity) entity).age + tickDelta) * -75.0f));
        } else if (((Entity) entity).isInPose(EntityPose.SLEEPING)) {
            Direction direction = ((LivingEntity) entity).getSleepingDirection();
            float g = direction != null ? getYaw(direction) : bodyYaw;
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0f));
        } else if (LivingEntityRenderer.shouldFlipUpsideDown(entity)) {
            matrices.translate(0.0f, ((Entity)entity).getHeight() + 0.1f, 0.0f);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
        }
    }

    private static float getYaw(Direction direction) {
        switch (direction) {
            case SOUTH: {
                return 90.0f;
            }
            case WEST: {
                return 0.0f;
            }
            case NORTH: {
                return 270.0f;
            }
            case EAST: {
                return 180.0f;
            }
        }
        return 0.0f;
    }

    private static final float SINE_45_DEGREES = (float) Math.sin(0.7853981633974483);

    //@EventListener
    @Subscribe
    public void onRenderCrystal(RenderCrystalEvent event) {
        if (!otherConfig.getValue())
        {
            return;
        }
        RenderSystem.enableBlend();
        // RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        event.matrixStack.push();
        float h = EndCrystalEntityRenderer.getYOffset(event.endCrystalEntity, event.g);
        float j = ((float) event.endCrystalEntity.endCrystalAge + event.g) * 3.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexConsumer = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        RenderSystem.lineWidth(2.0f);
        vertexConsumer.begin(modeConfig.getValue() == ChamsMode.NORMAL ? VertexFormat.DrawMode.QUADS :
                VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);
        event.matrixStack.push();
        Color color = new Color(r.getValue(),g.getValue(),b.getValue(),a.getValue());//Modules.COLORS.getColor();
        RenderSystem.setShaderColor(color.getRed() / 255.0f, color.getGreen() / 255.0f,
                color.getBlue() / 255.0f, 60 / 255.0f);
        event.matrixStack.scale(2.0f, 2.0f, 2.0f);
        event.matrixStack.translate(0.0f, -0.5f, 0.0f);
        int k = OverlayTexture.DEFAULT_UV;
        event.matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        event.matrixStack.translate(0.0f, 1.5f + h / 2.0f, 0.0f);
        event.matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, SINE_45_DEGREES, 0.0f, SINE_45_DEGREES));
        event.frame.render(event.matrixStack, vertexConsumer, event.i, k);
        float l = 0.875f;
        event.matrixStack.scale(0.875f, 0.875f, 0.875f);
        event.matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, SINE_45_DEGREES, 0.0f, SINE_45_DEGREES));
        event.matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        event.frame.render(event.matrixStack, vertexConsumer, event.i, k);
        event.matrixStack.scale(0.875f, 0.875f, 0.875f);
        event.matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, SINE_45_DEGREES, 0.0f, SINE_45_DEGREES));
        event.matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        event.core.render(event.matrixStack, vertexConsumer, event.i, k);
        event.matrixStack.pop();
        event.matrixStack.pop();
        tessellator.draw();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        event.cancel();
    }

    private boolean checkChams(LivingEntity entity) {
        if (entity instanceof PlayerEntity && playersConfig.getValue()) {
            return selfConfig.getValue() || entity != mc.player;
        }
        return (!entity.isInvisible() || invisiblesConfig.getValue()) && (EntityUtil.isMonster(entity) && monstersConfig.getValue() || (EntityUtil.isNeutral(entity) || EntityUtil.isPassive(entity)) && animalsConfig.getValue());
    }

    public enum ChamsMode {
        NORMAL,
        WIREFRAME
    }
}
