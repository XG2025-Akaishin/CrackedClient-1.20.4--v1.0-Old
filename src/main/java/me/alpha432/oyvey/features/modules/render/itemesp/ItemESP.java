package me.alpha432.oyvey.features.modules.render.itemesp;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector4d;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.ColorUtil;
import me.alpha432.oyvey.features.modules.render.itemesp.utils.Render2DUtils;
import me.alpha432.oyvey.features.modules.render.itemesp.utils.RenderUtils;

import java.awt.*;

public class ItemESP extends Module {
    public static DrawContext context;
    public static ItemESP INSTANCE = new ItemESP();
    public ItemESP() {
        super("ItemESP", "ItemESP", Category.RENDER, true, false, true);
        setInstance();
    }
    private final Setting<ESPMode> espMode = this.register(new Setting<>("Mode", ESPMode.Rect));
    private final Setting<Boolean> shadow = this.register(new Setting<>("Shadow", true));
    public Setting<Boolean> glow = this.register(new Setting<>("Glow", true));
    private final Setting<Float> radius = this.register(new Setting<>("Radius", 1f, 0.1f, 5f, v -> espMode.getValue() == ESPMode.Circle));
    private final Setting<Integer> cOffset = this.register(new Setting<>("ColorOffset", 2, 1, 50, v -> espMode.getValue() == ESPMode.Circle));
    private final Setting<Integer> cPoints = this.register(new Setting<>("CirclePoints", 12, 3, 32, v -> espMode.getValue() == ESPMode.Circle));

    private final Setting<Integer> circleRed = this.register(new Setting<>("CircleRed", 120, 0, 255, v -> espMode.getValue() == ESPMode.Circle));
    private final Setting<Integer> circleGreen = this.register(new Setting<>("CircleGreen", 120, 0, 255, v -> espMode.getValue() == ESPMode.Circle));
    private final Setting<Integer> circleBlue = this.register(new Setting<>("CircleBlue", 120, 0, 255, v -> espMode.getValue() == ESPMode.Circle));
    private final Setting<Integer> circleAlpha = this.register(new Setting<>("CircleAlpha", 120, 0, 255, v -> espMode.getValue() == ESPMode.Circle));

    private final Setting<Integer> textRed = this.register(new Setting<>("TextRed", 120, 0, 255));
    private final Setting<Integer> textGreen = this.register(new Setting<>("TextGreen", 120, 0, 255));
    private final Setting<Integer> textBlue = this.register(new Setting<>("TextBlue", 120, 0, 255));
    private final Setting<Integer> textAlpha = this.register(new Setting<>("TextAlpha", 120, 0, 255));

    private final Setting<Integer> shaRed = this.register(new Setting<>("ShadowRed", 120, 0, 255));
    private final Setting<Integer> shaGreen = this.register(new Setting<>("ShadowGreen", 120, 0, 255));
    private final Setting<Integer> shaBlue = this.register(new Setting<>("ShadowBlue", 120, 0, 255));
    private final Setting<Integer> shaAlpha = this.register(new Setting<>("ShadowAlpha", 120, 0, 255));

    private final Setting<Integer> rectRed = this.register(new Setting<>("RectRed", 120, 0, 255, v -> espMode.getValue() == ESPMode.Rect));
    private final Setting<Integer> rectGreen = this.register(new Setting<>("RectGreen", 120, 0, 255, v -> espMode.getValue() == ESPMode.Rect));
    private final Setting<Integer> rectBlue = this.register(new Setting<>("RectBlue", 120, 0, 255, v -> espMode.getValue() == ESPMode.Rect));
    private final Setting<Integer> rectAlpha = this.register(new Setting<>("RectAlpha", 120, 0, 255, v -> espMode.getValue() == ESPMode.Rect));

