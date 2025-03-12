package me.alpha432.oyvey.features.gui;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.gui.items.Item;
import me.alpha432.oyvey.features.gui.items.buttons.ModuleButton;
import me.alpha432.oyvey.features.gui.snows.Snow;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import com.mojang.blaze3d.systems.RenderSystem;

import static me.alpha432.oyvey.util.traits.Util.mc;

public class OyVeyGui extends Screen {
    private static OyVeyGui futureGui;
    private static OyVeyGui INSTANCE;
    private float scrollY;
    private final ArrayList<Component> components = new ArrayList();
    private ArrayList<Snow> _snowList = new ArrayList<Snow>();
    private final Identifier pic1 = new Identifier("textures/pic1.png");
    private final Identifier pic2 = new Identifier("textures/pic2.png");
    private final Identifier pic3 = new Identifier("textures/pic3.png");

    public OyVeyGui() {
        super(Text.literal("ClickGui"));
        setInstance();
        load();
    }

    public static OyVeyGui getInstance() {
        if (OyVeyGui.INSTANCE == null) {
            OyVeyGui.INSTANCE = new OyVeyGui();
        }
        return OyVeyGui.INSTANCE;
    }
    
    public static OyVeyGui getClickGui() {
        final OyVeyGui futureGui = OyVeyGui.futureGui;
        return getInstance();
    }
    
    private void setInstance() {
        OyVeyGui.INSTANCE = this;
    }

    private void load() {
        //Snow effect
        Random random = new Random();

        for (int i = 0; i < 100; ++i)
        {
            for (int y = 0; y < 3; ++y)
            {
                Snow snow = new Snow(25 * i, y * -50, random.nextInt(3) + 1, random.nextInt(2)+1);
                _snowList.add(snow);
            }
        }//Final


        int x = -84;
        for (final Module.Category category : CrackedClient.moduleManager.getCategories()) {
            this.components.add(new Component(category.getName(), x += 95, 4, true) {

                @Override
                public void setupItems() {
                    counter1 = new int[]{1};
                    CrackedClient.moduleManager.getModulesByCategory(category).forEach(module -> {
                        if (!module.hidden) {
                            this.addButton(new ModuleButton(module));
                        }
                    });
                }
            });
        }
        this.components.forEach(components -> components.getItems().sort(Comparator.comparing(Feature::getName)));
    }

    public void updateModule(Module module) {
        for (Component component : this.components) {
            for (Item item : component.getItems()) {
                if (!(item instanceof ModuleButton)) continue;
                ModuleButton button = (ModuleButton) item;
                Module mod = button.getModule();
                if (module == null || !module.equals(mod)) continue;
                button.initSettings();
            }
        }
    }

    public static void drawTexture(Identifier icon, float x, float y, int width, int height, DrawContext context) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        RenderSystem.texParameter(3553, 10240, 9729);
        RenderSystem.texParameter(3553, 10241, 9987);
        context.drawTexture(icon, 0, 0, 0.0F, 0.0F, width, height, width, height);
        context.getMatrices().pop();
    }

     public void render(DrawContext context, int mouseX, int mouseY, float delta) {//test
        /*Test*/net.minecraft.client.util.Window res = mc.getWindow();
        Item.context = context;
        context.fill(0, 0, context.getScaledWindowWidth(), context.getScaledWindowHeight(), new Color(ClickGui.getInstance().backgroundRed.getValue(), ClickGui.getInstance().backgroundGreen.getValue(), ClickGui.getInstance().backgroundBlue.getValue(), ClickGui.getInstance().backgroundAlpha.getValue()).hashCode());
        
//        context.drawTexture(texturelogo, (int) hx - 45, (int) hy + 10 , 0, 0, 80, 75, 80, 75);
        //Imports
        float hx = mc.getWindow().getScaledWidth() / 2f;
        float hy = mc.getWindow().getScaledHeight() / 2f;
        //Texture 1
        if (ClickGui.getInstance().pic1.getValue()){
        context.drawTexture(pic1, (int) hx - ClickGui.getInstance().pic1X.getValue(), (int) hy + ClickGui.getInstance().pic1Y.getValue() , 0, 0, ClickGui.getInstance().pic1width.getValue(), ClickGui.getInstance().pic1height.getValue(), ClickGui.getInstance().pic1width.getValue(), ClickGui.getInstance().pic1height.getValue());
        }
        //Texture 2
        if (ClickGui.getInstance().pic2.getValue()){
        context.drawTexture(pic2, (int) hx - ClickGui.getInstance().pic2X.getValue(), (int) hy + ClickGui.getInstance().pic2Y.getValue(), 0, 0, ClickGui.getInstance().pic2width.getValue(), ClickGui.getInstance().pic2height.getValue(), ClickGui.getInstance().pic2width.getValue(), ClickGui.getInstance().pic2height.getValue());
        }
        //Textire 3
        if (ClickGui.getInstance().pic3.getValue()){
        context.drawTexture(pic3, (int) hx - ClickGui.getInstance().pic3X.getValue(), (int) hy + ClickGui.getInstance().pic3Y.getValue(), 0, 0, ClickGui.getInstance().pic3width.getValue(), ClickGui.getInstance().pic3height.getValue(), ClickGui.getInstance().pic3width.getValue(), ClickGui.getInstance().pic3height.getValue());
        }
        //Continue
        this.components.forEach(components -> components.drawScreen(context, mouseX, mouseY, delta));

        if (!_snowList.isEmpty() && ClickGui.getInstance().Snowing.getValue())
        {
            _snowList.forEach(snow -> snow.Update(res, context));
        }
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int clickedButton) {
        this.components.forEach(components -> components.mouseClicked((int) mouseX, (int) mouseY, clickedButton));
        return super.mouseClicked(mouseX, mouseY, clickedButton);
    }

    @Override public boolean mouseReleased(double mouseX, double mouseY, int releaseButton) {
        this.components.forEach(components -> components.mouseReleased((int) mouseX, (int) mouseY, releaseButton));
        return super.mouseReleased(mouseX, mouseY, releaseButton);
    }

    /*@Override public boolean mouseScrolled(double mouseX, double mouseY, double dWheel) {
        if (dWheel < 0) {
            this.components.forEach(component -> component.setY(component.getY() - 10));
        } else if (dWheel > 0) {
            this.components.forEach(component -> component.setY(component.getY() + 10));
        }
        return this.mouseScrolled(mouseX, mouseY, dWheel);
    }*/
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount < 0) {
            this.components.forEach(component -> component.setY(component.getY() - 10));
        } else if (verticalAmount > 0) {
            this.components.forEach(component -> component.setY(component.getY() + 10));
        }
        scrollY += (int) (verticalAmount * 5D);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }


    @Override public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.components.forEach(component -> component.onKeyPressed(keyCode));
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override public boolean charTyped(char chr, int modifiers) {
        this.components.forEach(component -> component.onKeyTyped(chr, modifiers));
        return super.charTyped(chr, modifiers);
    }

    @Override public boolean shouldPause() {
        return false;
    }

    public final ArrayList<Component> getComponents() {
        return this.components;
    }

    public int getTextOffset() {
        return -6;
    }

    public Component getComponentByName(String name) {
        for (Component component : this.components) {
            if (!component.getName().equalsIgnoreCase(name)) continue;
            return component;
        }
        return null;
    }
}
