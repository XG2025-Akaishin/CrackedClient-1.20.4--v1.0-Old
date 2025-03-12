package me.alpha432.oyvey.features.futuregui;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.features.futuregui.components.items.buttons.Button;
import me.alpha432.oyvey.features.futuregui.components.items.buttons.ModuleButton;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.futuregui.components.Component;
import me.alpha432.oyvey.features.futuregui.components.items.Item;
import me.alpha432.oyvey.features.futuregui.components.items.buttons.ModuleButton;
import me.alpha432.oyvey.features.modules.client.FutureGui;
import me.alpha432.oyvey.util.futurerender.*;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static me.alpha432.oyvey.util.futurerender.Util.mc;

public class FutureGuiOpen 
        extends Screen {
    private static FutureGuiOpen FutureGuiCracked;
    private static FutureGuiOpen INSTANCE;
    private final ArrayList<Component> components = new ArrayList<Component>();
    private float scrollY;
    
    public FutureGuiOpen() {
        super(Text.literal("FutureGui"));
        //this.components = new ArrayList<Component>();
        this.setInstance();
        this.load();
    }
    
    public static FutureGuiOpen getInstance() {
        if (FutureGuiOpen.INSTANCE == null) {
            FutureGuiOpen.INSTANCE = new FutureGuiOpen();
        }
        return FutureGuiOpen.INSTANCE;
    }
    
    public static FutureGuiOpen getFutureGuiPro() {
        final FutureGuiOpen FutureGuiCracked = FutureGuiOpen.FutureGuiCracked;
        return getInstance();
    }
    
    private void setInstance() {
        FutureGuiOpen.INSTANCE = this;
    }
    
    private void load() {
        int x = -80;
        for (final Module.Category category : CrackedClient.moduleManager.getCategories()) {
            final ArrayList<Component> components2 = this.components;
            final String name = category.getName();
            x += 100;
            components2.add(new Component(name, x, 15, true) {

                public void setupItems() {
                    CrackedClient.moduleManager.getModulesByCategory(category).forEach(module -> {
                        if (!module.hidden) {
                            this.addButton((Button) new ModuleButton(module));
                        }
                    });
                }
            });
        }
        this.components.forEach(components -> components.getItems().sort((item1, item2) -> item1.getName().compareTo(item2.getName())));
    }
    
    public void updateModule(final Module module) {
        for (final Component component : this.components) {
            for (final Item item : component.getItems()) {
                if (!(item instanceof ModuleButton)) {
                    continue;
                }
                final ModuleButton button = (ModuleButton)item;
                final Module mod = button.getModule();
                if (module == null) {
                    continue;
                }
                if (!module.equals(mod)) {
                    continue;
                }
                button.initSettings();
                break;
            }
        }
    }
    
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        Item.context = context;
        this.components.forEach(components -> components.drawScreen(context, mouseX, mouseY, partialTicks));
        if ((Boolean)FutureGui.getInstance().backgroundScreen.getValue()) {
        FutureRenderer.Method510(context.getMatrices(), 0.0F, 0.0F, mc.getWindow().getWidth(), mc.getWindow().getHeight(), ColorUtil.toRGBA(14, 14, 14, 120)); //Render
        }
        if ((Boolean)FutureGui.getInstance().logoWaifu.getValue()) {
        //this.drawImageLogo();
        }
        this.components.forEach(components -> components.drawScreen(context, mouseX, mouseY, partialTicks));
    }
    
    @Override 
    public boolean mouseClicked(double mouseX, double mouseY, int clickedButton) {
        this.components.forEach(components -> components.mouseClicked((int) mouseX, (int) mouseY, clickedButton));
        return super.mouseClicked(mouseX, mouseY, clickedButton);
    }

    @Override 
    public boolean mouseReleased(double mouseX, double mouseY, int releaseButton) {
        this.components.forEach(components -> components.mouseReleased((int) mouseX, (int) mouseY, releaseButton));
        return super.mouseReleased(mouseX, mouseY, releaseButton);
    }
    
    /*public boolean doesGuiPauseGame() {
        return false;
    }*/
    @Override 
    public boolean shouldPause() {
        return false;
    }
    
    public final ArrayList<Component> getComponents() {
        return this.components;
    }
    
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount < 0) {
            this.components.forEach(component -> component.setY(component.getY() - 10));
        } else if (verticalAmount > 0) {
            this.components.forEach(component -> component.setY(component.getY() + 10));
        }
        scrollY += (int) (verticalAmount * 5D);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    
    public int getTextOffset() {
        return -6;
    }
    
    public Component getComponentByName(final String name) {
        for (final Component component : this.components) {
            if (!component.getName().equalsIgnoreCase(name)) {
                continue;
            }
            return component;
        }
        return null;
    }
    
    @Override public boolean charTyped(char chr, int modifiers) {
        this.components.forEach(component -> component.onKeyTyped(chr, modifiers));
        return super.charTyped(chr, modifiers);
    }
    
    static {
        FutureGuiOpen.INSTANCE = new FutureGuiOpen();
    }
}
