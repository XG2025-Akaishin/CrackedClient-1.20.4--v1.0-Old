package me.alpha432.oyvey.manager;

import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.alpha432.oyvey.event.impl.Render2DEvent;
import me.alpha432.oyvey.event.impl.Render3DEvent;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.futuregui.FutureGuiOpen;
import me.alpha432.oyvey.features.gui.OyVeyGui;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.chat.CrackedPop;
import me.alpha432.oyvey.features.modules.chat.antispam.AntiSpam;
import me.alpha432.oyvey.features.modules.chat.chatdiscord.ChatDiscord;
import me.alpha432.oyvey.features.modules.chat.chatencrypt.ChatEncrypted;
import me.alpha432.oyvey.features.modules.chat.chatmodifier.ChatModifier;
import me.alpha432.oyvey.features.modules.chat.commandv.CustomCommand;
import me.alpha432.oyvey.features.modules.chat.commandv.MessageSpammer;
import me.alpha432.oyvey.features.modules.chat.moduletools.ModuleTools;
import me.alpha432.oyvey.features.modules.chat.notifications.Notifications;
import me.alpha432.oyvey.features.modules.client.Cape;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.modules.client.Colors;
import me.alpha432.oyvey.features.modules.client.FutureGui;
import me.alpha432.oyvey.features.modules.client.HUD;
import me.alpha432.oyvey.features.modules.client.Inventory;
import me.alpha432.oyvey.features.modules.client.Language;
import me.alpha432.oyvey.features.modules.client.Logo;
import me.alpha432.oyvey.features.modules.client.RPC;
import me.alpha432.oyvey.features.modules.combat.Criticals;
import me.alpha432.oyvey.features.modules.combat.aura.Aura;
import me.alpha432.oyvey.features.modules.combat.autoarmor.AutoArmor;
import me.alpha432.oyvey.features.modules.combat.autocrystal.AutoCrystal;
import me.alpha432.oyvey.features.modules.combat.autototem.AutoTotem;
import me.alpha432.oyvey.features.modules.combat.burrow.Burrow;
import me.alpha432.oyvey.features.modules.combat.killAura.AuraModule;
import me.alpha432.oyvey.features.modules.combat.killAura.KillAura;
import me.alpha432.oyvey.features.modules.combat.surrawnd.Surround;
import me.alpha432.oyvey.features.modules.combat.tntaura.TNTAura;
import me.alpha432.oyvey.features.modules.misc.AutoBedDupe;
import me.alpha432.oyvey.features.modules.misc.AutoFrameDupe;
import me.alpha432.oyvey.features.modules.misc.EntityNotifier;
import me.alpha432.oyvey.features.modules.misc.MCF;
import me.alpha432.oyvey.features.modules.misc.PortalChat;
import me.alpha432.oyvey.features.modules.misc.Timer;
import me.alpha432.oyvey.features.modules.misc.autoauth.AutoAuth;
import me.alpha432.oyvey.features.modules.misc.autoframedupe.FrameDupeTest;
import me.alpha432.oyvey.features.modules.misc.autosaldupe.AutoSalDupe;
import me.alpha432.oyvey.features.modules.misc.fakegamemode.GameModeFake;
import me.alpha432.oyvey.features.modules.misc.fakeplayer.FakePlayer;
import me.alpha432.oyvey.features.modules.misc.fakeplayercracked.FakePlayerCracked;
import me.alpha432.oyvey.features.modules.misc.fakeplayerfuture.FakePlayerFuture;
import me.alpha432.oyvey.features.modules.misc.fakevanilla.FakeVanilla;
import me.alpha432.oyvey.features.modules.misc.mountbypass.MountBypass;
import me.alpha432.oyvey.features.modules.misc.music.MusicSound;
import me.alpha432.oyvey.features.modules.misc.scaffol.Scaffold;
import me.alpha432.oyvey.features.modules.misc.stashfinder.StashFinder;
import me.alpha432.oyvey.features.modules.misc.titlebrack.TileBreaker;
import me.alpha432.oyvey.features.modules.movement.AutoWalk;
import me.alpha432.oyvey.features.modules.movement.FlightNew;
import me.alpha432.oyvey.features.modules.movement.InvMove;
import me.alpha432.oyvey.features.modules.movement.JetPack;
import me.alpha432.oyvey.features.modules.movement.ReverseStep;
import me.alpha432.oyvey.features.modules.movement.Speed;
import me.alpha432.oyvey.features.modules.movement.Step;
import me.alpha432.oyvey.features.modules.movement.StorageSrch;
import me.alpha432.oyvey.features.modules.movement.autosprint.AutoSprint;
import me.alpha432.oyvey.features.modules.movement.autosprint.Sprint;
import me.alpha432.oyvey.features.modules.movement.elytrafly.ElytraFly;
import me.alpha432.oyvey.features.modules.movement.fastfall.FastFall;
import me.alpha432.oyvey.features.modules.movement.flight.Flight;
import me.alpha432.oyvey.features.modules.movement.icespeed.IceSpeed;
import me.alpha432.oyvey.features.modules.movement.jesus.Jesus;
import me.alpha432.oyvey.features.modules.movement.nofall.NoFall;
import me.alpha432.oyvey.features.modules.movement.noslow.NoSlow;
import me.alpha432.oyvey.features.modules.player.AutoRespawn;
import me.alpha432.oyvey.features.modules.player.FastPlace;
import me.alpha432.oyvey.features.modules.player.VelocityExplosion;
import me.alpha432.oyvey.features.modules.player.ViewLock;
import me.alpha432.oyvey.features.modules.player.XCarry;
import me.alpha432.oyvey.features.modules.player.fastuse.FastUse;
import me.alpha432.oyvey.features.modules.player.hotbarreplenish.HotbarReplenish;
import me.alpha432.oyvey.features.modules.player.middleclick.MiddleClick;
import me.alpha432.oyvey.features.modules.player.velocity.Velocity;
import me.alpha432.oyvey.features.modules.render.AspectRatio;
import me.alpha432.oyvey.features.modules.render.FreeLook;
import me.alpha432.oyvey.features.modules.render.HandModifier;
import me.alpha432.oyvey.features.modules.render.Zoom;
import me.alpha432.oyvey.features.modules.render.ZoomPro;
import me.alpha432.oyvey.features.modules.render.breakhighlight.BreakHighLight;
import me.alpha432.oyvey.features.modules.render.cameraclip.NoCameraClip;
import me.alpha432.oyvey.features.modules.render.chams.Chams;
import me.alpha432.oyvey.features.modules.render.entityesp.EntityESP;
import me.alpha432.oyvey.features.modules.render.fov.FOV;
import me.alpha432.oyvey.features.modules.render.fullbright.Fullbright;
import me.alpha432.oyvey.features.modules.render.holesp.HoleESP;
import me.alpha432.oyvey.features.modules.render.itemesp.ItemESP;
import me.alpha432.oyvey.features.modules.render.itemesphobos.PhobosItemEsp;
import me.alpha432.oyvey.features.modules.render.nametags.NameTags;
import me.alpha432.oyvey.features.modules.render.nobob.NoBob;
import me.alpha432.oyvey.features.modules.render.norender.NoRender;
import me.alpha432.oyvey.features.modules.render.particles.Particles;
import me.alpha432.oyvey.features.modules.render.popchams.PopChams;
import me.alpha432.oyvey.features.modules.render.shaders.Shaders;
import me.alpha432.oyvey.features.modules.render.storagesp.StorageEsp;
import me.alpha432.oyvey.features.modules.render.time.Time;
import me.alpha432.oyvey.features.modules.render.tooltips.Tooltips;
import me.alpha432.oyvey.features.modules.render.totemanimation.TotemAnimation;
import me.alpha432.oyvey.features.modules.render.totemparticle.TotemParticle;
import me.alpha432.oyvey.features.modules.render.tracer.Tracers;
import me.alpha432.oyvey.features.modules.render.twodesp.TwoDESP;
import me.alpha432.oyvey.features.modules.render.worldtweaks.WorldTweaks;
import me.alpha432.oyvey.features.notifications.notificationd.NotificationManager;
import me.alpha432.oyvey.features.modules.misc.antiattack.AntiAttack;
import me.alpha432.oyvey.features.modules.misc.armornoftify.ArmorNotify;
import me.alpha432.oyvey.util.futurepro.RenderUtils;
import me.alpha432.oyvey.util.traits.Jsonable;
import me.alpha432.oyvey.util.traits.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;

