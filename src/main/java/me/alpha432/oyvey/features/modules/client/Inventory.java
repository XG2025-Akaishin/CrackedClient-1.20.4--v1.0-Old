package me.alpha432.oyvey.features.modules.client;

import net.minecraft.util.*;
import me.alpha432.oyvey.event.impl.Render2DEvent;
import me.alpha432.oyvey.features.*;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.*;
import net.minecraft.client.gui.*;

public class Inventory extends Module {

    private static final Identifier inventory = new Identifier("textures/gui/container/shulker_box.png");
    private static final Identifier inventoryTransparent = new Identifier("textures/transparentinventory.png");
    private static final Identifier inventoryVanilla = new Identifier("textures/inventory.png");
    private static final Identifier inventoryblack = new Identifier("textures/blackinventory.png");

    public Setting<Mode> InvMode = this.register(new Setting("logoHeight", Mode.Black));
    public Setting<Integer> imageX = this.register(new Setting("logoX", 0, (-700), 1200));
    public Setting<Integer> imageY = this.register(new Setting("logoY", 0, (-700), 1200));//Test negative

    public Inventory() {
        super("Inventory", "Inventory ", Category.CLIENT, false, false, false);
    }

    public int GetX() {
        return imageX.getValue();
    }
    public int GetY() {
        return imageY.getValue();
    }
    public int SetWidth() {
        return 176;//Set
    }
    public int SetHeight() {
        return 67;//Set
    }

