package me.alpha432.oyvey.features.modules.render.holesp.utilshole;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static com.mojang.blaze3d.systems.RenderSystem.disableBlend;
import static me.alpha432.oyvey.features.modules.render.holesp.utilshole.Util.mc;
import com.mojang.blaze3d.systems.RenderSystem;

import me.alpha432.oyvey.features.modules.render.holesp.HoleESP;
import me.alpha432.oyvey.manager.ModuleManager;

public class Render3DEngine {

    public static List<FillAction> FILLED_QUEUE = new ArrayList<>();
    public static List<OutlineAction> OUTLINE_QUEUE = new ArrayList<>();
    public static List<FadeAction> FADE_QUEUE = new ArrayList<>();
    public static List<FillSideAction> FILLED_SIDE_QUEUE = new ArrayList<>();
    public static List<OutlineSideAction> OUTLINE_SIDE_QUEUE = new ArrayList<>();

    public record FillAction(Box box, Color color) {
    }

    public record OutlineAction(Box box, Color color, float lineWidth) {
    }

    public record FadeAction(Box box, Color color, Color color2) {
    }

    public record FillSideAction(Box box, Color color, Direction side) {
    }

    public record OutlineSideAction(Box box, Color color, float lineWidth, Direction side) {
    }


    public static void onRender3D(MatrixStack stack) {
        if (!FILLED_QUEUE.isEmpty() || !FADE_QUEUE.isEmpty() || !FILLED_SIDE_QUEUE.isEmpty()) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            FILLED_QUEUE.forEach(action -> setFilledBoxVertexes(bufferBuilder, stack.peek().getPositionMatrix(), action.box(), action.color()));

            FADE_QUEUE.forEach(action -> setFilledFadePoints(action.box(), bufferBuilder, stack.peek().getPositionMatrix(), action.color(), action.color2()));

            FILLED_SIDE_QUEUE.forEach(action -> setFilledSidePoints(bufferBuilder, stack.peek().getPositionMatrix(), action.box, action.color(), action.side()));

            tessellator.draw();
            RenderSystem.disableBlend();

            FADE_QUEUE.clear();
            FILLED_SIDE_QUEUE.clear();
            FILLED_QUEUE.clear();
        }
    
