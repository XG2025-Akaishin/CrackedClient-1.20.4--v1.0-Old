package me.alpha432.oyvey.features.notifications.notificationd.util;

import java.awt.Color;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class RenderUtil {
        public static void drawSmoothRect(float left, float top, float right, float bottom, int color) {
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        drawRect(left, top, right, bottom, color);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        drawRect(left * 2.0f - 1.0f, top * 2.0f, left * 2.0f, bottom * 2.0f - 1.0f, color);
        drawRect(left * 2.0f, top * 2.0f - 1.0f, right * 2.0f, top * 2.0f, color);
        drawRect(right * 2.0f, top * 2.0f, right * 2.0f + 1.0f, bottom * 2.0f - 1.0f, color);
        drawRect(left * 2.0f, bottom * 2.0f - 1.0f, right * 2.0f, bottom * 2.0f, color);
        GL11.glDisable(3042);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
    }
    public static void drawRect(float x, float y, float w, float h, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;//test //24
        float red = (float) (color >> 16 & 255) / 255.0F;//test
        float green = (float) (color >> 8 & 255) / 255.0F;//test
        float blue = (float) (color & 0xFF) / 255.0F;//test
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        //RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(x, h, 0.0F).color(red, green, blue, alpha).next();//test
        bufferBuilder.vertex( w, h, 0.0F).color(red, green, blue, alpha).next();//test
        bufferBuilder.vertex( w, y, 0.0F).color(red, green, blue, alpha).next();//test
        bufferBuilder.vertex( x, y, 0.0F).color(red, green, blue, alpha).next();//test
        //tessellator.draw();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    public static void verticalGradient(MatrixStack matrices, float left, float top, float right, float bottom, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;//test //24
        float red = (float) (color >> 16 & 255) / 255.0F;//test
        float green = (float) (color >> 8 & 255) / 255.0F;//test
        float blue = (float) (color & 0xFF) / 255.0F;//test
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, left, top, 0.0F).color(red, green, blue, alpha).next();
        bufferBuilder.vertex(matrix, left, bottom, 0.0F).color(red, green, blue, alpha).next();
        bufferBuilder.vertex(matrix, right, bottom, 0.0F).color(red, green, blue, alpha).next();
        bufferBuilder.vertex(matrix, right, top, 0.0F).color(red, green, blue, alpha).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        endRender();
    }
    public static void setupRender() {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }
    public static void endRender() {
        RenderSystem.disableBlend();
    }
    public static Color injectAlpha(final Color color, final int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp(alpha, 0, 255));
    }
}
