package me.alpha432.oyvey.features.modules.misc.fakeplayercracked;

import net.minecraft.block.Blocks;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.explosion.Explosion;
import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.combat.autocrystal.AutoCrystal;
import me.alpha432.oyvey.features.modules.misc.fakeplayercracked.utils.BlockUtil;
import me.alpha432.oyvey.features.modules.misc.fakeplayercracked.utils.Timer;
import me.alpha432.oyvey.features.settings.Setting;

import java.util.Objects;
import java.util.UUID;

import com.google.common.eventbus.Subscribe;
import com.mojang.authlib.GameProfile;

public class FakePlayerCracked extends Module {
	public static FakePlayerCracked INSTANCE;

    public FakePlayerCracked() {
        super("FakePlayerCracked", "FakePlayerCracked FutureClient", Category.MISC, true, false, false);
        INSTANCE = this;
    }

    private final Setting<String> name  = this.register(new Setting<String>("Name", "Cracked"));
    private final Setting<Boolean> damagec  = this.register(new Setting<Boolean>("Damage", true));
    private final Setting<Boolean> autoTotem  = this.register(new Setting<Boolean>("AutoTotem", true));
    private final Setting<Boolean> gApple  = this.register(new Setting<Boolean>("GApple", true));

	public static OtherClientPlayerEntity fakePlayer;

	private final Timer timer = new Timer();

	@Override
	public String getInfo() {
		return name.getValue();
	}

	@Override
	public void onEnable() {
		pops = 0;
		if (nullCheck()) {
			disable();
			return;
		}
		fakePlayer = new OtherClientPlayerEntity(mc.world, new GameProfile(UUID.fromString("11451466-6666-6666-6666-666666666600"), name.getValue()));
		fakePlayer.getInventory().clone(mc.player.getInventory());
		mc.world.addEntity(fakePlayer);
		fakePlayer.copyPositionAndRotation(mc.player);
		fakePlayer.bodyYaw = mc.player.bodyYaw;
		fakePlayer.headYaw = mc.player.headYaw;
		fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 9999, 2));
		fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 9999, 4));
		fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 9999, 1));
	}

	int pops = 0;
	@Override
	public void onUpdate() {
		if (!(fakePlayer != null && !fakePlayer.isDead() && fakePlayer.clientWorld == mc.world)) {
			disable();
			return;
		}
		fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 9999, 2));
		fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 9999, 4));
		if (gApple.getValue()) {
			if (timer.passedMs(4000)) {
				fakePlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 9999, 1));
				timer.reset();
				fakePlayer.setAbsorptionAmount(16);
			}
		}
		if (autoTotem.getValue() && fakePlayer.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
			CrackedClient.popManager.onTotemPop(fakePlayer);
			fakePlayer.setStackInHand(Hand.OFF_HAND, new ItemStack(Items.TOTEM_OF_UNDYING));
		}
		if (fakePlayer.isDead()) {
			if (fakePlayer.tryUseTotem(mc.world.getDamageSources().generic())) {
				fakePlayer.setHealth(10f);
				new EntityStatusS2CPacket(fakePlayer, EntityStatuses.USE_TOTEM_OF_UNDYING).apply(mc.player.networkHandler);
			}
		}
	}

	@Override
	public void onDisable() {
		if (fakePlayer == null) return;
		fakePlayer.kill();
		fakePlayer.setRemoved(Entity.RemovalReason.KILLED);
		fakePlayer.onRemoved();
		fakePlayer = null;
	}

    public float damage = 0f;
	@Subscribe
	public void onPacketReceive(PacketEvent.Receive event) {
		if (damagec.getValue() && fakePlayer != null && fakePlayer.hurtTime == 0) {
			if (autoTotem.getValue() && fakePlayer.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
				fakePlayer.setStackInHand(Hand.OFF_HAND, new ItemStack(Items.TOTEM_OF_UNDYING));
			}
			if (event.getPacket() instanceof ExplosionS2CPacket explosion) {
				if (MathHelper.sqrt((float) new Vec3d(explosion.getX(), explosion.getY(), explosion.getZ()).squaredDistanceTo(fakePlayer.getPos())) > 10) return;
					//damage = AutoCrystal.getInstance().calculateDamage(new Vec3d(explosion.getX(), explosion.getY(), explosion.getZ()), fakePlayer, fakePlayer);
                    damage = getExplosionDamageWPredict(new Vec3d(explosion.getX(), explosion.getY(), explosion.getZ()), fakePlayer, fakePlayer);
				fakePlayer.onDamaged(mc.world.getDamageSources().generic());
				if (fakePlayer.getAbsorptionAmount() >= damage) {
					fakePlayer.setAbsorptionAmount(fakePlayer.getAbsorptionAmount() - damage);
				} else {
					float damage2 = damage - fakePlayer.getAbsorptionAmount();
					fakePlayer.setAbsorptionAmount(0);
					fakePlayer.setHealth(fakePlayer.getHealth() - damage2);
				}
			}
			if (fakePlayer.isDead()) {
				if (fakePlayer.tryUseTotem(mc.world.getDamageSources().generic())) {
					fakePlayer.setHealth(10f);
					new EntityStatusS2CPacket(fakePlayer, EntityStatuses.USE_TOTEM_OF_UNDYING).apply(mc.player.networkHandler);
				}
			}
		}
	}

/*
    private float getExplosionDamage2(Vec3d crysPos, PlayerEntity target) {
        try {
            return getExplosionDamageWPredict(crysPos, target, predictPlayer(target, 3));
        } catch (Exception ignored) {
        }
        return 0f;
    }
*/

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
/*
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
*/
}