package me.alpha432.oyvey.features.futuregui.components.items;

import java.awt.Color;

import me.alpha432.oyvey.features.Feature;
import net.minecraft.client.gui.DrawContext;

public class Item extends Feature {
   protected float x;
   protected float y;
   protected int width;
   protected int height;
   private boolean hidden;
   public static DrawContext context;

   public Item(String name) {
      super(name);
   }

   public void setLocation(float x, float y) {
      this.x = x;
      this.y = y;
   }

   public void drawScreen(DrawContext context, int mouseX, int mouseY, float partialTicks) {
   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
   }

   public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
   }

   public void update() {
   }

   public void onKeyTyped(char typedChar, int keyCode) {
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public int getHeight() {
      return this.height;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public boolean setHidden(boolean hidden) {
      this.hidden = hidden;
      return this.hidden;
   }


//renderer text 
protected void drawString(String text, double x, double y, Color color) {
   drawString(text, x, y, color.hashCode());
}

protected void drawString(String text, double x, double y, int color) {
   context.drawTextWithShadow(mc.textRenderer, text, (int) x, (int) y, color);
}
}
