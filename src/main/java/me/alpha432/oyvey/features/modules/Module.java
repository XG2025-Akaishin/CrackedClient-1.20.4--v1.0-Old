package me.alpha432.oyvey.features.modules;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.event.impl.ClientEvent;
import me.alpha432.oyvey.event.impl.Render2DEvent;
import me.alpha432.oyvey.event.impl.Render3DEvent;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.commands.Command;
import me.alpha432.oyvey.features.modules.client.notufy.Notifys;
import me.alpha432.oyvey.features.modules.combat.autototem.AutoTotem;
import me.alpha432.oyvey.features.settings.Bind;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.manager.CommandManager;
import me.alpha432.oyvey.manager.ConfigManager;
import me.alpha432.oyvey.util.futurepro.RenderUtils;
import me.alpha432.oyvey.util.traits.Jsonable;
import net.minecraft.network.packet.Packet;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Module extends Feature implements Jsonable {
    private final String description;
    private final Category category;
    public Setting<Boolean> enabled = this.register(new Setting<Boolean>("Enabled", false));
    public Setting<Boolean> drawn = this.register(new Setting<Boolean>("Drawn", true));
    public Setting<Bind> bind = this.register(new Setting<Bind>("Keybind", new Bind(-1)));
    public Setting<String> displayName;
    public boolean hasListener;
    public boolean alwaysListening;
    public boolean hidden;
    
    public Module(String name, String description, Category category, boolean hasListener, boolean hidden, boolean alwaysListening) {
        super(name);
        this.displayName = this.register(new Setting<>("DisplayName", name));
        this.description = description;
        this.category = category;
        this.hasListener = hasListener;
        this.hidden = hidden;
        this.alwaysListening = alwaysListening;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onToggle() {
    }

    public void onLoad() {
    }

    public void onTick() {
    }

    public void onUpdate() {
    }

    public void onRender2D(Render2DEvent event) {
    }

    public void onRender3D(Render3DEvent event) {
    }

    //Game Render Prticles etc
    public void onPreRender3D(MatrixStack stack) {
    }

    //name tags
    public void onRender2D(DrawContext drawContext, float tickDelta) {

	}

    //Render Other HoleESP
    public void onThread() {
    }

    //Item ESP
        public void onPreRender2D(DrawContext event) {
    }

    //Saturno Renders Future Client
    public void onRender(MatrixStack matrixStack, float partialTicks) {
	}

    public void onUnload() {
    }

    public String getDisplayInfo() {
        return null;
    }

    public boolean isOn() {
        return this.enabled.getValue();
    }

    public boolean isOff() {
        return !this.enabled.getValue();
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            this.enable();
        } else {
            this.disable();
        }
    }

    public void enable() {
        this.enabled.setValue(true);
        this.onToggle();
        this.onEnable();
        if (this.isOn() && this.hasListener && !this.alwaysListening) {
            EVENT_BUS.register(this);
        }
    }

    public void disable() {
        if (this.hasListener && !this.alwaysListening) {
            EVENT_BUS.unregister(this);
        }
        this.enabled.setValue(false);
        this.onToggle();
        this.onDisable();
    }

    public void toggle() {
        ClientEvent event = new ClientEvent(!this.isEnabled() ? 1 : 0, this);
        EVENT_BUS.post(event);
        if (!event.isCancelled()) {
            this.setEnabled(!this.isEnabled());
        }
    }

    public String getDisplayName() {
        return this.displayName.getValue();
    }

    public void setDisplayName(String name) {
        Module module = CrackedClient.moduleManager.getModuleByDisplayName(name);
        Module originalModule = CrackedClient.moduleManager.getModuleByName(name);
        if (module == null && originalModule == null) {
            Command.sendMessage(this.getDisplayName() + ", name: " + this.getName() + ", has been renamed to: " + name);
            this.displayName.setValue(name);
            return;
        }
        Command.sendMessage(Formatting.RED + "A module of this name already exists.");
    }

    @Override public boolean isEnabled() {
        return isOn();
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isDrawn() {
        return this.drawn.getValue();
    }

    public void setDrawn(boolean drawn) {
        this.drawn.setValue(drawn);
    }

    public Category getCategory() {
        return this.category;
    }

    public String getInfo() {
        return null;
    }

    public Bind getBind() {
        return this.bind.getValue();
    }

    /*
    public boolean isKeyPressed(int KeyCode) {//Test
        if (mc.currentScreen != null) return false;
        return this.bind.getValue() == new Bind(KeyCode);
    }*/

    public void setBind(int key) {
        this.bind.setValue(new Bind(key));
    }

    public boolean listening() {
        return this.hasListener && this.isOn() || this.alwaysListening;
    }

    public String getFullArrayString() {
        return this.getDisplayName() + Formatting.GRAY + (this.getDisplayInfo() != null ? " [" + Formatting.WHITE + this.getDisplayInfo() + Formatting.GRAY + "]" : "");
    }


    @Override public JsonElement toJson() {
        JsonObject object = new JsonObject();
        for (Setting<?> setting : getSettings()) {
            try {
                if (setting.getValue() instanceof Bind bind) {
                    object.addProperty(setting.getName(), bind.getKey());
                } else {
                    object.addProperty(setting.getName(), setting.getValueAsString());
                }
            } catch (Throwable e) {
            }
        }
        return object;
    }

    @Override public void fromJson(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        String enabled = object.get("Enabled").getAsString();
        if (Boolean.parseBoolean(enabled)) toggle();
        for (Setting<?> setting : getSettings()) {
            try {
                ConfigManager.setValueFromJson(this, setting, object.get(setting.getName()));
            } catch (Throwable throwable) {
            }
        }
    }


    public enum Category {
        COMBAT("Combat"),
        MISC("Misc"),
        RENDER("Render"),
        MOVEMENT("Movement"),
        PLAYER("Player"),
        CHAT("Chat"),
        CLIENT("Client");

        private final String name;

        Category(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
    //Messages Module
    public void sendMessage(String message) {
        if (fullNullCheck()) return;
        mc.player.sendMessage(Text.of(CommandManager.getClientMessageGlobal() + " " + Formatting.GRAY + "[" + Formatting.DARK_PURPLE + getDisplayName() + Formatting.GRAY + "] " + message));
    }
    //TNT Ext
    protected void sendPacket(Packet<?> packet) {
        if (mc.getNetworkHandler() == null) return;

        mc.getNetworkHandler().sendPacket(packet);
    }
    public void disable(String reason) {
        sendMessage(reason);
        disable();
    }

    //Totem AutoCrystal

    public static void clickSlot(int id) {
        if (id == -1 || mc.interactionManager == null || mc.player == null) return;
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, id, 0, SlotActionType.PICKUP, mc.player);
    }

    public static void clickSlot(int id, SlotActionType type) {
        if (id == -1 || mc.interactionManager == null || mc.player == null) return;
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, id, 0, type, mc.player);
    }
    public void debug(String message) {
        if (fullNullCheck() || !AutoTotem.getInstance().debug.getValue()) return;
        mc.player.sendMessage(Text.of(CommandManager.getClientMessageGlobal() + " " + Formatting.GRAY + "[" + Formatting.DARK_PURPLE + getDisplayName() + Formatting.GRAY + "] [\uD83D\uDD27] " + message));
    }

    public static void bobView(MatrixStack matrices, float tickDelta) {
        if (!(mc.getCameraEntity() instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity playerEntity = (PlayerEntity)mc.getCameraEntity();
        float g = -(playerEntity.horizontalSpeed + (playerEntity.horizontalSpeed - playerEntity.prevHorizontalSpeed) * tickDelta);
        float h = MathHelper.lerp(tickDelta, playerEntity.prevStrideDistance, playerEntity.strideDistance);
        matrices.translate(MathHelper.sin(g * (float)Math.PI) * h * 0.1f, -Math.abs(MathHelper.cos(g * (float)Math.PI) * h) * 0.3, 0.0f);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin(g * (float)Math.PI) * h * 3.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Math.abs(MathHelper.cos(g * (float)Math.PI - 0.2f) * h) * 0.3f));
    }

	public void sendNotify(String string){
	}
}
