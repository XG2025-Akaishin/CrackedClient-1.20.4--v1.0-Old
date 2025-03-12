package me.alpha432.oyvey.features.modules.render.nametags.util;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;

public class Render2DUtil {
    public static void drawRect(MatrixStack matrices, float x, float y, float width, float height, int c) {
        drawRect(matrices, x, y, width, height, new Color(c, true));
    }

    public static void drawRect(MatrixStack matrices, float x, float y, float width, float height, Color c) {
        if (c.getAlpha() <= 5) return;
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, x, y + height, 0.0F).color(c.getRGB()).next();
        bufferBuilder.vertex(matrix, x + width, y + height, 0.0F).color(c.getRGB()).next();
        bufferBuilder.vertex(matrix, x + width, y, 0.0F).color(c.getRGB()).next();
        bufferBuilder.vertex(matrix, x, y, 0.0F).color(c.getRGB()).next();
        Tessellator.getInstance().draw();
        endRender();
    }


    public static void drawRect(DrawContext drawContext, float x, float y, float width, float height, Color c) {
        drawRect(drawContext.getMatrices(), x, y, width, height, c);
        //drawContext.fill((int) x, (int) y, (int) (x + width), (int) (y + height), c.getRGB());
    }

    public static void setupRender() {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    public static void endRender() {
        RenderSystem.disableBlend();
    }
}
