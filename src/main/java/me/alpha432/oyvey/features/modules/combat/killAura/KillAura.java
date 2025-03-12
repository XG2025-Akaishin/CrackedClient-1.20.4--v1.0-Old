package me.alpha432.oyvey.features.modules.combat.killAura;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.common.eventbus.Subscribe;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.event.impl.freelook.TickEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.combat.aura.utils.TimerUtils;
import me.alpha432.oyvey.features.modules.combat.killAura.utils.timer.CacheTimer;
import me.alpha432.oyvey.features.modules.combat.killAura.utils.timer.Timer;
import me.alpha432.oyvey.features.modules.misc.scaffol.Scaffold.OFFMain;
import me.alpha432.oyvey.features.modules.misc.scaffol.Scaffold.SwingSide;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class KillAura extends Module {
    private final Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Vanilla));
    private final Setting<SwingSide> swingSide = this.register(new Setting<SwingSide>("AttackType", SwingSide.Server));
    private final Setting<OFFMain> offMain = this.register(new Setting<OFFMain>("AttackType", OFFMain.MAIN_HAND));
    private final Setting<Boolean> armorCheck = this.register(new Setting<>("ArmorCheck", false));
    private final Setting<Boolean> autoSwap = this.register(new Setting<>("AutoSwap", true));
    private final Setting<Boolean> thirtyTwo = this.register(new Setting<>("32kDelay", true));
    private final Setting<Boolean> smartDelay = this.register(new Setting<>("SmartDelay", true));
    private final Setting<Integer> tickDelay = this.register(new Setting<Integer>("TickDelay", 100, 50, 1000));
    private final Setting<Integer> ticksExisted = this.register(new Setting<Integer>("TicksExisted", 0, 50, 200));
    private final Setting<AttackType> attackType = this.register(new Setting<AttackType>("AttackType", AttackType.DISTANCE));
    private final Setting<Float> range = this.register(new Setting<Float>("Range", 1.0f, 4.5f, 5.0f));
    private final Setting<Float> wallRange = this.register(new Setting<Float>("WallRange", 1.0f, 4.5f, 5.0f));
    private final Setting<Boolean> swordCheck = this.register(new Setting<>("Sword-Check", true));
    private final Setting<Boolean> players = this.register(new Setting<>("Players", true));
    private final Setting<Boolean> monsters = this.register(new Setting<>("Monsters", false));
    private final Setting<Boolean> neutrals = this.register(new Setting<>("Neutrals", false));
    private final Setting<Boolean> animals = this.register(new Setting<>("Animals", false));
    private final Setting<Boolean> invisibles = this.register(new Setting<>("Invisibles", true));
    private final Setting<Boolean> customHand = this.register(new Setting<>("CustomHand", true));
    //private final Setting<Boolean> render = this.register(new Setting<>("Render", true));CustomHand
    private final TimerUtils timer = new TimerUtils();

    public KillAura() {
        super("KillAura", "KillAura test", Category.COMBAT, true, false, false);
    }

    //private final Timer timerCache = new CacheTimer();
    //private List<Entity> targets = new ArrayList<>();
    private List<Entity> targets = new ArrayList<>();
    
    //@Subscribe
    @Override
    public void onUpdate() {
        if (nullCheck()) return;
        //targets = getEntities();
        //if (targets == null) return;
        //if (targets.isEmpty()) return;
        //targets.clear();
        if (!delayPassed()) return;
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity)) continue;
            if (((LivingEntity) entity).isDead()) continue;
                entity = getAttackTarget(entity.getPos());//vemos  filtro para los friends y exepciones de entidades a actacar
                if (attackTarget(entity)) {//verificamos que este la espada en el slot
                if (mode.getValue() == Mode.Vanilla) {
                    attackVanilla( ((LivingEntity) entity) );
                    timer.reset();
                } else if (mode.getValue() == Mode.Packet) {
                    attackPacked( ((LivingEntity) entity) );
                    timer.reset();
                } else {
                    mc.player.attack( ((LivingEntity) entity) );
                    mc.player.swingHand(Hand.MAIN_HAND);
                    mc.player.addCritParticles( ((LivingEntity) entity) );
                    timer.reset();
                }
            }
        }
    }

    private boolean delayPassed() {
        if (thirtyTwo.getValue() && is32k()) return true;
        if (smartDelay.getValue()) return mc.player.getAttackCooldownProgress(0.5f) >= 1;

        return timer.passedMillis(tickDelay.getValue());
    }

    private boolean is32k() {
        int level = EnchantmentHelper.getLevel(Enchantments.SHARPNESS, mc.player.getMainHandStack());
        return level >= 20;
    }

    public void attackPacked(LivingEntity target) {
        mc.player.resetLastAttackedTicks();
        //mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(target,mc.player.isSneaking()/*Test*/));
    }
    public void attackVanilla(LivingEntity target) {
        swingHand(Hand.MAIN_HAND, swingSide.getValue(), offMain.getValue());
        //mc.player.attack(target);
        mc.player.resetLastAttackedTicks();
        //mc.player.swingHand(Hand.MAIN_HAND);
        mc.player.addCritParticles(target);
    }

    private boolean attackTarget(Entity entity) {
        if (mc.player.isUsingItem() && mc.player.getActiveHand() == Hand.MAIN_HAND || mc.options.attackKey.isPressed()) {
            //timerCache.reset();
            return false;
        }
        boolean swordOff = mc.player.getOffHandStack().getItem() instanceof SwordItem;
        boolean swordMain = mc.player.getMainHandStack().getItem() instanceof SwordItem;
        if (autoSwap.getValue() &&/* timerCache.passed(500) &&*/ ( !swordMain || swordOff )) {//test
            int slot = getSwordSlot();
            if (slot != -1) {
                mc.player.getInventory().selectedSlot = slot;
                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
                mc.player.addCritParticles(entity);
            }
        }

        if (!isHoldingSword()) {
            return false;
        }
        return true;
    }

    public Entity getAttackTarget(Vec3d pos) {

        double min = Double.MAX_VALUE;
        Entity attackTarget = null;
        for (Entity entity : mc.world.getEntities()) {
            if (entity == null || entity == mc.player
                    || !entity.isAlive() || !isEnemy(entity)
                    || CrackedClient.friendManager.isFriend(entity.getName().toString()/*entity.getUuid()*/)
                    || entity instanceof EndCrystalEntity
                    || entity instanceof ItemEntity
                    || entity instanceof ArrowEntity
                    || entity instanceof ExperienceBottleEntity
                    || entity instanceof PlayerEntity player && player.isCreative()) {
                continue;
            }

        if (armorCheck.getValue() && entity instanceof LivingEntity && !entity.getArmorItems().iterator().hasNext()){
            continue;
        }

        double dist = pos.distanceTo(entity.getPos());
        if (isInAttackRange(dist, pos, entity)) {
            if (entity.age < ticksExisted.getValue()) {
                continue;
            }
            switch (attackType.getValue()) {
                case DISTANCE -> {
                    if (dist < min) {
                        min = dist;
                        attackTarget = entity;
                    }
                }
                case HEALTH -> {
                    if (entity instanceof LivingEntity e) {
                        float health = e.getHealth() + e.getAbsorptionAmount();
                        if (health < min) {
                            min = health;
                            attackTarget = entity;
                        }
                    }
                }
                case ARMOR -> {
                    if (entity instanceof LivingEntity e) {
                        float armor = getArmorDurability(e);
                        if (armor < min) {
                            min = armor;
                            attackTarget = entity;
                        }
                    }
                }
            }
        }
    }
    return attackTarget;
    }

    public boolean isInAttackRange(Vec3d pos, Entity entity)  {
        double dist = pos.distanceTo(entity.getPos());
        return isInAttackRange(dist, pos, entity);
    }

    public boolean isInAttackRange(double dist, Vec3d pos, Entity entity) {
        if (dist > range.getValue()) {
            return false;
        }
        BlockHitResult result = mc.world.raycast(new RaycastContext(pos, entity.getPos(),RaycastContext.ShapeType.COLLIDER,RaycastContext.FluidHandling.NONE, mc.player));
        if (result != null && dist > wallRange.getValue()) {
            return false;
        }
        return true;
    }

    /**
     * Verifica si los enemigos que se establescan
     * @param e
     * @return
     */
    private boolean isEnemy(Entity e) {
        return (!e.isInvisible() || invisibles.getValue()) && e instanceof PlayerEntity && players.getValue() || EntityUtil.isMonster(e) && monsters.getValue() || EntityUtil.isNeutral(e) && neutrals.getValue() || EntityUtil.isPassive(e) && animals.getValue();
    }


    /**
     * Encontrar el slot de inventario que contiene la espada con el mayor daño de ataque
     * @return
     */
    private int getSwordSlot() {
        float sharp = 0.0f;
        int slot = -1;
        // Maximize item attack damage
        for (int i = 0; i < 9; i++) {
            final ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof SwordItem swordItem)
            {
                float sharpness = EnchantmentHelper.getLevel(
                        Enchantments.SHARPNESS, stack) * 0.5f + 0.5f;
                float dmg = swordItem.getAttackDamage() + sharpness;
                if (dmg > sharp)
                {
                    sharp = dmg;
                    slot = i;
                }
            }
        }
        return slot;
    }

    private float getArmorDurability(LivingEntity e) {
        float edmg = 0.0f;
        float emax = 0.0f;
        for (ItemStack armor : e.getArmorItems()) {
            if (armor != null && !armor.isEmpty()) {
                edmg += armor.getDamage();
                emax += armor.getMaxDamage();
            }
        }
        return 100.0f - edmg / emax;
    }

    /**
     * Verifica si esta selecionado la enpada en la hotbar
     * retorna true o false
     * @return
     */
    public boolean isHoldingSword(){
        return !swordCheck.getValue() || mc.player.getMainHandStack().getItem() instanceof SwordItem;
    }

    public void swingHand(Hand hand, SwingSide side , OFFMain modeOffMain) {
        boolean isCustom = customHand.getValue();

        if(!isCustom) {
            switch (side) {
                case All -> mc.player.swingHand(hand);
                case Client -> mc.player.swingHand(hand, false);
                case Server -> mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(hand));
            }
        }else{
            Hand custom = null;
            if(modeOffMain == OFFMain.MAIN_HAND){
                custom = Hand.MAIN_HAND;
            }
            else if(modeOffMain == OFFMain.OFF_HAND){
                custom = Hand.OFF_HAND;
            }
            if(custom==null){
                return;
            }
            switch (side) {
                case All -> mc.player.swingHand(custom);
                case Client -> mc.player.swingHand(custom, false);
                case Server -> mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(custom));
            }
        }
    }

    public enum OFFMain {
        MAIN_HAND,
        OFF_HAND
    }

    public enum SwingSide {
        All,
        Client,
        Server
    }

    public enum AttackType {
        DISTANCE,
        HEALTH,
        ARMOR
    }

    public enum Mode {
        Packet,
        Vanilla
    }

