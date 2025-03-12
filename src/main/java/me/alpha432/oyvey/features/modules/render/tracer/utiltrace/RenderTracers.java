package me.alpha432.oyvey.features.modules.render.tracer.utiltrace;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;

import static com.mojang.blaze3d.systems.RenderSystem.disableBlend;

public class RenderTracers {

    public static void drawLineDebug(@NotNull Vec3d vec1, @NotNull Vec3d vec2, Color color, float width) {
        drawLine(vec1.x, vec1.y, vec1.z, vec2.x, vec2.y, vec2.z, color, width);
    }

    public static void drawLineDebug(double x1, double y1, double z1, double x2, double y2, double z2, Color color, float width) {
        setup();
        MatrixStack matrices = matrixFrom(x1, y1, z1);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        RenderSystem.lineWidth(width);
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.LINES);
        vertexLine(matrices, buffer, 0f, 0f, 0f, (float) (x2 - x1), (float) (y2 - y1), (float) (z2 - z1), color);
        tessellator.draw();
        RenderSystem.enableCull();
        RenderSystem.lineWidth(1f);
        cleanup();
    }

    public static void drawLine(@NotNull Vec3d vec1, @NotNull Vec3d vec2, Color color, float width) {
        drawLine(vec1.x, vec1.y, vec1.z, vec2.x, vec2.y, vec2.z, color, width);
    }

    public static void drawLine(double x1, double y1, double z1, double x2, double y2, double z2, Color color, float width) {
        setup();
        MatrixStack matrices = matrixFrom(x1, y1, z1);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        RenderSystem.lineWidth(width);
        buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        vertexLine(matrices, buffer, 0f, 0f, 0f, (float) (x2 - x1), (float) (y2 - y1), (float) (z2 - z1), color);
        tessellator.draw();
        RenderSystem.enableCull();
        RenderSystem.lineWidth(1f);
        cleanup();
    }

    public static void setup() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public static @NotNull MatrixStack matrixFrom(double x, double y, double z) {
        MatrixStack matrices = new MatrixStack();

        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));

        matrices.translate(x - camera.getPos().x, y - camera.getPos().y, z - camera.getPos().z);

        return matrices;
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
    public static void cleanup() {
        disableBlend();
    }
}
