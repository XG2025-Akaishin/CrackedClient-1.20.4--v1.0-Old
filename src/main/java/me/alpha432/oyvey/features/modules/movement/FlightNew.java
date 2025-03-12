package me.alpha432.oyvey.features.modules.movement;



import me.alpha432.oyvey.event.impl.freelook.TickEvent;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;

public class FlightNew extends Module {

	public static FlightNew INSTANCE = new FlightNew();

    public FlightNew() {
        super("FlightNew", "FlightNew", Category.MOVEMENT, true, false, false);
		INSTANCE = this;
		setInstance();
    }
    
    private final Setting<Float> flySpeed = this.register(new Setting<>("Speed", 2f, 0.1f, 15f));

	public static FlightNew getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FlightNew();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

	//@Override
	public void onUpdate(TickEvent event) {
		ClientPlayerEntity player = mc.player;
		float speed = this.flySpeed.getValue();
		if(mc.player.isRiding()) {
			Entity riding = mc.player.getRootVehicle();
			Vec3d velocity = riding.getVelocity();
			double motionY = mc.options.jumpKey.isPressed() ? 0.3 : 0;
			riding.setVelocity(velocity.x, velocity.y, velocity.z);
		}else {
			if (mc.options.sprintKey.isPressed()) {
				speed *= 1.5;
			}
			player.getAbilities().flying = false;
			player.setVelocity(new Vec3d(0, 0, 0));
			
			Vec3d vec = new Vec3d(0, 0, 0);
			
				
			if (mc.options.jumpKey.isPressed()) {
				vec = new Vec3d(0, speed, 0);
			}
			if (mc.options.sneakKey.isPressed()) {
				vec = new Vec3d(0, -speed, 0);
			}
			player.setVelocity(vec);
		}
	}

	public double getSpeed() {
		return this.flySpeed.getValue();
	}
    
}