    public static ItemESP getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ItemESP();
        }
        return INSTANCE;
    }

    public void setInstance() {
        INSTANCE = this;
    }


    private enum ESPMode {
        Rect, Circle, None
    }

    public void onPreRender2D(DrawContext context) {
        //Color textColor = new Color(textRed.getValue(), textGreen.getValue(), textBlue.getValue(), textAlpha.getValue());//TextColor
        int textco = ColorUtil.toRGBA(new Color(textRed.getValue(), textGreen.getValue(), textBlue.getValue(), textAlpha.getValue()));
        Color shadowColor = new Color(shaRed.getValue(), shaGreen.getValue(), shaBlue.getValue(), shaAlpha.getValue());//TextColor
        for (Entity ent : mc.world.getEntities()) {
            if (!(ent instanceof ItemEntity)) continue;
            Vec3d[] vectors = getPoints(ent);

            Vector4d position = null;
            for (Vec3d vector : vectors) {
                vector = RenderUtils.worldSpaceToScreenSpace(new Vec3d(vector.x, vector.y, vector.z));
                if (vector.z > 0 && vector.z < 1) {
                    if (position == null)
                        position = new Vector4d(vector.x, vector.y, vector.z, 0);
                    position.x = Math.min(vector.x, position.x);
                    position.y = Math.min(vector.y, position.y);
                    position.z = Math.max(vector.x, position.z);
                    position.w = Math.max(vector.y, position.w);
                }
            }

            if (position != null) {
                float posX = (float) position.x;
                float posY = (float) position.y;
                float endPosX = (float) position.z;

                float diff = (endPosX - posX) / 2f;
                float textWidth = (getStringWidth(ent.getDisplayName().getString()) * 1);
                float tagX = (posX + diff - textWidth / 2f) * 1;

                if (shadow.getValue())
                    Render2DUtils.drawBlurredShadow(context.getMatrices(), tagX - 2, posY - 13, getStringWidth(ent.getDisplayName().getString()) + 4, 10, 14, shadowColor);

                    //context.drawTextWithShadow(mc.textRenderer,ent.getDisplayName().getString(), tagX, (float) posY - 10, textColor);
                    context.drawTextWithShadow(mc.textRenderer,ent.getDisplayName().getString(), (int) (tagX), (int) ( posY - 10), textco);
            }
        }

        if (espMode.getValue() == ESPMode.Rect) {
            Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            Render2DUtils.setupRender();
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            for (Entity ent : mc.world.getEntities()) {
                if (!(ent instanceof ItemEntity)) continue;
                Vec3d[] vectors = getPoints(ent);

                Vector4d position = null;
                for (Vec3d vector : vectors) {
                    vector = RenderUtils.worldSpaceToScreenSpace(new Vec3d(vector.x, vector.y, vector.z));
                    if (vector.z > 0 && vector.z < 1) {
                        if (position == null)
                            position = new Vector4d(vector.x, vector.y, vector.z, 0);
                        position.x = Math.min(vector.x, position.x);
                        position.y = Math.min(vector.y, position.y);
                        position.z = Math.max(vector.x, position.z);
                        position.w = Math.max(vector.y, position.w);
                    }
                }

                if (position != null) {
                    float posX = (float) position.x;
                    float posY = (float) position.y;
                    float endPosX = (float) position.z;
                    float endPosY = (float) position.w;

                    drawRect(bufferBuilder, matrix, posX, posY, endPosX, endPosY);
                }
            }
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            Render2DUtils.endRender();
        }
    }

    public void onPreRender3D(MatrixStack stack) {
        Color circleColor = new Color(circleRed.getValue(), circleGreen.getValue(), circleBlue.getValue(), circleAlpha.getValue());//CircleColor
        if (espMode.getValue() == ESPMode.Circle)
            for (Entity ent : mc.world.getEntities())
                if (ent instanceof ItemEntity)
                    RenderUtils.drawCircle3D(stack, ent, radius.getValue(), circleColor.getRGB(), cPoints.getValue(), cOffset.getValue());
    }

    private void drawRect(BufferBuilder bufferBuilder, Matrix4f stack, float posX, float posY, float endPosX, float endPosY) {
        Color black = Color.BLACK;
        Render2DUtils.setRectPoints(bufferBuilder, stack, posX - 1F, posY, (posX + 0.5f), endPosY + 0.5f, black, black, black, black);
        Render2DUtils.setRectPoints(bufferBuilder, stack, posX - 1F, (posY - 0.5f), endPosX + 0.5f, posY + 1f, black, black, black, black);
        Render2DUtils.setRectPoints(bufferBuilder, stack, endPosX - 1f, posY, endPosX + 0.5f, endPosY + 0.5f, black, black, black, black);
        Render2DUtils.setRectPoints(bufferBuilder, stack, posX - 1, endPosY - 1f, endPosX + 0.5f, endPosY + 0.5f, black, black, black, black);
        Render2DUtils.setRectPoints(bufferBuilder, stack, posX - 0.5f, posY, posX, endPosY, getColor(270), getColor(0), getColor(0), getColor(270));
        Render2DUtils.setRectPoints(bufferBuilder, stack, posX, endPosY - 0.5f, endPosX, endPosY, getColor(0), getColor(180), getColor(180), getColor(0));
        Render2DUtils.setRectPoints(bufferBuilder, stack, posX - 0.5f, posY, endPosX, (posY + 0.5f), getColor(180), getColor(90), getColor(90), getColor(180));
        Render2DUtils.setRectPoints(bufferBuilder, stack, endPosX - 0.5f, posY, endPosX, endPosY, getColor(90), getColor(270), getColor(270), getColor(90));
    }
    public Color getColor(int count) {
        return new Color(rectRed.getValue(), rectGreen.getValue(), rectBlue.getValue(), rectAlpha.getValue());//DrawRect Color
    }

    @NotNull
    private static Vec3d[] getPoints(Entity ent) {
        double x = ent.prevX + (ent.getX() - ent.prevX) * mc.getTickDelta();
        double y = ent.prevY + (ent.getY() - ent.prevY) * mc.getTickDelta();
        double z = ent.prevZ + (ent.getZ() - ent.prevZ) * mc.getTickDelta();
        Box axisAlignedBB2 = ent.getBoundingBox();
        Box axisAlignedBB = new Box(axisAlignedBB2.minX - ent.getX() + x - 0.05, axisAlignedBB2.minY - ent.getY() + y, axisAlignedBB2.minZ - ent.getZ() + z - 0.05, axisAlignedBB2.maxX - ent.getX() + x + 0.05, axisAlignedBB2.maxY - ent.getY() + y + 0.15, axisAlignedBB2.maxZ - ent.getZ() + z + 0.05);
        Vec3d[] vectors = new Vec3d[]{new Vec3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ), new Vec3d(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ)};
        return vectors;
    }

    private int getStringWidth(String str) {
        return mc.textRenderer.getWidth(str);
    }
}
