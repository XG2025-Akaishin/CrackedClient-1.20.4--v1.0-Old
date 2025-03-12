package me.alpha432.oyvey.features.notifications.notificationd;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.features.gui.OyVeyGui;
import me.alpha432.oyvey.features.modules.render.holesp.utilshole.Render2DEngine;
import me.alpha432.oyvey.features.notifications.notificationd.util.RenderUtil;
import me.alpha432.oyvey.features.notifications.notificationd.util.timer.Timer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import static me.alpha432.oyvey.features.notifications.Util.mc;

public class NotificationNew {
    Window res = mc.getWindow();
    private final String message;
    private  final Timer timer;
    private final Type type;

    private double posY;
    private double width;
    private double height;
    private double animationX;
    private final int imageWidth;
    private final long stayTime;
    protected DrawContext context;

    private final Animation animation = new DecelerateAnimation(380, 1, Direction.BACKWARDS);
    private final Animation animationY = new DecelerateAnimation(380, 1);

    public NotificationNew(String message, Type type,int time) {
        stayTime = time;
        this.message = message;
        this.type = type;
        timer =  new Timer();
        timer.reset();
        //ScaledResolution sr = new ScaledResolution(mc);
        width =  mc.textRenderer.getWidth(message) + 34;//FontRender.getStringWidth5(message) + 34;
        animationX = width;
        imageWidth = 9;
        height = 33;
        posY = res.getScaledHeight() - height;
    }

    public void render(MatrixStack matrices , double getY) {
        Color scolor = new Color(NotificationManager.getInstance().colorRed.getValue(), NotificationManager.getInstance().colorGreen.getValue(), NotificationManager.getInstance().colorBlue.getValue(), NotificationManager.getInstance().colorAlpha.getValue());
        Color icolor = new Color(scolor.getRed(), scolor.getGreen(), scolor.getBlue(), scolor.getAlpha());
        //ScaledResolution resolution = new ScaledResolution(mc);
        animationY.setDirection(isFinished() ? Direction.BACKWARDS : Direction.FORWARDS);
        animation.setDirection(isFinished() ? Direction.FORWARDS : Direction.BACKWARDS);
        animationX = width * animation.getOutput();
        posY = animate(posY, getY);
        int x1 = (int) ((res.getScaledWidth() - 6) - width + animationX), y1 = (int) posY;

        //RenderUtil.drawSmoothRect((float) x1, y1, (float) width + (float) x1, (float) height + y1, icolor.getRGB());
        RenderUtil.verticalGradient(matrices, (float) x1, y1, (float) width + (float) x1, (float) height + y1, icolor.getRGB());

        //RenderUtil.verticalGradient(matrix, x1 + 25, y1 + 1, x1 + 25.5f, y1 + 12, Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0), HudEditor.textColor.getValue().getColorObject());
        //RenderUtil.verticalGradient(matrices, x1 + 25, y1 + 11, x1 + 25.5f, y1 + 22, HudEditor.textColor.getValue().getColorObject(), Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0));

        //drawString(type.getName(), (x1 + 6), y1 + 4, -1);
        drawString(type.getName(), (x1 + 6), y1 + 4, -1);
        drawString(message, (int) (x1 + 6), (int) ((float) y1 + 4 + height / 2), -1);//Error Font
        //FontRender.drawString5(message, (int) (x1 + 6), (int) ((float) y1 + 4 + (height - FontRender.getFontHeight5()) / 2), -1);

    }

    protected void drawString(String text, double x, double y, Color color) {
        drawString(text, x, y, color.hashCode());
    }

    protected void drawString(String text, double x, double y, int color) {
        context.drawTextWithShadow(mc.textRenderer, text, (int) x, (int) y, color);
        //drawString(text, x, y, color);
    }

    private boolean isFinished() {
        return timer.passedMs(stayTime);
    }
    public double getHeight() {
        return height;
    }


    public boolean shouldDelete() {
        return (isFinished()) && animationX >= width - 5;
    }


    public enum Type {
        SUCCESS("Success"),
        INFO("Information"),
        WARNING("Warning"),
        ERROR("Error"),
        ENABLED("Module toggled"),
        DISABLED("Module toggled");

        final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }


    public  double animate(double value, double target) {
        return value + (target - value) / (3 + 1 * CrackedClient.moduleManager.getModuleByClass(NotificationManager.class).deltt.getValue());
    }
}
