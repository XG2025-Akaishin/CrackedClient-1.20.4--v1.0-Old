package me.alpha432.oyvey.features.modules.movement.elytrafly;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.Vec3d;
import com.google.common.eventbus.Subscribe;
import me.alpha432.oyvey.event.impl.speed.PlayerJumpEvent;
import me.alpha432.oyvey.event.impl.freelook.TickEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.misc.fakeplayer.utils.ChatUtils;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.features.modules.movement.elytrafly.utils.Timer;
import me.alpha432.oyvey.features.modules.movement.elytrafly.utils.ItemUtils;
import me.alpha432.oyvey.features.modules.movement.elytrafly.utils.MathUtil;
import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.event.impl.elytrafly.PlayerTravelEvent;

public class ElytraFly extends Module {

    public final Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.Control));
    private final Setting<Float> speed = this.register(new Setting<>("Speed", 1.82f, 0.0f, 10.0f));
    private final Setting<Float> DownSpeed = this.register(new Setting<>("DownSpeed", 1.82f, 0.0f, 10.0f));
    private final Setting<Float> GlideSpeed = this.register(new Setting<>("GlideSpeed", 1f, 0f, 10f));
    private final Setting<Float> UpSpeed = this.register(new Setting<>("UpSpeed", 2.0f, 0f, 10f));
    private final Setting<Boolean> Accelerate = this.register(new Setting<>("Accelerate", true));
    private final Setting<Integer> vAccelerationTimer = this.register(new Setting<>("Timer", 1000, 0, 10000));
    private final Setting<Boolean> SpeedInLiquids = this.register(new Setting<>("SpeedInWater", false));
    private final Setting<Float> RotationPitch = this.register(new Setting<>("RotationPitch", 0.0f, (-90f), 90f));
    private final Setting<Boolean> CancelInWater = this.register(new Setting<>("CancelInWater", true));
    private final Setting<Integer> CancelAtHeight = this.register(new Setting<>("CancelAtHeight", 5, 0, 10));

    private final Setting<Boolean> InstantFly = this.register(new Setting<>("InstantFly", true));
    private final Setting<Boolean> EquipElytra = this.register(new Setting<>("EquipElytra", false));
    private final Setting<Boolean> PitchSpoof = this.register(new Setting<>("PitchSpoof", false));

    private final Timer PacketTimer = new Timer();
    private final Timer AccelerationTimer = new Timer();
    private final Timer AccelerationResetTimer = new Timer();
    private final Timer InstantFlyTimer = new Timer();
    private boolean SendMessage = false;

    public enum Mode {
        Normal, Tarzan, Superior, Packet, Control
    }

    public ElytraFly() {
        super("ElytraFly", "ElytraFly 1.20", Category.MOVEMENT, true, false, false);
    }

    private int ElytraSlot = -1;

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.player == null || mc.interactionManager == null) {
            toggle();///test
            return;
        }
        ElytraSlot = -1;

        if (EquipElytra.getValue()) {
            if (mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.ELYTRA) {
                for (int i = 0; i < 44; ++i) {
                    ItemStack Stack = mc.player.getInventory().getStack(i);
                    if (Stack.isEmpty() || Stack.getItem() != Items.ELYTRA) continue;
                    ElytraItem Elytra = (ElytraItem)Stack.getItem();
                    ElytraSlot = i;
                    break;
                }

                if (ElytraSlot != -1) {
                    boolean HasArmorAtChest = mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.AIR;
                    ItemUtils.Move(ElytraSlot, 6);
                    if (HasArmorAtChest) mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, ElytraSlot, 0, SlotActionType.PICKUP, mc.player);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.player == null || mc.interactionManager == null) return;

        if (ElytraSlot != -1) {
            boolean HasItem = !mc.player.getInventory().getStack(ElytraSlot).isEmpty() || mc.player.getInventory().getStack(ElytraSlot).getItem() != Items.AIR;
            ItemUtils.Move(6, ElytraSlot);
            if (HasItem) mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 6, 0, SlotActionType.PICKUP, mc.player);
        }
    }

    @Override
    public String getDisplayInfo() {
        return mode.getValue().name();
    }

    //@EventHandler
    @Subscribe
    private void OnTravel(PlayerTravelEvent event) {
        if (mc.player == null) return;

        /// Player must be wearing an elytra.
        if (mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.ELYTRA) return;

        if (!mc.player.isFallFlying()) {
            if (!mc.player.isOnGround() && InstantFly.getValue()) {
                if (!InstantFlyTimer.passed(1000)) return;
                InstantFlyTimer.reset();
                mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            }

            return;
        }

        switch (mode.getValue()) {
            case Normal, Tarzan, Packet -> HandleNormalModeElytra(event);
            case Superior -> HandleImmediateModeElytra(event);
            case Control -> HandleControlMode(event);
            default -> {
            }
        }
    }

    public void HandleNormalModeElytra(PlayerTravelEvent Travel) {
        if (mc.player == null) return;
        double YHeight = mc.player.getY();

        if (YHeight <= CancelAtHeight.getValue()) {
            if (!SendMessage) {
                ChatUtils.warningMessage("WARNING, you must scaffold up or use fireworks, as YHeight <= CancelAtHeight!");
                SendMessage = true;
            }
            return;
        }

        boolean IsMoveKeyDown = mc.player.input.movementForward > 0 || mc.player.input.movementSideways > 0;

        boolean cancelInWater = !mc.player.isTouchingWater() && !mc.player.isInLava() && CancelInWater.getValue();

        if (mc.player.input.jumping) {
            //p_Travel.cancel();
            Accelerate();
            return;
        }

        if (!IsMoveKeyDown) AccelerationTimer.resetTimeSkipTo(-vAccelerationTimer.getValue());
        else if ((mc.player.getPitch() <= RotationPitch.getValue() || mode.getValue() == Mode.Tarzan) && cancelInWater) {
            if (Accelerate.getValue() && AccelerationTimer.passed(vAccelerationTimer.getValue())) {
                Accelerate();
                return;
            }
            return;
        }

        //p_Travel.cancel();
        Accelerate();
    }

    public void HandleImmediateModeElytra(PlayerTravelEvent Travel) {
        if (mc.player == null) return;
        if (mc.player.input.jumping) {
            double MotionSquared = Math.sqrt(mc.player.getVelocity().x * mc.player.getVelocity().x + mc.player.getVelocity().z * mc.player.getVelocity().z);

            if (MotionSquared > 1.0) return;
            else {
                double[] dir = MathUtil.directionSpeedNoForward(speed.getValue());

                mc.player.setVelocity(dir[0], -(GlideSpeed.getValue() / 10000f), dir[1]);
            }

            //p_Travel.cancel();
            return;
        }

        mc.player.setVelocity(0, 0, 0);

        //p_Travel.cancel();

        double[] dir = MathUtil.directionSpeed(speed.getValue());

        if (mc.player.input.movementSideways != 0 || mc.player.input.movementForward != 0) mc.player.setVelocity(dir[0], -(GlideSpeed.getValue() / 10000f), dir[1]);

        if (mc.player.input.sneaking) mc.player.setVelocity(mc.player.getVelocity().x, -DownSpeed.getValue(), mc.player.getVelocity().z);
    }

    public void Accelerate() {
        if (mc.player == null) return;
        if (AccelerationResetTimer.passed(vAccelerationTimer.getValue())) {
            AccelerationResetTimer.reset();
            AccelerationTimer.reset();
            SendMessage = false;
        }

        float Speed = this.speed.getValue();

        final double[] dir = MathUtil.directionSpeed(Speed);

        mc.player.setVelocity(mc.player.getVelocity().x, -(GlideSpeed.getValue() / 10000f), mc.player.getVelocity().z);

        if (mc.player.input.movementSideways != 0 || mc.player.input.movementForward != 0) mc.player.setVelocity(dir[0], mc.player.getVelocity().y, dir[1]);
        else mc.player.setVelocity(0, mc.player.getVelocity().y, 0);

        if (mc.player.input.sneaking) mc.player.setVelocity(mc.player.getVelocity().x, -DownSpeed.getValue(), mc.player.getVelocity().z);
    }


    private void HandleControlMode(PlayerTravelEvent Event) {
        if (mc.player == null) return;
        final double[] dir = MathUtil.directionSpeed(speed.getValue());

        if (mc.player.input.movementSideways != 0 || mc.player.input.movementForward != 0) {
            mc.player.setVelocity(dir[0], mc.player.getVelocity().y, dir[1]);
            mc.player.addVelocity(-((mc.player.getVelocity().x*(Math.abs(mc.player.getPitch())+90)/90) - mc.player.getVelocity().x), mc.player.getVelocity().y, -((mc.player.getVelocity().z*(Math.abs(mc.player.getPitch())+90)/90) - mc.player.getVelocity().z));
        } else mc.player.setVelocity(0, mc.player.getVelocity().y, 0);

        mc.player.setVelocity(mc.player.getVelocity().x, (-MathUtil.degToRad(mc.player.getPitch())) * mc.player.input.movementForward, mc.player.getVelocity().z);

        //p_Event.cancel();
    }

    //@EventHandler
    @Subscribe
    private void PacketEvent(PacketEvent.Send event) {
        //if (!(event.Post))) return;

        if (mc.player == null) return;
        if (event.getPacket() instanceof PlayerMoveC2SPacket && PitchSpoof.getValue()) {
            if (!mc.player.isFallFlying()) return;
            if (event.getPacket() instanceof PlayerMoveC2SPacket.Full rotation && PitchSpoof.getValue()) {
                if (mc.getNetworkHandler() == null) return;
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(rotation.getX(0), rotation.getY(0), rotation.getZ(0), rotation.isOnGround()));
                event.cancel();
            } else if (event.getPacket() instanceof PlayerMoveC2SPacket.LookAndOnGround && PitchSpoof.getValue()) event.cancel();
        }
    }
}