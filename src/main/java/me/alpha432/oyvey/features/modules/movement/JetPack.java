package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.event.impl.freelook.TickEvent;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;

public class JetPack extends Module {

    public JetPack() {
        super("JetPack", "JetPack", Category.MOVEMENT, true, false, false);
    }
    
    private final Setting<Float> jetpackSpeed = this.register(new Setting<>("Speed", 0.5f, 0.1f, 5.0f));

	//@Override
	public void onUpdate(TickEvent event) {
		ClientPlayerEntity player = mc.player;
		float speed = this.jetpackSpeed.getValue();
		
		if(mc.player.fallDistance > 2f) {
			mc.player.networkHandler.sendPacket(new OnGroundOnly(true));
		}
		
		if(mc.player.isRiding()) {
			Entity riding = mc.player.getRootVehicle();
			Vec3d velocity = riding.getVelocity();
			double motionY = mc.options.jumpKey.isPressed() ? 0.3 : 0;
			riding.setVelocity(velocity.x, motionY, velocity.z);
		}else {
			player.getAbilities().flying = false;
			
			Vec3d playerSpeed = player.getVelocity();
			if (mc.options.jumpKey.isPressed()) {
				double angle = -player.bodyYaw;
				float leftThrusterX = (float) Math.sin(Math.toRadians(angle + 90)) * 0.25f;
				float leftThrusterZ = (float) Math.cos(Math.toRadians(angle + 90)) * 0.25f;
				float rightThrusterX = (float) Math.sin(Math.toRadians(angle + 270)) * 0.25f;
				float rightThrusterZ = (float) Math.cos(Math.toRadians(angle + 270)) * 0.25f;
				
				mc.world.addParticle(ParticleTypes.FLAME, player.getX() + leftThrusterX, player.getY() + 0.5f, player.getZ() + leftThrusterZ, leftThrusterX, -0.5f, leftThrusterZ);
				mc.world.addParticle(ParticleTypes.FLAME, player.getX() + rightThrusterX, player.getY() + 0.5f, player.getZ() + rightThrusterZ, rightThrusterX, -0.5f, rightThrusterZ);
				playerSpeed = playerSpeed.add(0, speed / 20.0f, 0);
			}
			player.setVelocity(playerSpeed);
		}
	}
    
}
