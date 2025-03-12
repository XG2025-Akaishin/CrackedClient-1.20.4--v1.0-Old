package me.alpha432.oyvey.features.futuregui.components.items.buttons;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.features.futuregui.FutureGuiOpen;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.futurerender.FutureRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

import me.alpha432.oyvey.features.modules.client.FutureGui;

public class EnumButton extends Button {
    public Setting setting;
    
    public EnumButton(final Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }
    
    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        //render Utils :b
            FutureRenderer.Method510(context.getMatrices(), this.x, this.y, this.x + this.width + 7.4f, this.y + this.height - 0.5f, this.getState() ? (this.isHovering(mouseX, mouseY) ? CrackedClient.colorFutureClient.getColorWithAlpha(CrackedClient.moduleManager.getModuleByClass(FutureGui.class).alpha.getValue()) : CrackedClient.colorFutureClient.getColorWithAlpha(CrackedClient.moduleManager.getModuleByClass(FutureGui.class).hoverAlpha.getValue())) : (this.isHovering(mouseX, mouseY) ? -2007673515 : 290805077));
            drawString(this.setting.getName() + " \u00A77" + this.setting.currentEnumName(), this.x + 2.3f, this.y - 1.7f - FutureGuiOpen.getFutureGuiPro().getTextOffset(), this.getState() ? -1 : -5592406);
        //OyVey.textManager.drawStringWithShadow(this.setting.getName() + " \u00A77" + this.setting.currentEnumName(), this.x + 2.3f, this.y - 1.7f - FutureGuiOpen.getFutureGuiPro().getTextOffset(), this.getState() ? -1 : -5592406);
    }
    
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_HARP, 1f));
        }
    }
    
    public int getHeight() {
        return 14;
    }
    
    public void toggle() {
        this.setting.increaseEnum();
    }
    
    public boolean getState() {
        return true;
    }
}
