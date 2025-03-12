package me.alpha432.oyvey.features.modules.movement.fastfall;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class FastFall extends Module {
    private final Setting<Float> speed = this.register(new Setting<>("Speed", 10f, 1f, 15f));
    private final Setting<Float> height = this.register(new Setting<>("Speed", 10f, 1f, 30f));

    public FastFall() {
        super("FastFall", "Control de velocidad de caida", Category.MOVEMENT, true, false, false);
    }

    public void onUpdate() {
        super.onUpdate();

        if(mc.player == null || mc.world == null || !mc.player.isOnGround()) {
            return;
        }

        int blocks = traceDown();

        if(blocks <= height.getValue()) {
            mc.player.addVelocity(0.0, -speed.getValue(), 0.0);
        }
    }

    private int traceDown() {
        int blocks = 0;
        int y = (int) Math.round(mc.player.getY()) - 1;

        while(y >= 0) {
            Vec3d start = mc.player.getPos();
            Vec3d end = start.withAxis(Direction.Axis.Y, y);
            HitResult result = mc.world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player));
            BlockPos pos = BlockPos.ofFloored(end);

            if(result != null && result.getType() == HitResult.Type.BLOCK) {
                return blocks;
            }

            blocks++;
            y--;
        }

        return blocks;
    }
}