public static class EntityUtil {
    public static boolean isMonster(Entity e) {
        return e instanceof Monster;
    }
    public static boolean isNeutral(Entity e) {
        return e instanceof Angerable && !((Angerable) e).hasAngerTime();
    }
    public static boolean isPassive(Entity e){
        return e instanceof PassiveEntity || e instanceof AmbientEntity || e instanceof SquidEntity;
    }

    public static boolean isVehicle(Entity e) {
        return e instanceof BoatEntity || e instanceof MinecartEntity || e instanceof FurnaceMinecartEntity || e instanceof ChestMinecartEntity;
    }
}
      
      /*
    public LivingEntity returnTargetEntity(List<LivingEntity> list) {
        LivingEntity output = null;
        for (LivingEntity entity : list) {
          float distanceTo = entity.getDistance(mc.player);
          boolean isCloser = (output == null || distanceTo < output.getDistance(mc.player));
          boolean inRange = (distanceTo <= Math.floor(this.range.getValue()));
          boolean isProjectile = entity instanceof ProjectileEntity;
          boolean isPlayer = entity instanceof PlayerEntity;
          boolean isMob = entity instanceof net.minecraft.entity.mob.Mob;
          boolean outputIsPlayer = output instanceof PlayerEntity;
          if (!inRange)
            continue; 
          if (distanceTo <= 2.0F && isProjectile) {
            output = entity;
            break;
          } 
          if (outputIsPlayer && isPlayer && isCloser) {
            output = entity;
            continue;
          } 
          if (isPlayer) {
            output = entity;
            continue;
          } 
          if (isMob && isCloser) {
            output = entity;
            continue;
          } 
          if (isCloser)
            output = entity; 
        } 
        return output;
      }*/
}
