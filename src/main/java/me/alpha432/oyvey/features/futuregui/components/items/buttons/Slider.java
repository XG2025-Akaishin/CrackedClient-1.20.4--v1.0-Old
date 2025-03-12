package me.alpha432.oyvey.features.futuregui.components.items.buttons;

import java.util.*;

import org.lwjgl.glfw.GLFW;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.features.futuregui.FutureGuiOpen;
import me.alpha432.oyvey.features.futuregui.components.Component;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.futurerender.FutureRenderer;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.DrawContext;

import me.alpha432.oyvey.features.modules.client.FutureGui;

public class Slider extends Button {
    public Setting setting;
    private final Number min;
    private final Number max;
    private final int difference;
    
    public Slider(final Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.min = (Number) setting.getMin();
        this.max = (Number) setting.getMax();
        this.difference = this.max.intValue() - this.min.intValue();
        this.width = 14;
    }
    
    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        this.dragSetting(mouseX, mouseY);
        FutureRenderer.Method510(context.getMatrices(), this.x, this.y, this.x + this.width + 7.4f, this.y + this.height - 0.5f, this.isHovering(mouseX, mouseY) ? -2007673515 : 290805077);
        FutureRenderer.Method510(context.getMatrices(), this.x, this.y, (((Number) this.setting.getValue()).floatValue() <= this.min.floatValue()) ? this.x : (this.x + (this.width + 7.4f) * this.partialMultiplier()), this.y + this.height - 0.5f, this.isHovering(mouseX, mouseY) ? CrackedClient.colorFutureClient.getColorWithAlpha(CrackedClient.moduleManager.getModuleByClass(FutureGui.class).alpha.getValue()) : CrackedClient.colorFutureClient.getColorWithAlpha(CrackedClient.moduleManager.getModuleByClass(FutureGui.class).hoverAlpha.getValue()));
        drawString(this.getName() + " \u00A77" + ((this.setting.getValue() instanceof Float) ? this.setting.getValue() : Double.valueOf(((Number) this.setting.getValue()).doubleValue())), this.x + 2.3f, this.y - 1.7f - FutureGuiOpen.getFutureGuiPro().getTextOffset(), -1);
        //OyVey.textManager.drawStringWithShadow(this.getName() + " \u00A77" + ((this.setting.getValue() instanceof Float) ? this.setting.getValue() : Double.valueOf(((Number) this.setting.getValue()).doubleValue())), this.x + 2.3f, this.y - 1.7f - FutureGuiOpen.getFutureGuiPro().getTextOffset(), -1);
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            this.setSettingFromX(mouseX);
        }
    }
    
    public boolean isHovering(final int mouseX, final int mouseY) {
        for (final Component component : FutureGuiOpen.getFutureGuiPro().getComponents()) {
            if (!component.drag) {
                continue;
            }
            return false;
        }
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() + 8.0f && mouseY >= this.getY() && mouseY <= this.getY() + this.height;
    }
    
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }
    
    private void dragSetting(final int mouseX, final int mouseY) {
        if (this.isHovering(mouseX, mouseY) && GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), 0) == 1) {
            this.setSettingFromX(mouseX);
        }
    }
    
    public int getHeight() {
        return 14;
    }
    
    private void setSettingFromX(final int mouseX) {
        final float percent = (mouseX - this.x) / (this.width + 7.4f);
        if (this.setting.getValue() instanceof Double) {
            final double result = (Double) this.setting.getMin() + this.difference * percent;
            this.setting.setValue(Math.round(10.0 * result) / 10.0);
        }
        else if (this.setting.getValue() instanceof Float) {
            final float result2 = (Float) this.setting.getMin() + this.difference * percent;
            this.setting.setValue(Math.round(10.0f * result2) / 10.0f);
        }
        else if (this.setting.getValue() instanceof Integer) {
            this.setting.setValue((Integer) this.setting.getMin() + (int)(this.difference * percent));
        }
    }
    
    private float middle() {
        return this.max.floatValue() - this.min.floatValue();
    }
    
    private float part() {
        return ((Number) this.setting.getValue()).floatValue() - this.min.floatValue();
    }
    
    private float partialMultiplier() {
        return this.part() / this.middle();
    }
}
