package me.alpha432.oyvey.features.modules.render.itemesp.utils;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import static me.alpha432.oyvey.features.modules.render.itemesp.utils.Util.mc;
import static com.mojang.blaze3d.systems.RenderSystem.disableBlend;

public class RenderUtils {
    
    public static final Matrix4f lastProjMat = new Matrix4f();
    public static final Matrix4f lastModMat = new Matrix4f();
    public static final Matrix4f lastWorldSpaceMatrix = new Matrix4f();

    public static @NotNull Vec3d worldSpaceToScreenSpace(@NotNull Vec3d pos) {
        Camera camera = mc.getEntityRenderDispatcher().camera;
        int displayHeight = mc.getWindow().getHeight();
        int[] viewport = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
        Vector3f target = new Vector3f();

        double deltaX = pos.x - camera.getPos().x;
        double deltaY = pos.y - camera.getPos().y;
        double deltaZ = pos.z - camera.getPos().z;

        Vector4f transformedCoordinates = new Vector4f((float) deltaX, (float) deltaY, (float) deltaZ, 1.f).mul(lastWorldSpaceMatrix);
        Matrix4f matrixProj = new Matrix4f(lastProjMat);
        Matrix4f matrixModel = new Matrix4f(lastModMat);
        matrixProj.mul(matrixModel).project(transformedCoordinates.x(), transformedCoordinates.y(), transformedCoordinates.z(), viewport, target);

        return new Vec3d(target.x / getScaleFactor(), (displayHeight - target.y) / getScaleFactor(), target.z);
    }

    public static double getScaleFactor() {
        return ClickGui.getInstance().scaleFactorFix.getValue() ? ClickGui.getInstance().scaleFactorFixValue.getValue() : mc.getWindow().getScaleFactor();
    }

    //Circle Render ItemESP
    public static void drawCircle3D(MatrixStack stack, Entity ent, float radius, int color, int points/*, boolean hudColor*/, int colorOffset) {
        RenderUtils.setup();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

        double x = ent.prevX + (ent.getX() - ent.prevX) * mc.getTickDelta() - mc.getEntityRenderDispatcher().camera.getPos().getX();
        double y = ent.prevY + (ent.getY() - ent.prevY) * mc.getTickDelta() - mc.getEntityRenderDispatcher().camera.getPos().getY();
        double z = ent.prevZ + (ent.getZ() - ent.prevZ) * mc.getTickDelta() - mc.getEntityRenderDispatcher().camera.getPos().getZ();
        stack.push();
        stack.translate(x, y, z);

        Matrix4f matrix = stack.peek().getPositionMatrix();
        for (int i = 0; i <= points; i++) {
            //if (hudColor)
            //   color = HudEditor.getColor(i * colorOffset).getRGB();

            bufferBuilder.vertex(matrix, (float) (radius * Math.cos(i * 6.28 / points)), 0f, (float) (radius * Math.sin(i * 6.28 / points))).color(color).next();
        }

        tessellator.draw();
        RenderUtils.cleanup();
        stack.translate(-x, -y, -z);
        stack.pop();
    }
    public static void setup() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public static void cleanup() {
        disableBlend();
    }
}
