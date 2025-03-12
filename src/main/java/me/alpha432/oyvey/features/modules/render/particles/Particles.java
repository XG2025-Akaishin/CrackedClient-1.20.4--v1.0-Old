package me.alpha432.oyvey.features.modules.render.particles;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.render.particles.Particles.ColorMode;
import me.alpha432.oyvey.features.settings.Setting;

import me.alpha432.oyvey.features.modules.render.particles.utils.MathUtility;
import me.alpha432.oyvey.features.modules.render.particles.utils.TextureUtility;
import me.alpha432.oyvey.features.modules.render.particles.utils.TextureOthers;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static me.alpha432.oyvey.features.modules.render.particles.utils.TextureUtility.*;

public class Particles extends Module {

    public Particles() {
        super("Particles", "Particles", Module.Category.RENDER, true, false, false);
    }

    private final Setting<Boolean> FireFlies = this.register(new Setting<>("FireFlies", true));
    private final Setting<Integer> ffcount = this.register(new Setting<>("FFCount", 30, 20, 200).withParent(FireFlies));
    private final Setting<Float> ffsize = this.register(new Setting<>("FFSize", 1f, 0.1f, 2.0f).withParent(FireFlies));
    private final Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.SnowFlake));
    private final Setting<Integer> count = this.register(new Setting<>("Count", 100, 20, 800));
    private final Setting<Float> size = this.register(new Setting<>("Size", 1f, 0.1f, 6.0f));
    public Setting<Integer> red = this.register(new Setting<>("Red", 1, 0, 255));
    public Setting<Integer> blue = this.register(new Setting<>("Blue", 1, 0, 255));
    public Setting<Integer> alpha = this.register(new Setting<>("Alpha", 1, 0, 255));
    public Setting<Integer> green = this.register(new Setting<>("Green", 1, 0, 255));

    public enum ColorMode {
        Custom, Sync
    }

    public enum Mode {
        Off, SnowFlake, Stars, Hearts, Dollars, Bloom;
    }


    private final ArrayList<ParticleBase> fireFlies = new ArrayList<>();
    private final ArrayList<ParticleBase> particles = new ArrayList<>();

    //@Override
    public void onUpdate() {
        fireFlies.removeIf(ParticleBase::tick);
        particles.removeIf(ParticleBase::tick);

        for (int i = fireFlies.size(); i < ffcount.getValue(); i++) {
            if (FireFlies.getValue()/*.isEnabled()*/)
                fireFlies.add(new FireFly(
                        (float) (mc.player.getX() + MathUtility.random(-25f, 25f)),
                        (float) (mc.player.getY() + MathUtility.random(2f, 15f)),
                        (float) (mc.player.getZ() + MathUtility.random(-25f, 25f)),
                        MathUtility.random(-0.2f, 0.2f),
                        MathUtility.random(-0.1f, 0.1f),
                        MathUtility.random(-0.2f, 0.2f)));
        }

        for (int j = particles.size(); j < count.getValue(); j++) {
            if (mode.getValue() != Mode.Off)
                particles.add(new ParticleBase(
                        (float) (mc.player.getX() + MathUtility.random(-48f, 48f)),
                        (float) (mc.player.getY() + MathUtility.random(2, 48f)),
                        (float) (mc.player.getZ() + MathUtility.random(-48f, 48f)),
                        MathUtility.random(-0.4f, 0.4f),
                        MathUtility.random(-0.1f, 0.1f),
                        MathUtility.random(-0.4f, 0.4f)));
        }
    }

    public void onPreRender3D(MatrixStack stack) {

        if(FireFlies.getValue()/*.isEnabled()*/) {
            stack.push();
            RenderSystem.setShaderTexture(0, firefly);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(() -> TEXTURE_COLOR_PROGRAM.backingProgram);
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            fireFlies.forEach(p -> p.render(bufferBuilder));
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            RenderSystem.depthMask(true);
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            stack.pop();
        }

        if(mode.getValue() != Mode.Off) {
            stack.push();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(() -> TEXTURE_COLOR_PROGRAM.backingProgram);
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            particles.forEach(p -> p.render(bufferBuilder));
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            RenderSystem.depthMask(true);
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            stack.pop();
        }
    }

    public class FireFly extends ParticleBase {
        private final List<Trail> trails = new ArrayList<>();


        public FireFly(float posX, float posY, float posZ, float motionX, float motionY, float motionZ) {
            super(posX, posY, posZ, motionX, motionY, motionZ);
        }

        //@Override
        public boolean tick() {

            if (mc.player.squaredDistanceTo(posX, posY, posZ) > 100) age -= 4;
            else if (!mc.world.getBlockState(new BlockPos((int) posX, (int) posY, (int) posZ)).isAir()) age -= 8;
            else age--;

            if (age < 0)
                return true;

            trails.removeIf(Trail::update);

            prevposX = posX;
            prevposY = posY;
            prevposZ = posZ;

            posX += motionX;
            posY += motionY;
            posZ += motionZ;

            trails.add(new Trail(new Vec3d(prevposX, prevposY, prevposZ), new Vec3d(posX, posY, posZ), /*lmode.getValue() == ColorMode.Sync ? HudEditor.getColor(age * 10) :*/ new Color(red.getValue(),green.getValue(),blue.getValue(),alpha.getValue())/*.getValue().getColorObject()*/));

            motionX *= 0.99f;
            motionY *= 0.99f;
            motionZ *= 0.99f;

            return false;
        }

        //@Override
        public void render(BufferBuilder bufferBuilder) {
            RenderSystem.setShaderTexture(0, firefly);
            if (!trails.isEmpty()) {
                Camera camera = mc.gameRenderer.getCamera();
                for (Trail ctx : trails) {
                    Vec3d pos = ctx.interpolate(1f);
                    MatrixStack matrices = new MatrixStack();
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
                    matrices.translate(pos.x, pos.y, pos.z);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                    Matrix4f matrix = matrices.peek().getPositionMatrix();

                    bufferBuilder.vertex(matrix, 0, -ffsize.getValue(), 0).texture(0f, 1f).color(TextureUtility.injectAlpha(ctx.color(), (int) (255 * ((float) age / (float) maxAge) * ctx.animation(mc.getTickDelta()))).getRGB()).next();
                    bufferBuilder.vertex(matrix, -ffsize.getValue(), -ffsize.getValue(), 0).texture(1f, 1f).color(TextureUtility.injectAlpha(ctx.color(), (int) (255 * ((float) age / (float) maxAge) * ctx.animation(mc.getTickDelta()))).getRGB()).next();
                    bufferBuilder.vertex(matrix, -ffsize.getValue(), 0, 0).texture(1f, 0).color(TextureUtility.injectAlpha(ctx.color(), (int) (255 * ((float) age / (float) maxAge) * ctx.animation(mc.getTickDelta()))).getRGB()).next();
                    bufferBuilder.vertex(matrix, 0, 0, 0).texture(0, 0).color(TextureUtility.injectAlpha(ctx.color(), (int) (255 * ((float) age / (float) maxAge) * ctx.animation(mc.getTickDelta()))).getRGB()).next();
                }
            }
        }
    }


    public class ParticleBase {

        protected float prevposX, prevposY, prevposZ, posX, posY, posZ, motionX, motionY, motionZ;
        protected int age, maxAge;

        public ParticleBase(float posX, float posY, float posZ, float motionX, float motionY, float motionZ) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            prevposX = posX;
            prevposY = posY;
            prevposZ = posZ;
            this.motionX = motionX;
            this.motionY = motionY;
            this.motionZ = motionZ;
            age = (int) MathUtility.random(100, 300);
            maxAge = age;
        }

        public boolean tick() {
            if (mc.player.squaredDistanceTo(posX, posY, posZ) > 4096) age -= 8;
            else age--;

            if (age < 0)
                return true;

            prevposX = posX;
            prevposY = posY;
            prevposZ = posZ;

            posX += motionX;
            posY += motionY;
            posZ += motionZ;

            motionX *= 0.9f;
            motionY *= 0.9f;
            motionZ *= 0.9f;

            motionY -= 0.001f;

            return false;
        }

        public void render(BufferBuilder bufferBuilder) {
            switch (mode.getValue()){
                case Bloom ->  RenderSystem.setShaderTexture(0, firefly);
                case SnowFlake ->  RenderSystem.setShaderTexture(0, snowflake);
                case Dollars ->  RenderSystem.setShaderTexture(0, dollar);
                case Hearts ->  RenderSystem.setShaderTexture(0, heart);
                case Stars ->  RenderSystem.setShaderTexture(0, star);
            }

            Camera camera = mc.gameRenderer.getCamera();
            Color color1 = /*lmode.getValue() == ColorMode.Sync : HudEditor.getColor(age * 2) :*/ new Color(red.getValue(),green.getValue(),blue.getValue(),alpha.getValue())/*.getValue().getColorObject()*/;
            Vec3d pos = TextureOthers.interpolatePos(prevposX, prevposY, prevposZ, posX, posY, posZ);

            MatrixStack matrices = new MatrixStack();
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
            matrices.translate(pos.x, pos.y, pos.z);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

            Matrix4f matrix1 = matrices.peek().getPositionMatrix();

            bufferBuilder.vertex(matrix1, 0, -size.getValue(), 0).texture(0f, 1f).color(TextureUtility.injectAlpha(color1, (int) (255 * ((float) age / (float) maxAge))).getRGB()).next();
            bufferBuilder.vertex(matrix1, -size.getValue(), -size.getValue(), 0).texture(1f, 1f).color(TextureUtility.injectAlpha(color1, (int) (255 * ((float) age / (float) maxAge))).getRGB()).next();
            bufferBuilder.vertex(matrix1, -size.getValue(), 0, 0).texture(1f, 0).color(TextureUtility.injectAlpha(color1, (int) (255 * ((float) age / (float) maxAge))).getRGB()).next();
            bufferBuilder.vertex(matrix1, 0, 0, 0).texture(0, 0).color(TextureUtility.injectAlpha(color1, (int) (255 * ((float) age / (float) maxAge))).getRGB()).next();
        }
    }
}
