package me.alpha432.oyvey.features.futuregui.components.items.buttons;


import java.awt.*;
import java.awt.datatransfer.*;

import org.lwjgl.glfw.GLFW;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.features.futuregui.FutureGuiOpen;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.futurerender.FutureRenderer;
import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.*;

import me.alpha432.oyvey.features.modules.client.FutureGui;

public class StringButton extends Button {
    public boolean isListening;
    private final Setting setting;
    private CurrentString currentString;
    
    public StringButton(final Setting setting) {
        super(setting.getName());
        this.currentString = new CurrentString("");
        this.setting = setting;
        this.width = 14;
    }
    
    public static String removeLastChar(final String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }
    
    public void drawScreen(DrawContext context,final int mouseX, final int mouseY, final float partialTicks) {
            FutureRenderer.Method510(context.getMatrices(), this.x, this.y, this.x + this.width + 7.4f, this.y + this.height - 0.5f, this.getState() ? (this.isHovering(mouseX, mouseY) ? CrackedClient.colorFutureClient.getColorWithAlpha(CrackedClient.moduleManager.getModuleByClass(FutureGui.class).alpha.getValue()) : CrackedClient.colorFutureClient.getColorWithAlpha(CrackedClient.moduleManager.getModuleByClass(FutureGui.class).hoverAlpha.getValue())) : (this.isHovering(mouseX, mouseY) ? -2007673515 : 290805077));
        if (this.isListening) {
            drawString(this.currentString.getString() + "_", this.x + 2.3f, this.y - 1.7f - FutureGuiOpen.getFutureGuiPro().getTextOffset(), this.getState() ? -1 : -5592406);
            //OyVey.textManager.drawStringWithShadow(this.currentString.getString() + OyVey.textManager.getIdleSign(), this.x + 2.3f, this.y - 1.7f - FutureGuiOpen.getFutureGuiPro().getTextOffset(), this.getState() ? -1 : -5592406);
        }
        else {
            drawString((this.setting.shouldRenderName() ? (this.setting.getName() + " \u00A77") : "") + this.setting.getValue(), this.x + 2.3f, this.y - 1.7f - FutureGuiOpen.getFutureGuiPro().getTextOffset(), this.getState() ? -1 : -5592406);
            //OyVey.textManager.drawStringWithShadow((this.setting.shouldRenderName() ? (this.setting.getName() + " \u00A77") : "") + this.setting.getValue(), this.x + 2.3f, this.y - 1.7f - FutureGuiOpen.getFutureGuiPro().getTextOffset(), this.getState() ? -1 : -5592406);
        }
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            //IUtil.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_HARP, 1.0f));
            mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_HARP, 1f));
        }
    }
    
    public void onKeyTyped(final char typedChar, final int keyCode) {
        if (this.isListening) {
            if (keyCode == 1) {
                return;
            }
            if (keyCode == 28) {
                this.enterString();
            }
            else if (keyCode == 14) {
                this.setString(removeLastChar(this.currentString.getString()));
            }
            else {
                //Else Other Code
            }
        }
    }
    
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }
    
    private void enterString() {
        if (this.currentString.getString().isEmpty()) {
            this.setting.setValue(this.setting.getDefaultValue());
        }
        else {
            this.setting.setValue(this.currentString.getString());
        }
        this.setString("");
        super.onMouseClick();
    }
    
    public int getHeight() {
        return 14;
    }
    
    public void toggle() {
        this.isListening = !this.isListening;
    }
    
    public boolean getState() {
        return !this.isListening;
    }
    
    public void setString(final String newString) {
        this.currentString = new CurrentString(newString);
    }
    
    public static class CurrentString
    {
        private final String string;
        
        public CurrentString(final String string) {
            this.string = string;
        }
        
        public String getString() {
            return this.string;
        }
    }
}
