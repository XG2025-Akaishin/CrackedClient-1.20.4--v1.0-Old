package me.alpha432.oyvey.features.futuregui.components;

import net.minecraft.util.*;
import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.futuregui.FutureGuiOpen;
import me.alpha432.oyvey.features.futuregui.components.items.Item;
import me.alpha432.oyvey.features.futuregui.components.items.buttons.Button;
import me.alpha432.oyvey.util.futurerender.FutureRenderer;

import java.awt.Color;
import java.util.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;

import me.alpha432.oyvey.features.modules.client.FutureGui;

public class Component extends Feature {
    protected DrawContext context;
    private final ArrayList<Item> items;
    public static final Identifier arrow = new Identifier("textures/future/texture/arrow.png");
    private MinecraftClient minecraft = MinecraftClient.getInstance();
    public boolean drag;
    private int x;
    private int y;
    private int x2;
    private int y2;
    private int width;
    private int height;
    private boolean open;
    private boolean hidden;
    
    public Component(final String name, final int x, final int y, final boolean open) {
        super(name);
        this.items = new ArrayList<Item>();
        this.hidden = false;
        this.x = x;
        this.y = y;
        this.width = 95;
        this.height = 18;
        this.open = open;
        this.setupItems();
    }
    
    public void setupItems() {
    }
    
    private void drag(final int mouseX, final int mouseY) {
        if (!this.drag) {
            return;
        }
        this.x = this.x2 + mouseX;
        this.y = this.y2 + mouseY;
    }
    
    public void drawScreen(DrawContext context, final int mouseX, final int mouseY, final float partialTicks) {
        this.context = context;
        this.drag(mouseX, mouseY);
        final float totalItemHeight = this.open ? (this.getTotalItemHeight() - 2.0f) : 0.0f;
         int color = -7829368;
         int argb = 0;
        if (this.open) {
            //Render
            FutureRenderer.Method510(context.getMatrices(),(float)this.x, this.y + 14.0f, (float)(this.x + this.width), this.y + this.height + totalItemHeight, 0);
        }
        //Import init color manager etc add alpha
             int n = color = CrackedClient.colorFutureClient.getColorWithAlpha(((Integer)((FutureGui)CrackedClient.moduleManager.getModuleByClass(FutureGui.class)).hoverAlpha.getValue()).intValue());
   
             FutureRenderer.Method510(context.getMatrices(),this.x, this.y - 1.5F, (this.x + this.width), (this.y + this.height - 6), color);
            if (this.open) {
                FutureRenderer.Method510(context.getMatrices(),this.x, this.y + 12.5F, (this.x + this.width), (this.y + this.height) + totalItemHeight, 1996488704);
               }
             if (this.open) {
                FutureRenderer.Method510(context.getMatrices(),this.x, this.y + 12.5F, (this.x + this.width), (this.y + this.height) + totalItemHeight, 0);
            }
            drawString(getName(), this.x + 3.0F, this.y - 4.0F - FutureGuiOpen.getFutureGuiPro().getTextOffset(), -1);
             //OyVey.textManager.drawStringWithShadow(getName(), this.x + 3.0F, this.y - 4.0F - FutureGuiOpen.getFutureGuiPro().getTextOffset(), -1);

        MatrixStack matrixStack = context.getMatrices();
        float txcontrol = (float) (x + width - 10 / 2) - 3;
        float tycontrol = (float) (y + 9.2f / 2) - (-1);

        if (FutureGui.getInstance().future.getValue()) {
            matrixStack.push();
            context.drawTexture(arrow, (int) (txcontrol - 5), (int) (tycontrol - 5), 0, 0, 10, 10, 10, 10);
            matrixStack.pop();
        }
        
        if (this.open) {
            //remove?????
            //add texture arrow si se buega
            float y = this.getY() + this.getHeight() - 3.0f;
            for (final Item item2 : this.getItems()) {
                if (item2.isHidden()) {
                    continue;
                }
                item2.setLocation(this.x + 2.0f, y);
                item2.setWidth(this.getWidth() - 4);
                item2.drawScreen(context, mouseX, mouseY, partialTicks);
                y += item2.getHeight() + 1.5f;
            }
        }
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.x2 = this.x - mouseX;
            this.y2 = this.y - mouseY;
            FutureGuiOpen.getFutureGuiPro().getComponents().forEach(component -> {
                if (component.drag) {
                    component.drag = false;
                }
                return;
            });
            this.drag = true;
            return;
        }
        if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
            this.open = !this.open;
            //IUtil.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1f));
            return;
        }
        if (!this.open) {
            return;
        }
        this.getItems().forEach(item -> item.mouseClicked(mouseX, mouseY, mouseButton));
    }
    
    public void mouseReleased(final int mouseX, final int mouseY, final int releaseButton) {
        if (releaseButton == 0) {
            this.drag = false;
        }
        if (!this.open) {
            return;
        }
        this.getItems().forEach(item -> item.mouseReleased(mouseX, mouseY, releaseButton));
    }
    
    public void onKeyTyped(final char typedChar, final int keyCode) {
        if (!this.open) {
            return;
        }
        this.getItems().forEach(item -> item.onKeyTyped(typedChar, keyCode));
    }
    
    public void addButton(final Button button) {
        this.items.add(button);
    }
    
    public int getX() {
        return this.x;
    }
    
    public void setX(final int x) {
        this.x = x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public void setY(final int y) {
        this.y = y;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public void setWidth(final int width) {
        this.width = width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public void setHeight(final int height) {
        this.height = height;
    }
    
    public boolean isHidden() {
        return this.hidden;
    }
    
    public void setHidden(final boolean hidden) {
        this.hidden = hidden;
    }
    
    public boolean isOpen() {
        return this.open;
    }
    
    public final ArrayList<Item> getItems() {
        return this.items;
    }
    
    private boolean isHovering(final int mouseX, final int mouseY) {
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.getHeight() - (this.open ? 2 : 0);
    }
    
    private float getTotalItemHeight() {
        float height = 0.0f;
        for (final Item item : this.getItems()) {
            height += item.getHeight() + 1.5f;
        }
        return height;
    }

    //Render Text Or Fond
    protected void drawString(String text, double x, double y, Color color) {
        drawString(text, x, y, color.hashCode());
    }

    protected void drawString(String text, double x, double y, int color) {
        context.drawTextWithShadow(mc.textRenderer, text, (int) x, (int) y, color);
    }
}
