package me.alpha432.oyvey.util.futurerender;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;

public class FutureRenderer {
    
    public static void Method510(MatrixStack matrix, float x, float y, float w, float h, int color) {
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferbuilder.vertex(matrix.peek().getPositionMatrix(), x, h, 0.0f).color(red, green, blue, alpha).next();
        bufferbuilder.vertex(matrix.peek().getPositionMatrix(), w, h, 0.0f).color(red, green, blue, alpha).next();
        bufferbuilder.vertex(matrix.peek().getPositionMatrix(), w, y, 0.0f).color(red, green, blue, alpha).next();
        bufferbuilder.vertex(matrix.peek().getPositionMatrix(), x, y, 0.0f).color(red, green, blue, alpha).next();
        tessellator.draw();
        //BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();
    }
}