public class ModuleManager implements Jsonable, Util  {
    public List<Module> modules = new ArrayList<>();
    public List<Module> sortedModules = new ArrayList<>();
    public List<Module> alphabeticallySortedModules = new ArrayList<Module>();
    public List<String> sortedModulesABC = new ArrayList<>();

    public void init() {
        modules.add(new Chams());
        modules.add(new Scaffold());
        modules.add(new TotemAnimation());
        modules.add(new TwoDESP());
        modules.add(new FakePlayerCracked());
        modules.add(new ZoomPro());
        modules.add(new TotemParticle());
        modules.add(new PopChams());
        modules.add(new NameTags());
        modules.add(new IceSpeed());
        modules.add(new ArmorNotify());
        modules.add(new PhobosItemEsp());
        modules.add(new MusicSound());
        modules.add(new Language());
        modules.add(new FakePlayerFuture());
        modules.add(new NoRender());
        modules.add(new Jesus());
        modules.add(new EntityESP());
        modules.add(new TileBreaker());
        modules.add(new NoFall());
        modules.add(new FastFall());
        modules.add(new Speed());
        modules.add(new ElytraFly());
        modules.add(new GameModeFake());
        modules.add(new AntiSpam());
        modules.add(new AntiAttack());
        modules.add(new BreakHighLight());
        modules.add(new StashFinder());
        modules.add(new FakeVanilla());
        modules.add(new Aura());
        modules.add(new HUD());
        modules.add(new AutoWalk());
        modules.add(new TNTAura());
        modules.add(new AutoSprint());
        modules.add(new InvMove());
        modules.add(new FrameDupeTest());
        modules.add(new ClickGui());
        modules.add(new Criticals());
        modules.add(new FreeLook());
        modules.add(new HandModifier());
        modules.add(new AutoAuth());
        modules.add(new FastUse());
        modules.add(new MCF());
        modules.add(new Step());
        modules.add(new ReverseStep());
        modules.add(new FastPlace());
        modules.add(new VelocityExplosion());
        modules.add(new HotbarReplenish());
        modules.add(new ViewLock());
        modules.add(new XCarry());
        modules.add(new AspectRatio());
        modules.add(new NoSlow());
        modules.add(new FOV());
        modules.add(new CrackedPop());
        modules.add(new CustomCommand());
        modules.add(new MessageSpammer());
        modules.add(new PortalChat());
        modules.add(new Cape());
        modules.add(new AutoCrystal());
        modules.add(new AutoTotem());
        modules.add(new ChatModifier());
        modules.add(new ModuleTools());
        modules.add(new Notifications());
        modules.add(new AutoArmor());
        modules.add(new FakePlayer());
        modules.add(new Flight());
        modules.add(new Fullbright());
        modules.add(new Sprint());
        modules.add(new Velocity());
        modules.add(new NoCameraClip());
        modules.add(new AutoFrameDupe());
        modules.add(new EntityNotifier());
        modules.add(new Burrow());
        modules.add(new AutoRespawn());
        modules.add(new MiddleClick());
        modules.add(new RPC());
        modules.add(new AutoBedDupe());
        modules.add(new WorldTweaks());
        modules.add(new Particles());
        modules.add(new Tooltips());
        modules.add(new HoleESP());
        modules.add(new Tracers());
        modules.add(new Shaders());
        modules.add(new Time());
        modules.add(new NotificationManager());
        modules.add(new Logo());
        modules.add(new Inventory());
        modules.add(new AutoSalDupe());
        modules.add(new MountBypass());
        modules.add(new Colors());
        modules.add(new FutureGui());
        modules.add(new Zoom());
        modules.add(new ChatEncrypted());
        modules.add(new ItemESP());
        modules.add(new StorageEsp());
        modules.add(new ChatDiscord()); 
        modules.add(new NoBob()); 
        modules.add(new FlightNew());
        modules.add(new StorageSrch());
        modules.add(new JetPack());
        modules.add(new Surround());
        modules.add(new Timer());
    }

