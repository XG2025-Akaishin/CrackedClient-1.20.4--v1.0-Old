package me.alpha432.oyvey.features.modules.combat.killAura;


import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
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
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Random;

import com.google.common.eventbus.Subscribe;
import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
//import me.alpha432.oyvey.manager.tick.TickSync;
import me.alpha432.oyvey.features.modules.combat.killAura.utils.timer.Timer;
import me.alpha432.oyvey.features.modules.combat.Criticals;
import me.alpha432.oyvey.features.modules.combat.autocrystal.AutoCrystal;
import me.alpha432.oyvey.features.modules.combat.killAura.utils.timer.CacheTimer;

public class AuraModule extends Module {

    //Setting<Boolean> swingConfig = this.register(new Setting<>("Swing", "Swings the hand after attacking", true));
    private final Setting<Boolean> swingConfig = this.register(new Setting<>("Swing", true));
    // RANGES
    //Config<TargetMode> modeConfig = new EnumConfig<>("Mode", "The mode for " + "targeting entities to attack", TargetMode.SWITCH, TargetMode.values());
    private final Setting<TargetMode> modeConfig = this.register(new Setting<TargetMode>("Mode", TargetMode.SWITCH));
    //Config<Priority> priorityConfig = new EnumConfig<>("Priority", "The " +"heuristic to prioritize when searching for targets",Priority.HEALTH, Priority.values());
    private final Setting<Priority> priorityConfig = this.register(new Setting<Priority>("Priority", Priority.HEALTH));
    //Config<Float> rangeConfig = new NumberConfig<>("Range", "Range to attack " +"entities", 1.0f, 4.5f, 5.0f);
    private final Setting<Float> rangeConfig = this.register(new Setting<Float>("Range", 1.0f, 4.5f, 5.0f));
    //Config<Float> wallRangeConfig = new NumberConfig<>("WallRange", "Range to" +" attack entities through walls", 1.0f, 4.5f, 5.0f);
    private final Setting<Float> wallRangeConfig = this.register(new Setting<Float>("WallRange", 1.0f, 4.5f, 5.0f));
    //Config<Float> fovConfig = new NumberConfig<>("FOV", "Field of view to " +"attack entities", 1.0f, 180.0f, 180.0f);
    private final Setting<Float> fovConfig = this.register(new Setting<Float>("FOV", 1.0f, 180.0f, 180.0f));
    //Config<Boolean> latencyPositionConfig = new BooleanConfig("LatencyPosition", "Targets the latency positions of enemies", false);
    private final Setting<Boolean> latencyPositionConfig = this.register(new Setting<>("LatencyPosition", false));
    //Config<Integer> maxLatencyConfig = new NumberConfig<>("MaxLatency","Maximum latency factor when calculating positions", 50, 250,1000, () -> latencyPositionConfig.getValue());
    private final Setting<Integer> maxLatencyConfig = this.register(new Setting<Integer>("MaxLatency", 50, 250,1000));
    //Config<Boolean> attackDelayConfig = new BooleanConfig("AttackDelay","Delays attacks according to minecraft hit delays for maximum " +"damage per attack", false);
    private final Setting<Boolean> attackDelayConfig = this.register(new Setting<>("AttackDelay", false));
    //Config<Float> attackSpeedConfig = new NumberConfig<>("AttackSpeed","Delay for attacks (Only functions if AttackDelay is off)", 1.0f,20.0f, 20.0f, () -> !attackDelayConfig.getValue());
    private final Setting<Float> attackSpeedConfig = this.register(new Setting<Float>("AttackSpeed", 1.0f,20.0f, 20.0f));
    //Config<Float> randomSpeedConfig = new NumberConfig<>("RandomSpeed","Randomized delay for attacks (Only functions if AttackDelay is " +"off)", 0.0f, 0.0f, 10.0f,() -> !attackDelayConfig.getValue());
    private final Setting<Float> randomSpeedConfig = this.register(new Setting<Float>("RandomSpeed", 0.0f, 0.0f, 10.0f));
    //Config<Integer> packetsConfig = new NumberConfig<>("Packets", "Maximum " +"attack packets to send in a single tick", 0, 1, 20);
    private final Setting<Integer> packetsConfig = this.register(new Setting<Integer>("Packets", 0, 1, 20));
    //Config<Float> swapDelayConfig = new NumberConfig<>("SwapPenalty", "Delay " +"for attacking after swapping items which prevents NCP flags", 0.0f, 0.0f, 10.0f);
    private final Setting<Float> swapDelayConfig = this.register(new Setting<Float>("SwapPenalty", 0.0f, 0.0f, 10.0f));
    //Config<TickSync> tpsSyncConfig = new EnumConfig<>("TPS-Sync", "Syncs the " +"attacks with the server TPS", TickSync.NONE, TickSync.values());
    //private final Setting<TickSync> tpsSyncConfig = this.register(new Setting<TickSync>("TPS-Sync", TickSync.NONE));
    //Config<Boolean> autoSwapConfig = new BooleanConfig("AutoSwap","Automatically swaps to a weapon before attacking", true);
    private final Setting<Boolean> autoSwapConfig = this.register(new Setting<>("AutoSwap", true));
    //Config<Boolean> swordCheckConfig = new BooleanConfig("Sword-Check","Checks if a weapon is in the hand before attacking", true);
    private final Setting<Boolean> swordCheckConfig = this.register(new Setting<>("Sword-Check", true));
    // ROTATE
    //Config<Vector> hitVectorConfig = new EnumConfig<>("HitVector", "The " +"vector to aim for when attacking entities", Vector.FEET,Vector.values());
    private final Setting<Vector> hitVectorConfig = this.register(new Setting<Vector>("HitVector", Vector.FEET));
    //Config<Boolean> rotateConfig = new BooleanConfig("Rotate", "Rotate" +"before attacking", false);
    private final Setting<Boolean> rotateConfig = this.register(new Setting<>("Rotate", false));
    //Config<Boolean> strictRotateConfig = new BooleanConfig("RotateStrict","Rotates yaw over multiple ticks to prevent certain rotation  " +"flags in NCP", false, () -> rotateConfig.getValue());
    private final Setting<Boolean> strictRotateConfig = this.register(new Setting<>("RotateStrict", false));
    //Config<Integer> rotateLimitConfig = new NumberConfig<>("RotateLimit", "Maximum yaw rotation in degrees for one tick",1, 180, 180, NumberDisplay.DEGREES,() -> rotateConfig.getValue() && strictRotateConfig.getValue());
    private final Setting<Integer> rotateLimitConfig = this.register(new Setting<Integer>("RotateLimit", 1, 180, 180));
    //Config<Integer> yawTicksConfig = new NumberConfig<>("YawTicks","Minimum ticks to rotate yaw", 1, 1, 5,() -> rotateConfig.getValue() && strictRotateConfig.getValue());
    private final Setting<Integer> yawTicksConfig = this.register(new Setting<Integer>("YawTicks", 1, 1, 5));
    //Config<Integer> rotateTimeoutConfig = new NumberConfig<>("RotateTimeout", "Minimum ticks to hold the rotation yaw after " +"reaching the rotation", 0, 0, 5, () -> rotateConfig.getValue());
    private final Setting<Integer> rotateTimeoutConfig = this.register(new Setting<Integer>("RotateTimeout", 0, 0, 5));
    //Config<Integer> ticksExistedConfig = new NumberConfig<>("TicksExisted","The minimum age of the entity to be considered for attack", 0, 50, 200);
    private final Setting<Integer> ticksExistedConfig = this.register(new Setting<Integer>("TicksExisted", 0, 50, 200));
    //Config<Boolean> armorCheckConfig = new BooleanConfig("ArmorCheck","Checks if target has armor before attacking", false);
    private final Setting<Boolean> armorCheckConfig = this.register(new Setting<>("ArmorCheck", false));
    //Config<Boolean> autoBlockConfig = new BooleanConfig("AutoBlock","Automatically blocks after attack", false);
    private final Setting<Boolean> autoBlockConfig = this.register(new Setting<>("AutoBlock", false));
    //Config<Boolean> stopSprintConfig = new BooleanConfig("StopSprint","Stops sprinting before attacking to maintain vanilla behavior", false);
    private final Setting<Boolean> stopSprintConfig = this.register(new Setting<>("StopSprint", false));
    //Config<Boolean> stopShieldConfig = new BooleanConfig("StopShield","Automatically handles shielding before attacking", false);
    private final Setting<Boolean> stopShieldConfig = this.register(new Setting<>("StopShield", false));
    //
    //Config<Boolean> playersConfig = new BooleanConfig("Players","Target players", true);
    //Config<Boolean> monstersConfig = new BooleanConfig("Monsters","Target monsters", false);
    //Config<Boolean> neutralsConfig = new BooleanConfig("Neutrals","Target neutrals", false);
    //Config<Boolean> animalsConfig = new BooleanConfig("Animals","Target animals", false);
    //Config<Boolean> invisiblesConfig = new BooleanConfig("Invisibles","Target invisible entities", true);
    //Config<Boolean> renderConfig = new BooleanConfig("Render","Renders an indicator over the target", true);

