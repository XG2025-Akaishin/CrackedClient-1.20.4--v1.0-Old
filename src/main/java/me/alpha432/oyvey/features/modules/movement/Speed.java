package me.alpha432.oyvey.features.modules.movement;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.Vec3d;
import com.google.common.eventbus.Subscribe;
import me.alpha432.oyvey.event.impl.speed.PlayerJumpEvent;
import me.alpha432.oyvey.event.impl.freelook.TickEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;

public class Speed extends Module {

    public final Setting<Modes> Mode = this.register(new Setting<>("ModeSpeed", Modes.OnGround));
    public final Setting<Boolean> sprint = this.register(new Setting<>("KeepSprint", true));
    public final Setting<Float> motion = this.register(new Setting("Motion", 1f, 0f, 1f, v -> sprint.getValue()));
    private final Setting<Float> Speed = this.register(new Setting<>("Speed", 1.0f, 0.0f, 2.0f));
    private final Setting<Float> StrafeSpeed = this.register(new Setting<>("StrafeSpeed", 1.0f, 0.0f, 2.0f));
    private final Setting<Boolean> AutoSprint = this.register(new Setting<>("AutoSprint", false));
    private final Setting<Boolean> SpeedInLiquids = this.register(new Setting<>("SpeedInWater", false));

    public enum Modes {
        Strafe,
        OnGround
    }

    public Speed() {
        super("Speed", "Speed strafe",Category.MOVEMENT, true,false,false);
    }

    @Subscribe
    public void OnPlayerTick(TickEvent event) {
        if (mc.player != null) {
            double velocity = Math.abs(mc.player.getVelocity().getX()) + Math.abs(mc.player.getVelocity().getZ());
            if ((mc.player.isTouchingWater() || mc.player.isInLava()) && !SpeedInLiquids.getValue()) return;
            //if (UseTimer.getValue()) timer.SetOverrideSpeed(1.088f);

            if (Mode.getValue() == Modes.OnGround) {
                if ((mc.player.forwardSpeed != 0 || mc.player.sidewaysSpeed != 0) && mc.player.isOnGround()) {
                    if (!mc.player.isSprinting()) {
                        mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
                    }

                    mc.player.setVelocity(new Vec3d(0, mc.player.getVelocity().y, 0));
                    mc.player.updateVelocity(Speed.getValue().floatValue(), new Vec3d(mc.player.sidewaysSpeed, 0, mc.player.forwardSpeed));
                }
            } else if (Mode.getValue() == Modes.Strafe) {
                if ((mc.player.forwardSpeed != 0 || mc.player.sidewaysSpeed != 0)) {
                    if (AutoSprint.getValue()) {
                        if (!mc.player.isSprinting()) {
                            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
                        }
                    }

                    mc.player.setVelocity(new Vec3d(0, mc.player.getVelocity().y, 0));
                    mc.player.updateVelocity(StrafeSpeed.getValue().floatValue(), new Vec3d(mc.player.sidewaysSpeed, 0, mc.player.forwardSpeed));

                    if (velocity >= 0.12 && mc.player.isOnGround()) {
                        mc.player.updateVelocity(velocity >= 0.3 ? 0.0f : 0.15f, new Vec3d(mc.player.sidewaysSpeed, 0, mc.player.forwardSpeed));
                        mc.player.jump();
                    }
                }
            }
        }
    }

    @Subscribe
    private void OnPlayerJump(PlayerJumpEvent event) {
        if (Mode.getValue() == Modes.Strafe) event.cancel();
    }
}