    public Module getModuleByName(String name) {
        for (Module module : this.modules) {
            if (!module.getName().equalsIgnoreCase(name)) continue;
            return module;
        }
        return null;
    }

    public <T extends Module> T getModuleByClass(Class<T> clazz) {
        for (Module module : this.modules) {
            if (!clazz.isInstance(module)) continue;
            return (T) module;
        }
        return null;
    }

    public void enableModule(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        if (module != null) {
            module.enable();
        }
    }

    public void disableModule(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        if (module != null) {
            module.disable();
        }
    }

    public void enableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.enable();
        }
    }

    public void disableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.disable();
        }
    }

    public boolean isModuleEnabled(String name) {
        Module module = this.getModuleByName(name);
        return module != null && module.isOn();
    }

    public boolean isModuleEnabled(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        return module != null && module.isOn();
    }

    public Module getModuleByDisplayName(String displayName) {
        for (Module module : this.modules) {
            if (!module.getDisplayName().equalsIgnoreCase(displayName)) continue;
            return module;
        }
        return null;
    }

    public ArrayList<Module> getEnabledModules() {
        ArrayList<Module> enabledModules = new ArrayList<>();
        for (Module module : this.modules) {
            if (!module.isEnabled()) continue;
            enabledModules.add(module);
        }
        return enabledModules;
    }

    public ArrayList<String> getEnabledModulesName() {
        ArrayList<String> enabledModules = new ArrayList<>();
        for (Module module : this.modules) {
            if (!module.isEnabled() || !module.isDrawn()) continue;
            enabledModules.add(module.getFullArrayString());
        }
        return enabledModules;
    }

    public ArrayList<Module> getModulesByCategory(Module.Category category) {
        ArrayList<Module> modulesCategory = new ArrayList<Module>();
        this.modules.forEach(module -> {
            if (module.getCategory() == category) {
                modulesCategory.add(module);
            }
        });
        return modulesCategory;
    }

    public void render(MatrixStack matrixStack) {//New Implementation Saturno Renders
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		matrixStack.push();
		RenderUtils.applyRenderOffset(matrixStack);
		for(Module module : modules) {
			if(module.isOn()) {
				module.onRender(matrixStack, MinecraftClient.getInstance().getTickDelta());
			}
		}
		matrixStack.pop();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}

    public List<Module.Category> getCategories() {
        return Arrays.asList(Module.Category.values());
    }

    public void onLoad() {
        this.modules.stream().filter(Module::listening).forEach(EVENT_BUS::register);
        this.modules.forEach(Module::onLoad);
    }

    public void onUpdate() {
        this.modules.stream().filter(Feature::isEnabled).forEach(Module::onUpdate);
    }

    public void onTick() {
        this.modules.stream().filter(Feature::isEnabled).forEach(Module::onTick);
    }

    public void onRender2D(Render2DEvent event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender2D(event));
    }

    public void onRender3D(Render3DEvent event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender3D(event));
    }
    //Name Tags
    public void render2DNameTag(DrawContext drawContext) {
		modules.stream().filter(Module::isOn).forEach(module -> module.onRender2D(drawContext, MinecraftClient.getInstance().getTickDelta()));
	}

    //Render GameCamera Particles etc
    public void onPreRender3D(MatrixStack matrixStack) {
        this.modules.stream().filter(Module::isEnabled).forEach(module -> module.onPreRender3D(matrixStack));
    }
    //Render ItemESP
    public void onPreRender2D(DrawContext context) {
        modules.stream().filter(Module::isEnabled).forEach(module -> module.onPreRender2D(context));
        //ThunderHack.core.onRender2D(context);
    }
    public void sortModules(boolean reverse) {
        this.sortedModules = this.getEnabledModules().stream().filter(Module::isDrawn)
                .sorted(Comparator.comparing(module -> mc.textRenderer.getWidth(module.getFullArrayString()) * (reverse ? -1 : 1)))
                .collect(Collectors.toList());
    }

    public void alphabeticallySortModules() {
        this.alphabeticallySortedModules = this.getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(Module::getDisplayName)).collect(Collectors.toList());
    }

    public void sortModulesABC() {
        this.sortedModulesABC = new ArrayList<>(this.getEnabledModulesName());
        this.sortedModulesABC.sort(String.CASE_INSENSITIVE_ORDER);
    }

    public void onUnload() {
        this.modules.forEach(EVENT_BUS::unregister);
        this.modules.forEach(Module::onUnload);
    }

    public void onUnloadPost() {
        for (Module module : this.modules) {
            module.enabled.setValue(false);
        }
    }
    
    //@Subscribe
    public void onKeyPressed(int eventKey) {
        if (eventKey <= 0 || ModuleManager.mc.currentScreen instanceof OyVeyGui || ModuleManager.mc.currentScreen instanceof FutureGuiOpen) return;
        this.modules.forEach(module -> {
            if (module.getBind().getKey() == eventKey) {//isKeyPressed     if (module.getBind().getKey() == eventKey) {
                module.toggle();
            }
        });
    }

    @Override public JsonElement toJson() {
        JsonObject object = new JsonObject();
        for (Module module : modules) {
            object.add(module.getName(), module.toJson());
        }
        return object;
    }

    @Override public void fromJson(JsonElement element) {
        for (Module module : modules) {
            module.fromJson(element.getAsJsonObject().get(module.getName()));
        }
    }

    @Override public String getFileName() {
        return "modules.json";
    }

    public void registerModule(Module module) {
        if (module == null) return;

        this.modules.add(module);

        if (module.listening()) {
            EVENT_BUS.register(module);
        }
    }

}