    private final Setting<Boolean> playersConfig = this.register(new Setting<>("Players", true));
    private final Setting<Boolean> monstersConfig = this.register(new Setting<>("Monsters", false));
    private final Setting<Boolean> neutralsConfig = this.register(new Setting<>("Neutrals", false));
    private final Setting<Boolean> animalsConfig = this.register(new Setting<>("Animals", false));
    private final Setting<Boolean> invisiblesConfig = this.register(new Setting<>("Invisibles", true));
    private final Setting<Boolean> renderConfig = this.register(new Setting<>("Render", true));
    //
    private Entity entityTarget;
    private final Timer attackTimer = new CacheTimer();
    private final Timer critTimer = new CacheTimer();
    private final Timer autoSwapTimer = new CacheTimer();
    //private final Timer switchTimer = new CacheTimer();
    private long randomDelay = -1;
    //
    //private int rotating;


    public AuraModule()
    {
        super("KillAura", "KillAura test", Category.COMBAT, true, false, false);
    }

    @Override
    public void onDisable()
    {
        entityTarget = null;
        //rotating = 0;
    }

    @Subscribe
    public void onUpdate() {//PlayerUpdateEvent
        //if (event.getStage() != EventStage.PRE)
        //{
        //    return;
        //}
        //if (AutoCrystal.getInstance().isAttacking() ||AutoCrystal.getInstance().isPlacing() || rotating > 0){return;}
        final Vec3d eyepos = mc.player.getEyePos();
        switch (modeConfig.getValue())
        {
            case SWITCH -> entityTarget = getAttackTarget(eyepos);
            case SINGLE ->
            {
                if (entityTarget == null || !entityTarget.isAlive()
                        || !isInAttackRange(eyepos, entityTarget))
                {
                    entityTarget = getAttackTarget(eyepos);
                }
            }
        }
        if (entityTarget == null /*|| !switchTimer.passed(swapDelayConfig.getValue() * 25.0f)*/)
        {
            return;
        }/* 
        if (rotateConfig.getValue())
        {
            float[] rotation = RotationUtil.getRotationsTo(mc.player.getEyePos(),
                    getAttackRotateVec(entityTarget));
            setRotation(rotation[0], rotation[1]);
        }
        if (isRotationBlocked())
        {
            return;
        }*/
        /*
        if (attackDelayConfig.getValue()) {
            float ticks = 20.0f - tpsSyncConfig.getValue();
            float progress = mc.player.getAttackCooldownProgress(ticks);
            if (progress >= 1.0f && attackTarget(entityTarget))
            {
                mc.player.resetLastAttackedTicks();
            }
        }*/
        //else
        {
            if (randomDelay < 0) {
                randomDelay = (long) new Random().nextFloat(
                        (randomSpeedConfig.getValue() * 10.0f) + 1.0f);
            }
            float delay = (attackSpeedConfig.getValue() * 50.0f) + randomDelay;
            if (attackTimer.passed(1000.0f - delay) && attackTarget(entityTarget))
            {
                randomDelay = -1;
                attackTimer.reset();
            }
        }
    }

