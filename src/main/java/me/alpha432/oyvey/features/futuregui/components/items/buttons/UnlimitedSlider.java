package me.alpha432.oyvey.features.futuregui.components.items.buttons;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.features.futuregui.FutureGuiOpen;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.futurerender.FutureRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import me.alpha432.oyvey.features.modules.client.FutureGui;

public class UnlimitedSlider extends Button {
    public Setting setting;
    
    public UnlimitedSlider(final Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 14;
    }
    
    public void drawScreen(DrawContext context, final int mouseX, final int mouseY, final float partialTicks) {
            FutureRenderer.Method510(context.getMatrices(), this.x, this.y, this.x + this.width + 7.4f, this.y + this.height - 0.5f, this.isHovering(mouseX, mouseY) ? CrackedClient.colorFutureClient.getColorWithAlpha(CrackedClient.moduleManager.getModuleByClass(FutureGui.class).alpha.getValue()) : CrackedClient.colorFutureClient.getColorWithAlpha(CrackedClient.moduleManager.getModuleByClass(FutureGui.class).hoverAlpha.getValue()));
                drawString(" - " + this.setting.getName() + " \u00A77" + this.setting.getValue() + "\u00A7r +", this.x + 2.3f, this.y - 1.7f - FutureGuiOpen.getFutureGuiPro().getTextOffset(), this.getState() ? -1 : -5592406);
            //OyVey.textManager.drawStringWithShadow(" - " + this.setting.getName() + " \u00A77" + this.setting.getValue() + "\u00A7r +", this.x + 2.3f, this.y - 1.7f - FutureGuiOpen.getFutureGuiPro().getTextOffset(), this.getState() ? -1 : -5592406);
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_HARP, 1f));
            if (this.isRight(mouseX)) {
                if (this.setting.getValue() instanceof Double) {
                    this.setting.setValue((Double) this.setting.getValue() + 1.0);
                }
                else if (this.setting.getValue() instanceof Float) {
                    this.setting.setValue((Float) this.setting.getValue() + 1.0f);
                }
                else if (this.setting.getValue() instanceof Integer) {
                    this.setting.setValue((Integer) this.setting.getValue() + 1);
                }
            }
            else if (this.setting.getValue() instanceof Double) {
                this.setting.setValue((Double) this.setting.getValue() - 1.0);
            }
            else if (this.setting.getValue() instanceof Float) {
                this.setting.setValue((Float) this.setting.getValue() - 1.0f);
            }
            else if (this.setting.getValue() instanceof Integer) {
                this.setting.setValue((Integer) this.setting.getValue() - 1);
            }
        }
    }
    
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }
    
    public int getHeight() {
        return 14;
    }
    
    public void toggle() {
    }
    
    public boolean getState() {
        return true;
    }
    
    public boolean isRight(final int x) {
        return x > this.x + (this.width + 7.4f) / 2.0f;
    }
}
