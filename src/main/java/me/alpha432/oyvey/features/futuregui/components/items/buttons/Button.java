package me.alpha432.oyvey.features.futuregui.components.items.buttons;

import java.util.*;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.features.futuregui.FutureGuiOpen;
import me.alpha432.oyvey.features.futuregui.components.Component;
import me.alpha432.oyvey.features.futuregui.components.items.Item;
import me.alpha432.oyvey.util.futurerender.FutureRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

import me.alpha432.oyvey.features.modules.client.FutureGui;

public class Button extends Item {
    private boolean state;
    
    public Button(final String name) {
        super(name);
        this.height = 15;
    }
    
    //@Override
    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        FutureRenderer.Method510(context.getMatrices(), this.x, this.y, this.x + this.width, this.y + this.height - 0.5f, this.getState() ? (this.isHovering(mouseX, mouseY) ? CrackedClient.colorFutureClient.getColorWithAlpha(CrackedClient.moduleManager.getModuleByClass(FutureGui.class).alpha.getValue()) : CrackedClient.colorFutureClient.getColorWithAlpha(CrackedClient.moduleManager.getModuleByClass(FutureGui.class).hoverAlpha.getValue())) : (!this.isHovering(mouseX, mouseY) ? CrackedClient.colorFutureClient.getColorWithAlpha(30) : -2007673515 ));
        drawString(this.getName(), this.x + 2.3f, this.y - 2.0f - FutureGuiOpen.getFutureGuiPro().getTextOffset(), this.getState() ? -1 : -5592406);
        //OyVey.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 2.0f - FutureGuiOpen.getFutureGuiPro().getTextOffset(), this.getState() ? -1 : -5592406);
    }
    
    //@Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.onMouseClick();
        }
    }
    
    public void onMouseClick() {
        this.state = !this.state;
        this.toggle();
        mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_HARP, 1f));
    }
    
    public void toggle() {
    }
    
    public boolean getState() {
        return this.state;
    }
    
    @Override
    public int getHeight() {
        return 14;
    }
    
    public boolean isHovering(final int mouseX, final int mouseY) {
        for (final Component component : FutureGuiOpen.getFutureGuiPro().getComponents()) {
            if (!component.drag) {
                continue;
            }
            return false;
        }
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.height;
    }
}
