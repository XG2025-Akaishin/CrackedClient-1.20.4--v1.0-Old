package me.alpha432.oyvey.features.modules.render.twodesp.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;

import java.awt.*;

public class Render2DUtil {
    public static void setRectPoints(BufferBuilder bufferBuilder, Matrix4f matrix, float x, float y, float x1, float y1, Color c1, Color c2, Color c3, Color c4) {
        bufferBuilder.vertex(matrix, x, y1, 0.0F).color(c1.getRGB()).next();
        bufferBuilder.vertex(matrix, x1, y1, 0.0F).color(c2.getRGB()).next();
        bufferBuilder.vertex(matrix, x1, y, 0.0F).color(c3.getRGB()).next();
        bufferBuilder.vertex(matrix, x, y, 0.0F).color(c4.getRGB()).next();
    }

    public static void setupRender() {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    public static void endRender() {
        RenderSystem.disableBlend();
    }
}
