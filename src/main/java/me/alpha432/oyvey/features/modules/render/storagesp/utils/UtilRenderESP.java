package me.alpha432.oyvey.features.modules.render.storagesp.utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.*;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;

import static com.mojang.blaze3d.systems.RenderSystem.disableBlend;
import static me.alpha432.oyvey.features.modules.render.storagesp.utils.UtilMCI.mc;

public class UtilRenderESP {


    public static void onRender3D(MatrixStack stack) {
        if (!FILLED_QUEUE.isEmpty() ) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            FILLED_QUEUE.forEach(action -> setFilledBoxVertexes(bufferBuilder, stack.peek().getPositionMatrix(), action.box(), action.color()));

            tessellator.draw();
            RenderSystem.disableBlend();

            FILLED_QUEUE.clear();
        }

        if (!OUTLINE_QUEUE.isEmpty()) {
            setup();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            RenderSystem.disableCull();
            RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
            buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);

            RenderSystem.lineWidth(2f);

            OUTLINE_QUEUE.forEach(action -> {
                setOutlinePoints(action.box(), matrixFrom(action.box().minX, action.box().minY, action.box().minZ), buffer, action.color());
            });

            tessellator.draw();
            RenderSystem.enableCull();
            cleanup();
            OUTLINE_QUEUE.clear();
        }
    }

    public static void setup() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public static void cleanup() {
        disableBlend();
    }

    //Fill
    public static List<FillAction> FILLED_QUEUE = new ArrayList<>();
    @Deprecated
    @SuppressWarnings("unused")
    public static void drawFilledBox(MatrixStack stack, Box box, Color c) {
        FILLED_QUEUE.add(new FillAction(box, c));
    }
    public record FillAction(Box box, Color color) {
    }
    //Outline
    public static List<OutlineAction> OUTLINE_QUEUE = new ArrayList<>();
    @Deprecated
    public static void drawBoxOutline(@NotNull Box box, Color color, float lineWidth) {
        OUTLINE_QUEUE.add(new OutlineAction(box, color, lineWidth));
    }
    public record OutlineAction(Box box, Color color, float lineWidth) {
    }

    public static void setFilledBoxVertexes(@NotNull BufferBuilder bufferBuilder, Matrix4f m, @NotNull Box box, @NotNull Color c) {
        float minX = (float) (box.minX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float minY = (float) (box.minY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float minZ = (float) (box.minZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());
        float maxX = (float) (box.maxX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float maxY = (float) (box.maxY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float maxZ = (float) (box.maxZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());

        bufferBuilder.vertex(m, minX, minY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, maxX, minY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, maxX, minY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, minX, minY, maxZ).color(c.getRGB()).next();

        bufferBuilder.vertex(m, minX, minY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, minX, maxY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, maxX, maxY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, maxX, minY, minZ).color(c.getRGB()).next();

        bufferBuilder.vertex(m, maxX, minY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, maxX, maxY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, maxX, maxY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, maxX, minY, maxZ).color(c.getRGB()).next();

        bufferBuilder.vertex(m, minX, minY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, maxX, minY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, maxX, maxY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, minX, maxY, maxZ).color(c.getRGB()).next();

        bufferBuilder.vertex(m, minX, minY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, minX, minY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, minX, maxY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, minX, maxY, minZ).color(c.getRGB()).next();

        bufferBuilder.vertex(m, minX, maxY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, minX, maxY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, maxX, maxY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(m, maxX, maxY, minZ).color(c.getRGB()).next();
    }
    public static @NotNull MatrixStack matrixFrom(double x, double y, double z) {
        MatrixStack matrices = new MatrixStack();

        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));

        matrices.translate(x - camera.getPos().x, y - camera.getPos().y, z - camera.getPos().z);

        return matrices;
    }
    public static void setOutlinePoints(Box box, MatrixStack matrices, BufferBuilder buffer, Color color) {
        box = box.offset(new Vec3d(box.minX, box.minY, box.minZ).negate());

        float x1 = (float) box.minX;
        float y1 = (float) box.minY;
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float y2 = (float) box.maxY;
        float z2 = (float) box.maxZ;

        vertexLine(matrices, buffer, x1, y1, z1, x2, y1, z1, color);
        vertexLine(matrices, buffer, x2, y1, z1, x2, y1, z2, color);
        vertexLine(matrices, buffer, x2, y1, z2, x1, y1, z2, color);
        vertexLine(matrices, buffer, x1, y1, z2, x1, y1, z1, color);
        vertexLine(matrices, buffer, x1, y1, z2, x1, y2, z2, color);
        vertexLine(matrices, buffer, x1, y1, z1, x1, y2, z1, color);
        vertexLine(matrices, buffer, x2, y1, z2, x2, y2, z2, color);
        vertexLine(matrices, buffer, x2, y1, z1, x2, y2, z1, color);
        vertexLine(matrices, buffer, x1, y2, z1, x2, y2, z1, color);
        vertexLine(matrices, buffer, x2, y2, z1, x2, y2, z2, color);
        vertexLine(matrices, buffer, x2, y2, z2, x1, y2, z2, color);
        vertexLine(matrices, buffer, x1, y2, z2, x1, y2, z1, color);
    }
    public static void vertexLine(@NotNull MatrixStack matrices, @NotNull VertexConsumer buffer, float x1, float y1, float z1, float x2, float y2, float z2, @NotNull Color lineColor) {
        Matrix4f model = matrices.peek().getPositionMatrix();
        Matrix3f normal = matrices.peek().getNormalMatrix();
        Vector3f normalVec = getNormal(x1, y1, z1, x2, y2, z2);
        buffer.vertex(model, x1, y1, z1).color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), lineColor.getAlpha()).normal(normal, normalVec.x(), normalVec.y(), normalVec.z()).next();
        buffer.vertex(model, x2, y2, z2).color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), lineColor.getAlpha()).normal(normal, normalVec.x(), normalVec.y(), normalVec.z()).next();
    }
    public static @NotNull Vector3f getNormal(float x1, float y1, float z1, float x2, float y2, float z2) {
        float xNormal = x2 - x1;
        float yNormal = y2 - y1;
        float zNormal = z2 - z1;
        float normalSqrt = MathHelper.sqrt(xNormal * xNormal + yNormal * yNormal + zNormal * zNormal);

        return new Vector3f(xNormal / normalSqrt, yNormal / normalSqrt, zNormal / normalSqrt);
    }

}
