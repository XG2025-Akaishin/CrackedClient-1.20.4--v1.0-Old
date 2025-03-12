package me.alpha432.oyvey.features.futuregui.components.items.buttons;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.features.futuregui.FutureGuiOpen;
import me.alpha432.oyvey.features.settings.Bind;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.futurerender.FutureRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

import me.alpha432.oyvey.features.modules.client.FutureGui;

public class BindButton extends Button {
    public boolean isListening;
    private final Setting setting;
    
    public BindButton(final Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 14;
    }
    
    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks) {
            FutureRenderer.Method510(context.getMatrices(), this.x, this.y, this.x + this.width + 7.4f, this.y + this.height - 0.5f, this.getState() ? (this.isHovering(mouseX, mouseY) ? CrackedClient.colorFutureClient.getColorWithAlpha(((FutureGui)CrackedClient.moduleManager.getModuleByName("FutureGui")).alpha.getValue()) : CrackedClient.colorFutureClient.getColorWithAlpha(((FutureGui)CrackedClient.moduleManager.getModuleByName("FutureGui")).hoverAlpha.getValue())) : (this.isHovering(mouseX, mouseY) ? -2007673515 : 290805077));
        if (this.isListening) {
            drawString("Press a key...", this.x + 2.3f, this.y - 1.7f - FutureGuiOpen.getFutureGuiPro().getTextOffset(), this.getState() ? -1 : -5592406);
            //OyVey.textManager.drawStringWithShadow("Press a key...", this.x + 2.3f, this.y - 1.7f - FutureGuiOpen.getFutureGuiPro().getTextOffset(), this.getState() ? -1 : -5592406);
        }
        else {
            drawString(this.setting.getName() + " \u00A77" + this.setting.getValue().toString(), this.x + 2.3f, this.y - 1.7f - FutureGuiOpen.getFutureGuiPro().getTextOffset(), this.getState() ? -1 : -5592406);
            //OyVey.textManager.drawStringWithShadow(this.setting.getName() + " \u00A77" + this.setting.getValue().toString(), this.x + 2.3f, this.y - 1.7f - FutureGuiOpen.getFutureGuiPro().getTextOffset(), this.getState() ? -1 : -5592406);
        }
    }
    
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_HARP, 1f));
        }
    }
    
    public void onKeyTyped(final char typedChar, final int keyCode) {
        if (this.isListening) {
            Bind bind = new Bind(keyCode);
            if (bind.toString().equalsIgnoreCase("Escape")) {
                return;
            }
            if (bind.toString().equalsIgnoreCase("Delete")) {
                bind = new Bind(-1);
            }
            this.setting.setValue(bind);
            super.onMouseClick();
        }
    }
    
    @Override
    public int getHeight() {
        return 14;
    }
    
    @Override
    public void toggle() {
        this.isListening = !this.isListening;
    }
    
    @Override
    public boolean getState() {
        return !this.isListening;
    }
}