        if (!OUTLINE_QUEUE.isEmpty() || !OUTLINE_SIDE_QUEUE.isEmpty()) {
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

            OUTLINE_SIDE_QUEUE.forEach(action -> {
                setSideOutlinePoints(action.box, matrixFrom(action.box().minX, action.box().minY, action.box().minZ), buffer, action.color(), action.side());
            });

            tessellator.draw();
            RenderSystem.enableCull();
            cleanup();
            OUTLINE_QUEUE.clear();
            OUTLINE_SIDE_QUEUE.clear();
        }
    }

    public static void setFilledSidePoints(BufferBuilder buffer, Matrix4f matrix, Box box, Color c, Direction dir) {
        float minX = (float) (box.minX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float minY = (float) (box.minY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float minZ = (float) (box.minZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());
        float maxX = (float) (box.maxX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float maxY = (float) (box.maxY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float maxZ = (float) (box.maxZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());

        if (dir == Direction.DOWN) {
            buffer.vertex(matrix, minX, minY, minZ).color(c.getRGB()).next();
            buffer.vertex(matrix, maxX, minY, minZ).color(c.getRGB()).next();
            buffer.vertex(matrix, maxX, minY, maxZ).color(c.getRGB()).next();
            buffer.vertex(matrix, minX, minY, maxZ).color(c.getRGB()).next();
        }

        if (dir == Direction.NORTH) {
            buffer.vertex(matrix, minX, minY, minZ).color(c.getRGB()).next();
            buffer.vertex(matrix, minX, maxY, minZ).color(c.getRGB()).next();
            buffer.vertex(matrix, maxX, maxY, minZ).color(c.getRGB()).next();
            buffer.vertex(matrix, maxX, minY, minZ).color(c.getRGB()).next();
        }

        if (dir == Direction.EAST) {
            buffer.vertex(matrix, maxX, minY, minZ).color(c.getRGB()).next();
            buffer.vertex(matrix, maxX, maxY, minZ).color(c.getRGB()).next();
            buffer.vertex(matrix, maxX, maxY, maxZ).color(c.getRGB()).next();
            buffer.vertex(matrix, maxX, minY, maxZ).color(c.getRGB()).next();
        }
        if (dir == Direction.SOUTH) {
            buffer.vertex(matrix, minX, minY, maxZ).color(c.getRGB()).next();
            buffer.vertex(matrix, maxX, minY, maxZ).color(c.getRGB()).next();
            buffer.vertex(matrix, maxX, maxY, maxZ).color(c.getRGB()).next();
            buffer.vertex(matrix, minX, maxY, maxZ).color(c.getRGB()).next();
        }

        if (dir == Direction.WEST) {
            buffer.vertex(matrix, minX, minY, minZ).color(c.getRGB()).next();
            buffer.vertex(matrix, minX, minY, maxZ).color(c.getRGB()).next();
            buffer.vertex(matrix, minX, maxY, maxZ).color(c.getRGB()).next();
            buffer.vertex(matrix, minX, maxY, minZ).color(c.getRGB()).next();
        }

        if (dir == Direction.UP) {
            buffer.vertex(matrix, minX, maxY, minZ).color(c.getRGB()).next();
            buffer.vertex(matrix, minX, maxY, maxZ).color(c.getRGB()).next();
            buffer.vertex(matrix, maxX, maxY, maxZ).color(c.getRGB()).next();
            buffer.vertex(matrix, maxX, maxY, minZ).color(c.getRGB()).next();
        }
    }


    public static void setFilledFadePoints(Box box, BufferBuilder buffer, Matrix4f posMatrix, Color c, Color c1) {
        float minX = (float) (box.minX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float minY = (float) (box.minY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float minZ = (float) (box.minZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());
        float maxX = (float) (box.maxX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float maxY = (float) (box.maxY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float maxZ = (float) (box.maxZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());

        if (HoleESP.getInstance().culling.getValue())
            RenderSystem.enableCull();

        buffer.vertex(posMatrix, minX, minY, minZ).color(c.getRGB()).next();
        buffer.vertex(posMatrix, minX, maxY, minZ).color(c1.getRGB()).next();
        buffer.vertex(posMatrix, maxX, maxY, minZ).color(c1.getRGB()).next();
        buffer.vertex(posMatrix, maxX, minY, minZ).color(c.getRGB()).next();

        buffer.vertex(posMatrix, maxX, minY, minZ).color(c.getRGB()).next();
        buffer.vertex(posMatrix, maxX, maxY, minZ).color(c1.getRGB()).next();
        buffer.vertex(posMatrix, maxX, maxY, maxZ).color(c1.getRGB()).next();
        buffer.vertex(posMatrix, maxX, minY, maxZ).color(c.getRGB()).next();

        buffer.vertex(posMatrix, minX, minY, maxZ).color(c.getRGB()).next();
        buffer.vertex(posMatrix, maxX, minY, maxZ).color(c.getRGB()).next();
        buffer.vertex(posMatrix, maxX, maxY, maxZ).color(c1.getRGB()).next();
        buffer.vertex(posMatrix, minX, maxY, maxZ).color(c1.getRGB()).next();

        buffer.vertex(posMatrix, minX, minY, minZ).color(c.getRGB()).next();
        buffer.vertex(posMatrix, minX, minY, maxZ).color(c.getRGB()).next();
        buffer.vertex(posMatrix, minX, maxY, maxZ).color(c1.getRGB()).next();
        buffer.vertex(posMatrix, minX, maxY, minZ).color(c1.getRGB()).next();

        buffer.vertex(posMatrix, minX, maxY, minZ).color(c1.getRGB()).next();
        buffer.vertex(posMatrix, minX, maxY, maxZ).color(c1.getRGB()).next();
        buffer.vertex(posMatrix, maxX, maxY, maxZ).color(c1.getRGB()).next();
        buffer.vertex(posMatrix, maxX, maxY, minZ).color(c1.getRGB()).next();

        if (HoleESP.getInstance().culling.getValue())
            RenderSystem.disableCull();
    }

    public static void setSideOutlinePoints(Box box, MatrixStack matrices, BufferBuilder buffer, Color color, Direction dir) {
        box = box.offset(new Vec3d(box.minX, box.minY, box.minZ).negate());

        float x1 = (float) box.minX;
        float y1 = (float) box.minY;
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float y2 = (float) box.maxY;
        float z2 = (float) box.maxZ;

        switch (dir) {
            case UP -> {
                vertexLine(matrices, buffer, x1, y2, z1, x2, y2, z1, color);
                vertexLine(matrices, buffer, x2, y2, z1, x2, y2, z2, color);
                vertexLine(matrices, buffer, x2, y2, z2, x1, y2, z2, color);
                vertexLine(matrices, buffer, x1, y2, z2, x1, y2, z1, color);
            }
            case DOWN -> {
                vertexLine(matrices, buffer, x1, y1, z1, x2, y1, z1, color);
                vertexLine(matrices, buffer, x2, y1, z1, x2, y1, z2, color);
                vertexLine(matrices, buffer, x2, y1, z2, x1, y1, z2, color);
                vertexLine(matrices, buffer, x1, y1, z2, x1, y1, z1, color);
            }
            case EAST -> {
                vertexLine(matrices, buffer, x2, y1, z1, x2, y2, z1, color);
                vertexLine(matrices, buffer, x2, y1, z2, x2, y2, z2, color);
                vertexLine(matrices, buffer, x2, y2, z2, x2, y2, z1, color);
                vertexLine(matrices, buffer, x2, y1, z2, x2, y1, z1, color);
            }
            case WEST -> {
                vertexLine(matrices, buffer, x1, y1, z1, x1, y2, z1, color);
                vertexLine(matrices, buffer, x1, y1, z2, x1, y2, z2, color);
                vertexLine(matrices, buffer, x1, y2, z2, x1, y2, z1, color);
                vertexLine(matrices, buffer, x1, y1, z2, x1, y1, z1, color);
            }
            case NORTH -> {
                vertexLine(matrices, buffer, x2, y1, z1, x2, y2, z1, color);
                vertexLine(matrices, buffer, x1, y1, z1, x1, y2, z1, color);
                vertexLine(matrices, buffer, x2, y1, z1, x1, y1, z1, color);
                vertexLine(matrices, buffer, x2, y2, z1, x1, y2, z1, color);
            }
            case SOUTH -> {
                vertexLine(matrices, buffer, x1, y1, z2, x1, y2, z2, color);
                vertexLine(matrices, buffer, x2, y1, z2, x2, y2, z2, color);
                vertexLine(matrices, buffer, x1, y1, z2, x2, y1, z2, color);
                vertexLine(matrices, buffer, x1, y2, z2, x2, y2, z2, color);
            }
        }
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




    public static void drawHoleOutline(@NotNull Box box, Color color, float lineWidth) {
        setup();
        MatrixStack matrices = matrixFrom(box.minX, box.minY, box.minZ);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        RenderSystem.lineWidth(lineWidth);
        buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);

        box = box.offset(new Vec3d(box.minX, box.minY, box.minZ).negate());

        float x1 = (float) box.minX;
        float y1 = (float) box.minY;
        float y2 = (float) box.maxY;
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float z2 = (float) box.maxZ;

        vertexLine(matrices, buffer, x1, y1, z1, x2, y1, z1, color);
        vertexLine(matrices, buffer, x2, y1, z1, x2, y1, z2, color);
        vertexLine(matrices, buffer, x2, y1, z2, x1, y1, z2, color);
        vertexLine(matrices, buffer, x1, y1, z2, x1, y1, z1, color);

        vertexLine(matrices, buffer, x1, y1, z1, x1, y2, z1, color);
        vertexLine(matrices, buffer, x2, y1, z2, x2, y2, z2, color);
        vertexLine(matrices, buffer, x1, y1, z2, x1, y2, z2, color);
        vertexLine(matrices, buffer, x2, y1, z1, x2, y2, z1, color);

        tessellator.draw();
        RenderSystem.enableCull();
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
