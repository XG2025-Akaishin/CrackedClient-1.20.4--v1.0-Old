package me.alpha432.oyvey.features.modules.movement.jesus;

import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import com.google.common.eventbus.Subscribe;
import me.alpha432.oyvey.event.impl.freelook.TickEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;

public class Jesus extends Module {
public final Setting<Mode> mode = this.register(new Setting<>("ModeSpeed", Mode.NCP));

public enum Modes {
    Strafe,
    OnGround
}

public Jesus() {
    super("Jesus", "Jesus yu",Category.MOVEMENT, true,false,false);
}

    private boolean isInLiquid(Vec3d pos) {
        BlockPos bp = BlockPos.ofFloored(pos);
        FluidState state = mc.world.getFluidState(bp);

        return !state.isEmpty() && pos.y - bp.getY() <= state.getHeight();
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mode.getValue() == Mode.NCP) {
            Entity entity = mc.player.getRootVehicle();

            if (entity.isSneaking() || entity.fallDistance > 3f)
                return;

            if (isInLiquid(entity.getPos().add(0, 0.3, 0))) {
                entity.setVelocity(entity.getVelocity().x, 0.08, entity.getVelocity().z);
            } else if (isInLiquid(entity.getPos().add(0, 0.1, 0))) {
                entity.setVelocity(entity.getVelocity().x, 0.05, entity.getVelocity().z);
            } else if (isInLiquid(entity.getPos().add(0, 0.05, 0))) {
                entity.setVelocity(entity.getVelocity().x, 0.01, entity.getVelocity().z);
            } else if (isInLiquid(entity.getPos())) {
                entity.setVelocity(entity.getVelocity().x, -0.01, entity.getVelocity().z);
                entity.setOnGround(true);
            }
        }
    }

    private enum Mode {
        NCP
    }
}
