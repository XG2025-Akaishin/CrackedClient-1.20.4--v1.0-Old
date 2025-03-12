package me.alpha432.oyvey.features.modules.combat.aura;

import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.Subscribe;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.event.impl.freelook.TickEvent;
import me.alpha432.oyvey.features.modules.combat.Criticals;
import me.alpha432.oyvey.features.modules.combat.aura.utils.TimerUtils;
import static me.alpha432.oyvey.features.modules.combat.aura.utils.AUtils.processAttack;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.manager.FriendManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.SwordItem;
import me.alpha432.oyvey.features.modules.Module;
import java.util.Comparator;

public class Aura extends Module {

    public Aura() {
        super("Aura", "AutoArmor ou", Category.COMBAT, true, false, false);
    }

    public Setting<Weapon> weapon = this.register(new Setting<>("Weapon", Weapon.Any));
    public Setting<Boolean> smartDelay = this.register(new Setting<>("SmartDelay", true));
    public Setting<Integer> actionDelay = this.register(new Setting<>("ActionDelay", 0, 500, 1000));
    public Setting<Integer> attackRange = this.register(new Setting<>("AttackRange", 6, 1, 16));
    public Setting<Boolean> thirtyTwo = this.register(new Setting<>("32k", true));
    public Setting<Boolean> rotate = this.register(new Setting<>("Rotate", false));

    public Setting<Boolean> players = this.register(new Setting<>("Players", true));
    public Setting<Boolean> monsters = this.register(new Setting<>("Monsters", true));
    public Setting<Boolean> animals = this.register(new Setting<>("Animals", true));
    public Setting<Boolean> passive = this.register(new Setting<>("Passive", false));
    public Setting<Boolean> villagers = this.register(new Setting<>("Villagers", false));
    public Setting<Boolean> nametaged = this.register(new Setting<>("Nametaged", false));

    public Entity target;
    private List<Entity> targets = new ArrayList<>();
    private final TimerUtils timer = new TimerUtils();

    //@Override
    public void onEnable() {
        target = null;
    }

    @Subscribe
    public void onTick(TickEvent.Post event) {
        targets = getEntities();
        if (targets == null) return;//test return world null
        if (targets.isEmpty()) return;

        if (!delayPassed()) return;
        if (!holdsWeapon()) return;

        target = targets.get(0);

        doAttack(target);
        //setDisplayInfo(target.getName().getString());
    }

    @Override
    public String getDisplayInfo() {
        return weapon.getValue().name();
    }

    private List<Entity> getEntities() {
        targets.clear();
        if (mc.world == null) return null;//test world null
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity)) continue;
            if (((LivingEntity) entity).isDead()) continue;

            if (mc.player.distanceTo(entity) <= attackRange.getValue()) targets.add(entity);
        }

        targets.removeIf(this::badTarget);
        targets.sort(Comparator.comparingDouble(this::distance));
        return targets;
    }

    private double distance(Entity entity) {
        return mc.player.distanceTo(entity);
    }

    private boolean badTarget(Entity entity) {
        if (entity == null) return true;
        if (entity.equals(mc.player) || entity.equals(mc.cameraEntity)) return true;

        if (!passive.getValue()) {
            if (entity instanceof EndermanEntity enderman && !enderman.isAngry()) return true;
            if (entity instanceof Tameable tameable && tameable.getOwnerUuid() != null && tameable.getOwnerUuid().equals(mc.player.getUuid()))
                return true;
            if (entity instanceof MobEntity mob && !mob.isAttacking() && !(entity instanceof PhantomEntity))
                return true;
        }

        if (entity instanceof PlayerEntity player) {
            if (CrackedClient.friendManager.isFriend(player)) return true;
            if (player.isCreative() || player.isSpectator()) return true;

            return !players.getValue();
        }
        if (entity instanceof Monster) return !monsters.getValue();
        if (entity instanceof AnimalEntity) return !animals.getValue();
        if (entity instanceof VillagerEntity) return !villagers.getValue();

        return !nametaged.getValue() && entity.hasCustomName();
    }

    private boolean holdsWeapon() {
        return switch (weapon.getValue()) {
            case Sword -> mc.player.getMainHandStack().getItem() instanceof SwordItem;
            case Axe -> mc.player.getMainHandStack().getItem() instanceof AxeItem;
            case Both -> mc.player.getMainHandStack().getItem() instanceof SwordItem || mc.player.getMainHandStack().getItem() instanceof AxeItem;
            case Any -> true;
        };
    }

    private boolean delayPassed() {
        if (thirtyTwo.getValue() && is32k()) return true;
        if (smartDelay.getValue()) return mc.player.getAttackCooldownProgress(0.5f) >= 1;

        return timer.passedMillis(actionDelay.getValue());
    }

    private boolean is32k() {
        int level = EnchantmentHelper.getLevel(Enchantments.SHARPNESS, mc.player.getMainHandStack());

        return level >= 20;
    }

    private void doAttack(Entity entity) {
        if (Criticals.getInstance().isEnabled()) Criticals.getInstance().doCritical();
        //if (rotate.getValue()) Rotations.rotate(Rotations.getYaw(entity), 0);//rotate remove
        processAttack(entity);

        timer.reset();
    }

    public enum Weapon {
        Sword, Axe, Both, Any
    }
}