    public void renderLogo(DrawContext context){
            // Inventory Gui
            switch (InvMode.getValue()) {
                case Normal -> {
                    context.drawTexture(inventory, (int) GetX(), (int) GetY(), 0, 0, 0, 176, 67, 176, 67);
                }
                case Vanilla -> {
                    context.drawTexture(inventoryVanilla, (int) GetX(), (int) GetY(), 0, 0, 0, 176, 67, 176, 67);
                }
                case Black -> {
                    context.drawTexture(inventoryblack, (int) GetX(), (int) GetY(), 0, 0, 0, 176, 67, 176, 67);
                }
                case Transparent -> {
                    context.drawTexture(inventoryTransparent, (int) GetX(), (int) GetY(), 0, 0, 0, 176, 67, 176, 67);
                }
            }
        if (mc.player != null) {
            // Even more Cursed code but uh too lazy to fix it ¯\_(ツ)_/¯ (Your chance to pr a fix for this)

            // Row 1
            context.drawItem(mc.player.getInventory().getStack(9), (int) GetX() + 8, (int) GetY() + 7);
            context.drawItem(mc.player.getInventory().getStack(10), (int) GetX() + 26, (int) GetY() + 7);
            context.drawItem(mc.player.getInventory().getStack(11), (int) GetX() + 44, (int) GetY() + 7);
            context.drawItem(mc.player.getInventory().getStack(12), (int) GetX() + 62, (int) GetY() + 7);
            context.drawItem(mc.player.getInventory().getStack(13), (int) GetX() + 80, (int) GetY() + 7);
            context.drawItem(mc.player.getInventory().getStack(14), (int) GetX() + 98, (int) GetY() + 7);
            context.drawItem(mc.player.getInventory().getStack(15), (int) GetX() + 116, (int) GetY() + 7);
            context.drawItem(mc.player.getInventory().getStack(16), (int) GetX() + 134, (int) GetY() + 7);
            context.drawItem(mc.player.getInventory().getStack(17), (int) GetX() + 152, (int) GetY() + 7);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(9), (int) GetX() + 8, (int) GetY() + 7);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(10), (int) GetX() + 26, (int) GetY() + 7);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(11), (int) GetX() + 44, (int) GetY() + 7);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(12), (int) GetX() + 62, (int) GetY() + 7);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(13), (int) GetX() + 80, (int) GetY() + 7);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(14), (int) GetX() + 98, (int) GetY() + 7);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(15), (int) GetX() + 116, (int) GetY() + 7);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(16), (int) GetX() + 134, (int) GetY() + 7);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(17), (int) GetX() + 152, (int) GetY() + 7);
            // Row 2
            context.drawItem(mc.player.getInventory().getStack(18), (int) GetX() + 8, (int) GetY() + 25);
            context.drawItem(mc.player.getInventory().getStack(19), (int) GetX() + 26, (int) GetY() + 25);
            context.drawItem(mc.player.getInventory().getStack(20), (int) GetX() + 44, (int) GetY() + 25);
            context.drawItem(mc.player.getInventory().getStack(21), (int) GetX() + 62, (int) GetY() + 25);
            context.drawItem(mc.player.getInventory().getStack(22), (int) GetX() + 80, (int) GetY() + 25);
            context.drawItem(mc.player.getInventory().getStack(23), (int) GetX() + 98, (int) GetY() + 25);
            context.drawItem(mc.player.getInventory().getStack(24), (int) GetX() + 116, (int) GetY() + 25);
            context.drawItem(mc.player.getInventory().getStack(25), (int) GetX() + 134, (int) GetY() + 25);
            context.drawItem(mc.player.getInventory().getStack(26), (int) GetX() + 152, (int) GetY() + 25);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(18), (int) GetX() + 8, (int) GetY() + 25);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(19), (int) GetX() + 26, (int) GetY() + 25);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(20), (int) GetX() + 44, (int) GetY() + 25);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(21), (int) GetX() + 62, (int) GetY() + 25);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(22), (int) GetX() + 80, (int) GetY() + 25);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(23), (int) GetX() + 98, (int) GetY() + 25);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(24), (int) GetX() + 116, (int) GetY() + 25);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(25), (int) GetX() + 134, (int) GetY() + 25);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(26), (int) GetX() + 152, (int) GetY() + 25);
            // Row 3
            context.drawItem(mc.player.getInventory().getStack(27), (int) GetX() + 8, (int) GetY() + 43);
            context.drawItem(mc.player.getInventory().getStack(28), (int) GetX() + 26, (int) GetY() + 43);
            context.drawItem(mc.player.getInventory().getStack(29), (int) GetX() + 44, (int) GetY() + 43);
            context.drawItem(mc.player.getInventory().getStack(30), (int) GetX() + 62, (int) GetY() + 43);
            context.drawItem(mc.player.getInventory().getStack(31), (int) GetX() + 80, (int) GetY() + 43);
            context.drawItem(mc.player.getInventory().getStack(32), (int) GetX() + 98, (int) GetY() + 43);
            context.drawItem(mc.player.getInventory().getStack(33), (int) GetX() + 116, (int) GetY() + 43);
            context.drawItem(mc.player.getInventory().getStack(34), (int) GetX() + 134, (int) GetY() + 43);
            context.drawItem(mc.player.getInventory().getStack(35), (int) GetX() + 152, (int) GetY() + 43);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(27), (int) GetX() + 8, (int) GetY() + 43);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(28), (int) GetX() + 26, (int) GetY() + 43);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(29), (int) GetX() + 44, (int) GetY() + 43);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(30), (int) GetX() + 62, (int) GetY() + 43);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(31), (int) GetX() + 80, (int) GetY() + 43);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(32), (int) GetX() + 98, (int) GetY() + 43);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(33), (int) GetX() + 116, (int) GetY() + 43);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(34), (int) GetX() + 134, (int) GetY() + 43);
            context.drawItemInSlot(mc.textRenderer, mc.player.getInventory().getStack(35), (int) GetX() + 152, (int) GetY() + 43);
        }
            SetWidth();//176
            SetHeight();//67
    }


    private enum Mode {
        Normal,
        Vanilla, 
        Black,
        Transparent
    }
    
    //@Override
    public void onRender2D(final Render2DEvent event) {
        if (!Feature.fullNullCheck()) {
                this.renderLogo(event.getContext());
        }
    }
    
}
