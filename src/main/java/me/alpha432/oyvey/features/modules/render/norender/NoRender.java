package me.alpha432.oyvey.features.modules.render.norender;

import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.ElderGuardianAppearanceParticle;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.event.impl.norender.ParticleEvent;
import me.alpha432.oyvey.event.impl.norender.UpdateWalkingEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.render.fov.FOV;
import me.alpha432.oyvey.features.settings.Setting;

import java.lang.reflect.Field;

import com.google.common.eventbus.Subscribe;

public class NoRender extends Module {
    private static NoRender INSTANCE = new NoRender();
	public final Setting<Boolean> potions = this.register(new Setting<>("Potions", true));
	public final Setting<Boolean> xp = this.register(new Setting<>("XP", true));
	public final Setting<Boolean> arrows = this.register(new Setting<>("Arrows", true));
	public final Setting<Boolean> eggs = this.register(new Setting<>("Eggs", true));
	public final Setting<Boolean> armor = this.register(new Setting<>("Armor", true));
	public final Setting<Boolean> hurtCam = this.register(new Setting<>("HurtCam", true));
	public final Setting<Boolean> fireOverlay = this.register(new Setting<>("FireOverlay", true));
	public final Setting<Boolean> waterOverlay = this.register(new Setting<>("WaterOverlay", true));
	public final Setting<Boolean> blockOverlay = this.register(new Setting<>("BlockOverlay", true));
	public final Setting<Boolean> nausea = this.register(new Setting<>("Nausea", true));
	public final Setting<Boolean> blindness = this.register(new Setting<>("Blindness", true));
	public final Setting<Boolean> fog = this.register(new Setting<>("Fog", true));
	public final Setting<Boolean> darkness = this.register(new Setting<>("Darkness", true));
	public final Setting<Boolean> fireEntity = this.register(new Setting<>("EntityFire", true));
	public final Setting<Boolean> antiTitle = this.register(new Setting<>("Title", false));
	//public final Setting<Boolean> antiPlayerCollision = this.register(new Setting<>("PlayerCollision", true));
	public final Setting<Boolean> elderGuardian = this.register(new Setting<>("Guardian", true));
	public final Setting<Boolean> explosions = this.register(new Setting<>("Explosions", true));
	public final Setting<Boolean> campFire = this.register(new Setting<>("CampFire", true));
	public final Setting<Boolean> fireworks = this.register(new Setting<>("Fireworks", true));
	public final Setting<Boolean> totem = this.register(new Setting<>("Totem", true));

    public NoRender() {
        super("NoRender", "Opens NoRender", Module.Category.RENDER, true, false, false);
        this.setInstance();
    }

	public static NoRender getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoRender();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

	@Subscribe
	public void onPacketReceive(PacketEvent.Receive e){
		if(e.getPacket() instanceof TitleS2CPacket && antiTitle.getValue()){
			e.cancel();;
		}
	}

	@Subscribe
	public void onUpdateWalking(UpdateWalkingEvent event) {
		for(Entity ent : mc.world.getEntities()){
			if(ent instanceof PotionEntity){
				if(potions.getValue())
					mc.world.removeEntity(ent.getId(), Entity.RemovalReason.KILLED);
			}
			if(ent instanceof ExperienceBottleEntity){
				if(xp.getValue())
					mc.world.removeEntity(ent.getId(), Entity.RemovalReason.KILLED);
			}
			if(ent instanceof ArrowEntity){
				if(arrows.getValue())
					mc.world.removeEntity(ent.getId(), Entity.RemovalReason.KILLED);
			}
			if(ent instanceof EggEntity){
				if(eggs.getValue())
					mc.world.removeEntity(ent.getId(), Entity.RemovalReason.KILLED);
			}
		}
	}

	@Subscribe
	public void onParticle(ParticleEvent.AddParticle event) {
		if (elderGuardian.getValue() && event.particle instanceof ElderGuardianAppearanceParticle) {
			event.cancel();
		} else if (explosions.getValue() && event.particle instanceof ExplosionLargeParticle) {
			event.cancel();
		} else if (campFire.getValue() && event.particle instanceof CampfireSmokeParticle) {
			event.cancel();
		} else if (fireworks.getValue() && (event.particle instanceof FireworksSparkParticle.FireworkParticle || event.particle instanceof FireworksSparkParticle.Flash)) {
			event.cancel();
		}
	}
}