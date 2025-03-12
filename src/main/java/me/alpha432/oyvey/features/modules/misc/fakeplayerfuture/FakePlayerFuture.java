package me.alpha432.oyvey.features.modules.misc.fakeplayerfuture;

import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.NotNull;

import com.google.common.eventbus.Subscribe;
import com.mojang.authlib.GameProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.event.impl.fakeplayerfuture.AttackEntityEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;

import me.alpha432.oyvey.mixin.fakeplayerfuture.ILivingEntity;

public class FakePlayerFuture extends Module {
    public FakePlayerFuture() {
        super("FakePlayer", "FakePlayer FutureClient", Category.MISC, true, false, false);
    }

    private int movementTick, deathTime;
    private long TICK_TIMER;

    public static OtherClientPlayerEntity fakePlayer;
    private final List<PlayerState> positions = new ArrayList<>();


    @Override
    public void onEnable() {
        if (Module.nullCheck()) return;
        TICK_TIMER = System.currentTimeMillis();

        fakePlayer = new OtherClientPlayerEntity(mc.world, new GameProfile(UUID.fromString("66123666-6666-6666-6666-666666666600"), "AnarchyGooDFake"));
        fakePlayer.copyPositionAndRotation(mc.player);

        fakePlayer.setStackInHand(Hand.MAIN_HAND, mc.player.getMainHandStack().copy());
        fakePlayer.setStackInHand(Hand.OFF_HAND, mc.player.getOffHandStack().copy());

        fakePlayer.getInventory().setStack(36, mc.player.getInventory().getStack(36).copy());
        fakePlayer.getInventory().setStack(37, mc.player.getInventory().getStack(37).copy());
        fakePlayer.getInventory().setStack(38, mc.player.getInventory().getStack(38).copy());
        fakePlayer.getInventory().setStack(39, mc.player.getInventory().getStack(39).copy());

        mc.world.addEntity(fakePlayer);
        fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 9999, 2));
        fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 9999, 4));
        fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 9999, 1));
    }

    @Subscribe
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof ExplosionS2CPacket explosion && fakePlayer != null && fakePlayer.hurtTime == 0) {
            fakePlayer.onDamaged(mc.world.getDamageSources().generic());
            fakePlayer.setHealth(fakePlayer.getHealth() + fakePlayer.getAbsorptionAmount() - getExplosionDamage2(new Vec3d(explosion.getX(), explosion.getY(), explosion.getZ()), fakePlayer));
            if (fakePlayer.isDead()) {
                if (fakePlayer.tryUseTotem(mc.world.getDamageSources().generic())) {
                    fakePlayer.setHealth(10f);
                    new EntityStatusS2CPacket(fakePlayer, EntityStatuses.USE_TOTEM_OF_UNDYING).apply(mc.player.networkHandler);
                }
            }
        }
    }

    private PlayerEntity equipAndReturn(PlayerEntity original, Vec3d posVec) {
        PlayerEntity copyEntity = new PlayerEntity(mc.world, original.getBlockPos(), original.getYaw(), new GameProfile(UUID.fromString("66123666-1234-5432-6666-667563866600"), "PredictEntity339")) {
            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return false;
            }
        };

        copyEntity.setPosition(posVec);
        copyEntity.setHealth(original.getHealth());
        copyEntity.prevX = original.prevX;
        copyEntity.prevZ = original.prevZ;
        copyEntity.prevY = original.prevY;
        copyEntity.getInventory().clone(original.getInventory());
        for (StatusEffectInstance se : original.getStatusEffects()) {
            copyEntity.addStatusEffect(se);
        }

        return copyEntity;
    }

    private PlayerEntity predictPlayer(PlayerEntity entity, int ticks) {
        Vec3d posVec = new Vec3d(entity.getX(), entity.getY(), entity.getZ());
        double motionX = entity.getX() - entity.prevX;
        double motionY = entity.getY() - entity.prevY;
        double motionZ = entity.getZ() - entity.prevZ;

        for (int i = 0; i < ticks; i++) {
            if (!mc.world.isAir(BlockPos.ofFloored(posVec.add(0, motionY, 0)))) {
                motionY = 0;
            }
            if (!mc.world.isAir(BlockPos.ofFloored(posVec.add(motionX, 0, 0))) || !mc.world.isAir(BlockPos.ofFloored(posVec.add(motionX, 1, 0)))) {
                motionX = 0;
            }
            if (!mc.world.isAir(BlockPos.ofFloored(posVec.add(0, 0, motionZ))) || !mc.world.isAir(BlockPos.ofFloored(posVec.add(0, 1, motionZ)))) {
                motionZ = 0;
            }
            posVec = posVec.add(motionX, motionY, motionZ);

        }

        return equipAndReturn(entity, posVec);
    }

    private float getExplosionDamage2(Vec3d crysPos, PlayerEntity target) {
        try {
            return getExplosionDamageWPredict(crysPos, target, predictPlayer(target, 3));
        } catch (Exception ignored) {
        }
        return 0f;
    }

    private float getExplosionDamageWPredict(Vec3d explosionPos, PlayerEntity target, PlayerEntity predict) {
        if (mc.world.getDifficulty() == Difficulty.PEACEFUL) return 0f;

        Explosion explosion = new Explosion(mc.world, null, explosionPos.x, explosionPos.y, explosionPos.z, 6f, false, Explosion.DestructionType.DESTROY);
        if (!new Box(
                MathHelper.floor(explosionPos.x - 11d),
                MathHelper.floor(explosionPos.y - 11d),
                MathHelper.floor(explosionPos.z - 11d),
                MathHelper.floor(explosionPos.x + 13d),
                MathHelper.floor(explosionPos.y + 13d),
                MathHelper.floor(explosionPos.z + 13d)).intersects(predict.getBoundingBox())
        ) {
            return 0f;
        }

        if (!target.isImmuneToExplosion(explosion) && !target.isInvulnerable()) {
            double distExposure = MathHelper.sqrt((float) predict.squaredDistanceTo(explosionPos)) / 12d;
            if (distExposure <= 1.0) {
                double xDiff = predict.getX() - explosionPos.x;
                double yDiff = predict.getY() - explosionPos.y;
                double zDiff = predict.getX() - explosionPos.z;
                double diff = MathHelper.sqrt((float) (xDiff * xDiff + yDiff * yDiff + zDiff * zDiff));
                if (diff != 0.0) {
                    double exposure = Explosion.getExposure(explosionPos, predict);
                    double finalExposure = (1.0 - distExposure) * exposure;

                    float toDamage = (float) Math.floor((finalExposure * finalExposure + finalExposure) / 2.0 * 7.0 * 12d + 1.0);

                    if (mc.world.getDifficulty() == Difficulty.EASY) {
                        toDamage = Math.min(toDamage / 2f + 1f, toDamage);
                    } else if (mc.world.getDifficulty() == Difficulty.HARD) {
                        toDamage = toDamage * 3f / 2f;
                    }

                    toDamage = DamageUtil.getDamageLeft(toDamage, target.getArmor(), (float) Objects.requireNonNull(target.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)).getValue());

                    if (target.hasStatusEffect(StatusEffects.RESISTANCE)) {
                        int resistance = 25 - (Objects.requireNonNull(target.getStatusEffect(StatusEffects.RESISTANCE)).getAmplifier() + 1) * 5;
                        float resistance_1 = toDamage * resistance;
                        toDamage = Math.max(resistance_1 / 25f, 0f);
                    }

                    if (toDamage <= 0f) {
                        toDamage = 0f;
                    } else {
                        int protAmount = EnchantmentHelper.getProtectionAmount(target.getArmorItems(), mc.world.getDamageSources().explosion(explosion));
                        if (protAmount > 0) {
                            toDamage = DamageUtil.getInflictedDamage(toDamage, protAmount);
                        }
                    }
                    return toDamage;
                }
            }
        }
        return 0f;
    }

    @Override
    public void onUpdate() {
        if (Module.nullCheck()) return;
        if (fakePlayer != null) {
            movementTick = 0;

            if (fakePlayer.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
                fakePlayer.setStackInHand(Hand.OFF_HAND, new ItemStack(Items.TOTEM_OF_UNDYING));
            }

            if (fakePlayer.isDead()) {
                deathTime++;
                if (deathTime > 10) {
                    toggle();
                }
            }
        }
    }

    private float getAttackCooldownProgressPerTick() {
        return (float) (1.0 / mc.player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED) * (20.0 * TICK_TIMER));
    }

    private float getAttackCooldown() {
        return MathHelper.clamp(((float) ((ILivingEntity) mc.player).getLastAttackedTicks() + 0.5f) / getAttackCooldownProgressPerTick(), 0.0F, 1.0F);
    }

    @Subscribe
    public void onAttack(AttackEntityEvent event) {
        if (fakePlayer != null && event.entity == fakePlayer && fakePlayer.hurtTime == 0) {
            mc.world.playSound(mc.player, fakePlayer.getX(), fakePlayer.getY(), fakePlayer.getZ(), SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 1f, 1f);

            fakePlayer.onDamaged(mc.world.getDamageSources().generic());
            if (getAttackCooldown() >= 0.85) {
                fakePlayer.setHealth(fakePlayer.getHealth() + fakePlayer.getAbsorptionAmount() - getHitDamage(mc.player.getMainHandStack(), fakePlayer));
            } else {
                fakePlayer.setHealth(fakePlayer.getHealth() + fakePlayer.getAbsorptionAmount() - 1f);
            }
            if (fakePlayer.isDead()) {
                if (fakePlayer.tryUseTotem(mc.world.getDamageSources().generic())) {
                    fakePlayer.setHealth(10f);
                    new EntityStatusS2CPacket(fakePlayer, EntityStatuses.USE_TOTEM_OF_UNDYING).apply(mc.player.networkHandler);
                    //event.isCancelled();//Test
                }
            }
        }
    }

    private float getHitDamage(@NotNull ItemStack weapon, PlayerEntity ent) {
        if (mc.player == null) return 0;
        float baseDamage = 1f;

        if (weapon.getItem() instanceof SwordItem swordItem)
            baseDamage = swordItem.getAttackDamage();

        if (weapon.getItem() instanceof AxeItem axeItem)
            baseDamage = axeItem.getAttackDamage();

        baseDamage += EnchantmentHelper.getLevel(Enchantments.SHARPNESS, weapon);

        if (mc.player.hasStatusEffect(StatusEffects.STRENGTH)) {
            int strength = Objects.requireNonNull(mc.player.getStatusEffect(StatusEffects.STRENGTH)).getAmplifier() + 1;
            baseDamage += 3 * strength;
        }

        // Reduce by resistance
        // baseDamage = resistanceReduction(target, damage);

        // Reduce by armour
        baseDamage = DamageUtil.getDamageLeft(baseDamage, ent.getArmor(), (float) ent.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).getValue());

        // Reduce by enchants
        // damage = normalProtReduction(target, damage);

        return baseDamage;
    }

    @Override
    public void onDisable() {
        if (fakePlayer == null) return;
        fakePlayer.kill();
        fakePlayer.setRemoved(Entity.RemovalReason.KILLED);
        fakePlayer.onRemoved();
        fakePlayer = null;
        positions.clear();
        deathTime = 0;
    }


    private record PlayerState(double x, double y, double z, float yaw, float pitch) {
    }
}