    private boolean attackTarget(Entity entity)
    {
        if (mc.player.isUsingItem() && mc.player.getActiveHand() == Hand.MAIN_HAND
                || mc.options.attackKey.isPressed())
        {
            autoSwapTimer.reset();
            return false;
        }
        // END PRE
        boolean sword = mc.player.getMainHandStack().getItem() instanceof SwordItem;
        if (autoSwapConfig.getValue() && autoSwapTimer.passed(500) && !sword)
        {
            int slot = getSwordSlot();
            if (slot != -1)
            {
                mc.player.getInventory().selectedSlot = slot;
                sendPacket(new UpdateSelectedSlotC2SPacket(slot));
            }
        }
        if (!isHoldingSword())
        {
            return false;
        }
        preAttackTarget();
        // preMotionAttackTarget();
        sendPacket(PlayerInteractEntityC2SPacket.attack(entity,mc.player.isSneaking()));
        if (swingConfig.getValue())
        {
            mc.player.swingHand(Hand.MAIN_HAND);
        }
        else
        {
            sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
        }
        postAttackTarget(entity);
        return true;
    }

    private int getSwordSlot()
    {
        float sharp = 0.0f;
        int slot = -1;
        // Maximize item attack damage
        for (int i = 0; i < 9; i++)
        {
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

    private boolean shielding;
    private boolean sneaking;
    private boolean sprinting;

    private void preAttackTarget() {
        final ItemStack offhand = mc.player.getOffHandStack();
        // Shield state
        shielding = false;
        if (stopShieldConfig.getValue())
        {
            shielding = offhand.getItem() == Items.SHIELD
                    && mc.player.isBlocking();
            if (shielding)
            {
                sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, mc.player.getBlockPos(), Direction.getFacing(mc.player.getX(),mc.player.getY(), mc.player.getZ())));
            }
        }
        sneaking = false;
        sprinting = false;
        if (stopSprintConfig.getValue())
        {
            sneaking = mc.player.isSneaking();
            if (sneaking)
            {
                sendPacket(new ClientCommandC2SPacket(mc.player,
                        ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
            }
            sprinting = mc.player.isSprinting();
            if (sprinting)
            {
                sendPacket(new ClientCommandC2SPacket(mc.player,
                        ClientCommandC2SPacket.Mode.STOP_SPRINTING));
            }
        }
    }

    // RELEASE
    private void postAttackTarget(Entity entity)
    {
        /*if (shielding)
        {
            sendSequencedPacket(s ->
                    new PlayerInteractItemC2SPacket(Hand.OFF_HAND, s));
        }*/
        if (sneaking)
        {
            sendPacket(new ClientCommandC2SPacket(mc.player,
                    ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        }
        if (sprinting)
        {
            sendPacket(new ClientCommandC2SPacket(mc.player,
                    ClientCommandC2SPacket.Mode.START_SPRINTING));
        }
        if (Criticals.getInstance().isEnabled() && critTimer.passed(500))
        {
            if (!mc.player.isOnGround()
                    || mc.player.isRiding()
                    || mc.player.isSubmergedInWater()
                    || mc.player.isInLava()
                    || mc.player.isHoldingOntoLadder()
                    || mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
                    || mc.player.input.jumping)
            {
                return;
            }
            Criticals.getInstance().doCritical();//preAttackPacket();
            critTimer.reset();
            mc.player.addCritParticles(entity);
        }
    }

    /**
     *
     * @param pos
     * @return
     */
    private Entity getAttackTarget(Vec3d pos)
    {
        double min = Double.MAX_VALUE;
        Entity attackTarget = null;
        for (Entity entity : mc.world.getEntities())
        {
            if (entity == null || entity == mc.player
                    || !entity.isAlive() || !isEnemy(entity)
                    || CrackedClient.friendManager.isFriend(entity.getName().toString())
                    || entity instanceof EndCrystalEntity
                    || entity instanceof ItemEntity
                    || entity instanceof ArrowEntity
                    || entity instanceof ExperienceBottleEntity
                    || entity instanceof PlayerEntity player && player.isCreative())
            {
                continue;
            }
            if (armorCheckConfig.getValue()
                    && entity instanceof LivingEntity
                    && !entity.getArmorItems().iterator().hasNext())
            {
                continue;
            }
            double dist = pos.distanceTo(entity.getPos());
            if (isInAttackRange(dist, pos, entity))
            {
                if (entity.age < ticksExistedConfig.getValue())
                {
                    continue;
                }
                switch (priorityConfig.getValue())
                {
                    case DISTANCE ->
                    {
                        if (dist < min)
                        {
                            min = dist;
                            attackTarget = entity;
                        }
                    }
                    case HEALTH ->
                    {
                        if (entity instanceof LivingEntity e)
                        {
                            float health = e.getHealth() + e.getAbsorptionAmount();
                            if (health < min)
                            {
                                min = health;
                                attackTarget = entity;
                            }
                        }
                    }
                    case ARMOR ->
                    {
                        if (entity instanceof LivingEntity e)
                        {
                            float armor = getArmorDurability(e);
                            if (armor < min)
                            {
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

    /**
     *
     * @param e
     * @return
     */
    private float getArmorDurability(LivingEntity e)
    {
        float edmg = 0.0f;
        float emax = 0.0f;
        for (ItemStack armor : e.getArmorItems())
        {
            if (armor != null && !armor.isEmpty())
            {
                edmg += armor.getDamage();
                emax += armor.getMaxDamage();
            }
        }
        return 100.0f - edmg / emax;
    }

    /**
     *
     * @param pos
     * @param entity
     * @return
     */
    public boolean isInAttackRange(Vec3d pos, Entity entity)
    {
        double dist = pos.distanceTo(entity.getPos());
        return isInAttackRange(dist, pos, entity);
    }

    /**
     *
     * @param dist
     * @param pos
     * @param entity
     * @return
     */
    public boolean isInAttackRange(double dist, Vec3d pos, Entity entity)
    {
        if (dist > rangeConfig.getValue())
        {
            return false;
        }
        BlockHitResult result = mc.world.raycast(new RaycastContext(
                pos, entity.getPos(),
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE, mc.player));
        if (result != null && dist > wallRangeConfig.getValue())
        {
            return false;
        }
        return true;
    }

    /**
     *
     * @return
     */
    public boolean isHoldingSword()
    {
        return !swordCheckConfig.getValue() || mc.player.getMainHandStack().getItem() instanceof SwordItem;
    }

    private Vec3d getAttackRotateVec(Entity entity)
    {
        Vec3d feetPos = entity.getPos();
        return switch (hitVectorConfig.getValue())
        {
            case FEET -> feetPos;
            case TORSO -> feetPos.add(0.0, entity.getHeight() / 2.0f, 0.0);
            case EYES -> feetPos.add(0.0,
                    entity.getStandingEyeHeight(), 0.0);
        };
    }

    /**
     * Returns the number of ticks the rotation will last after setting the
     * player rotations to the dest rotations. Yaws are only calculated in
     * this method, the yaw will not be updated until the main
     * loop runs.
     *
     * @param dest The rotation
     * @return
     */
    private float[] getLimitRotation(float dest)
    {
        int tick;
        float[] yawLimits;
        if (strictRotateConfig.getValue())
        {
            float diff = dest - 4; // yaw diff
            float magnitude = Math.abs(diff);
            if (magnitude > 180.0f)
            {
                diff += diff > 0.0f ? -360.0f : 360.0f;
            }
            final int dir = diff > 0.0f ? 1 : -1;
            tick = yawTicksConfig.getValue();
            // partition yaw
            float deltaYaw = magnitude / tick;
            if (deltaYaw > rotateLimitConfig.getValue())
            {
                tick = MathHelper.ceil(magnitude / rotateLimitConfig.getValue());
                deltaYaw = magnitude / tick;
            }
            deltaYaw *= dir;
            int yawCount = tick;
            tick += rotateTimeoutConfig.getValue();
            yawLimits = new float[tick];
            int off = tick - 1;
            float yawTotal = 0.0f;
            for (int i = 0; i < tick; ++i)
            {
                if (i > yawCount)
                {
                    yawLimits[off - i] = 0.0f;
                    continue;
                }
                yawTotal += deltaYaw;
                yawLimits[off - i] = yawTotal;
            }
        }
        else
        {
            tick = rotateTimeoutConfig.getValue() + 1;
            yawLimits = new float[tick];
            int off = tick - 1;
            yawLimits[off] = dest;
            for (int i = 1; i < tick; ++i)
            {
                yawLimits[off - i] = 0.0f;
            }
        }
        return yawLimits;
    }

    /**
     * Returns <tt>true</tt> if the {@link Entity} is a valid enemy to attack.
     *
     * @param e The potential enemy entity
     * @return <tt>true</tt> if the entity is an enemy
     *
     * @see EntityUtil
     */
    private boolean isEnemy(Entity e)
    {
        return (!e.isInvisible() || invisiblesConfig.getValue())
                && e instanceof PlayerEntity && playersConfig.getValue()
                || EntityUtil.isMonster(e) && monstersConfig.getValue()
                || EntityUtil.isNeutral(e) && neutralsConfig.getValue()
                || EntityUtil.isPassive(e) && animalsConfig.getValue();
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

    public enum TargetMode
    {
        SWITCH,
        SINGLE
    }

    public enum Vector
    {
        EYES,
        TORSO,
        FEET
    }

    public enum Priority
    {
        HEALTH,
        DISTANCE,
        ARMOR
    }
}
