package me.alpha432.oyvey.util.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.math.Box;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import static me.alpha432.oyvey.util.traits.Util.mc;
import java.awt.*;

public class RendersUtil {

    public static void drawBoxOutline(MatrixStack stack, Box box, Color c) {
        float minX = (float) (box.minX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float minY = (float) (box.minY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float minZ = (float) (box.minZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());
        float maxX = (float) (box.maxX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float maxY = (float) (box.maxY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float maxZ = (float) (box.maxZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());
    
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
    
        setup3D();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);
    
        // Dibujar líneas del bloque
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, minZ).color(c.getRGB()).next();
    
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, maxZ).color(c.getRGB()).next();
    
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, maxZ).color(c.getRGB()).next();
    
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, minZ).color(c.getRGB()).next();
    
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, minZ).color(c.getRGB()).next();
    
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, maxZ).color(c.getRGB()).next();
    
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, maxZ).color(c.getRGB()).next();
    
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, minZ).color(c.getRGB()).next();
    
        tessellator.draw();
        clean3D();
    }

    public static void drawBoxFilled(MatrixStack stack, Box box, Color c) {
        float minX = (float) (box.minX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float minY = (float) (box.minY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float minZ = (float) (box.minZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());
        float maxX = (float) (box.maxX - mc.getEntityRenderDispatcher().camera.getPos().getX());
        float maxY = (float) (box.maxY - mc.getEntityRenderDispatcher().camera.getPos().getY());
        float maxZ = (float) (box.maxZ - mc.getEntityRenderDispatcher().camera.getPos().getZ());

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        setup3D();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, maxZ).color(c.getRGB()).next();

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, minZ).color(c.getRGB()).next();

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, minZ).color(c.getRGB()).next();

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, maxZ).color(c.getRGB()).next();

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, minY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), maxX, maxY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, maxZ).color(c.getRGB()).next();

        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, minZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, minY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, maxZ).color(c.getRGB()).next();
        bufferBuilder.vertex(stack.peek().getPositionMatrix(), minX, maxY, minZ).color(c.getRGB()).next();

        tessellator.draw();
        clean3D();
    }


    public static void setup() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public static void setup3D() {
        setup();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
    }
    public static void clean() {
        RenderSystem.disableBlend();
    }

    public static void clean3D() {
        clean();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
    }
}
