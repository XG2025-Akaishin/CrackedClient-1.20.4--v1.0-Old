package me.alpha432.oyvey.features.futuregui.components.items.buttons;

import java.util.ArrayList;
import java.util.List;

import me.alpha432.oyvey.features.futuregui.components.items.Item;
import me.alpha432.oyvey.features.settings.Bind;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import me.alpha432.oyvey.features.modules.client.FutureGui;
import me.alpha432.oyvey.features.modules.Module;

public class ModuleButton extends Button {
    public static final Identifier gear = new Identifier("textures/future/texture/gear.png");
    private final Module module;
    private List<Item> items;
    private boolean subOpen;
    
    public ModuleButton(final Module module) {
        super(module.getName());
        this.items = new ArrayList<Item>();
        this.module = module;
        this.initSettings();
    }
    
    public void initSettings() {
        final ArrayList<Item> newItems = new ArrayList<Item>();
        if (!this.module.getSettings().isEmpty()) {
            for (final Setting setting : this.module.getSettings()) {
                if (setting.getValue() instanceof Boolean && !setting.getName().equals("Enabled")) {
                    newItems.add((Item)new BooleanButton(setting));
                }
                if (setting.getValue() instanceof Bind && !this.module.getName().equalsIgnoreCase("Hud")) {
                    newItems.add((Item)new BindButton(setting));
                }
                if (setting.getValue() instanceof String || setting.getValue() instanceof Character) {
                    newItems.add((Item)new StringButton(setting));
                }
                if (setting.isNumberSetting()) {
                    if (setting.hasRestriction()) {
                        newItems.add((Item)new Slider(setting));
                        continue;
                    }
                    newItems.add((Item)new UnlimitedSlider(setting));
                }
                if (!setting.isEnumSetting()) {
                    continue;
                }
                newItems.add((Item)new EnumButton(setting));
            }
        }
        this.items = newItems;
    }


    public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(context, mouseX, mouseY, partialTicks);
        if (!this.items.isEmpty()) {
            //Puede suceder el error de  que no aparesca los items de la gui
        }

        MatrixStack matrixStack = context.getMatrices();
        float txcontrol = (float) (x + width - 10 / 2) - 3;
        float tycontrol = (float) (y + 9.2f / 2) - (-3);
        if (FutureGui.getInstance().future.getValue()) {
            matrixStack.push();
            context.drawTexture(gear, (int) (txcontrol - 5), (int) (tycontrol - 5), 0, 0, 10, 10, 10, 10);
            matrixStack.pop();
        }
    
        if (this.subOpen) {
            float height = 1.0f;
            for (final Item item : this.items) {
                if (!item.isHidden()) {
                    item.setLocation(this.x + 1.0f, this.y + (height += 15.0f));
                    item.setHeight(15);
                    item.setWidth(this.width - 9);
                    item.drawScreen(context, mouseX, mouseY, partialTicks);
                }
                item.update();
            }
        }
        //Remove Info mouse raton
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!this.items.isEmpty()) {
            if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
                this.subOpen = !this.subOpen;
                //IUtil.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1f));
            }
            if (this.subOpen) {
                for (final Item item : this.items) {
                    if (item.isHidden()) {
                        continue;
                    }
                    item.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }
    }
    
    public void onKeyTyped(final char typedChar, final int keyCode) {
        super.onKeyTyped(typedChar, keyCode);
        if (!this.items.isEmpty() && this.subOpen) {
            for (final Item item : this.items) {
                if (item.isHidden()) {
                    continue;
                }
                item.onKeyTyped(typedChar, keyCode);
            }
        }
    }
    
    public int getHeight() {
        if (this.subOpen) {
            int height = 14;
            for (final Item item : this.items) {
                if (item.isHidden()) {
                    continue;
                }
                height += item.getHeight() + 1;
            }
            return height + 2;
        }
        return 14;
    }
    
    public Module getModule() {
        return this.module;
    }
    
    @Override
    public void toggle() {
        this.module.toggle();
    }

    @Override
    public boolean getState() {
        return this.module.isEnabled();
    }
}
