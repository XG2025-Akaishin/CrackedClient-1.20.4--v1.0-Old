package me.alpha432.oyvey.features.modules.player.velocity;


import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;

import com.google.common.eventbus.Subscribe;

import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.event.impl.autototem.EventSync;
import me.alpha432.oyvey.features.modules.player.XCarry;
import me.alpha432.oyvey.features.modules.player.velocity.utils.MovementUtility;
import me.alpha432.oyvey.mixin.velocity.IExplosionS2CPacket;
import me.alpha432.oyvey.mixin.velocity.ISPacketEntityVelocity;

public class Velocity extends Module {
    private static Velocity INSTANCE = new Velocity();
    public Setting<Boolean> onlyAura = this.register(new Setting<>("OnlyAura", false));
    public Setting<Boolean> pauseInWater = this.register(new Setting<>("PauseInFluids", false));
    public Setting<Boolean> autoDisable = this.register(new Setting<>("DisableOnVerify", false));
    public Setting<Boolean> cc = this.register(new Setting<>("CC", false));
    public Setting<Boolean> fishingHook = this.register(new Setting<>("FishingHook", true));
    public Setting<Boolean> fire = this.register(new Setting<>("PauseFire", false));
    public Setting<Boolean> antiPush = this.register(new Setting<>("AntiPush", false));
    public Setting<Boolean> blocks = this.register(new Setting<>("Blocks", true)).withParent(antiPush);
    public Setting<Boolean> players = this.register(new Setting<>("Players", true)).withParent(antiPush);
    public Setting<Boolean> water = this.register(new Setting<>("Water", true)).withParent(antiPush);
    private final Setting<modeEn> mode = this.register(new Setting<>("Mode", modeEn.Matrix));
    public Setting<Float> vertical = this.register(new Setting<>("Vertical", 0.0f, 0.0f, 100.0f, v -> mode.getValue() == modeEn.Custom));
    private final Setting<jumpModeEn> jumpMode = this.register(new Setting<>("JumpMode", jumpModeEn.Jump, v -> mode.getValue() == modeEn.Jump));
    public Setting<Float> horizontal = this.register(new Setting<>("Horizontal", 0.0f, 0.0f, 100.0f, v -> mode.getValue() == modeEn.Custom || mode.getValue() == modeEn.Jump));
    public Setting<Float> motion = this.register(new Setting<>("Motion", .42f, 0.4f, 0.5f, v -> mode.getValue() == modeEn.Jump));
    public Setting<Boolean> fail = this.register(new Setting<>("SmartFail", true, v -> mode.getValue() == modeEn.Jump));
    public Setting<Float> failRate = this.register(new Setting<>("FailRate", 0.3f, 0.0f, 1.0f, v -> mode.getValue() == modeEn.Jump && fail.getValue()));
    public Setting<Float> jumpRate = this.register(new Setting<>("FailJumpRate", 0.25f, 0.0f, 1.0f, v -> mode.getValue() == modeEn.Jump && fail.getValue()));

    public Velocity() {
        super("Velocity", "Velocity", Category.PLAYER, true, false, false);
        this.setInstance();
    }

    public static Velocity getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Velocity();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public String getDisplayInfo() {
        return mode.getValue().name();
    }

    private boolean doJump, failJump, skip, flag;
    private int grimTicks, ccCooldown;

    //@EventHandler
        @Subscribe
    public void onPacketReceive(PacketEvent.Receive e) {
        if (fullNullCheck()) return;

        if(mc.player != null && (mc.player.isTouchingWater() || mc.player.isSubmergedInWater() || mc.player.isInLava()) && pauseInWater.getValue())
            return;

        if(mc.player != null && mc.player.isOnFire() && fire.getValue() && (mc.player.hurtTime > 0)){
            return;
        }

        if (ccCooldown > 0) {
            ccCooldown--;
            return;
        }

        if (e.getPacket() instanceof GameMessageS2CPacket && autoDisable.getValue()) {
            String text = ((GameMessageS2CPacket) e.getPacket()).content().getString();
            if (text.contains("What Phobos ")) disable(":^)");
        }

        if (e.getPacket() instanceof EntityStatusS2CPacket pac
                && pac.getStatus() == 31
                && pac.getEntity(mc.world) instanceof FishingBobberEntity
                && fishingHook.getValue()) {
            FishingBobberEntity fishHook = (FishingBobberEntity) pac.getEntity(mc.world);
            if (fishHook.getHookedEntity() == mc.player) {
                e.cancel();
            }
        }

        // MAIN VELOCITY
        if (e.getPacket() instanceof EntityVelocityUpdateS2CPacket pac) {
            if (pac.getId() == mc.player.getId() && (!onlyAura.getValue()/* || ModuleManager.aura.isEnabled()*/)) {
                switch (mode.getValue()) {
                    case Matrix -> {
                        if (!flag) {
                            e.cancel();
                            flag = true;
                        } else {
                            flag = false;
                            ((ISPacketEntityVelocity) pac).setMotionX(((int) ((double) pac.getVelocityX() * -0.1)));
                            ((ISPacketEntityVelocity) pac).setMotionZ(((int) ((double) pac.getVelocityZ() * -0.1)));
                        }
                    }
                    case Redirect -> {
                        int vX = pac.getVelocityX();
                        int vZ = pac.getVelocityZ();
                        if (vX < 0) vX *= -1;
                        if (vZ < 0) vZ *= -1;
                        double[] motion = MovementUtility.forward((vX + vZ));
                        ((ISPacketEntityVelocity) pac).setMotionX((int) (motion[0]));
                        ((ISPacketEntityVelocity) pac).setMotionY(0);
                        ((ISPacketEntityVelocity) pac).setMotionZ((int) (motion[1]));
                    }
                    case Custom -> {
                        ((ISPacketEntityVelocity) pac).setMotionX((int) ((float) pac.getVelocityX() * horizontal.getValue() / 100f));
                        ((ISPacketEntityVelocity) pac).setMotionY((int) ((float) pac.getVelocityY() * vertical.getValue() / 100f));
                        ((ISPacketEntityVelocity) pac).setMotionZ((int) ((float) pac.getVelocityZ() * horizontal.getValue() / 100f));
                    }
                    case Sunrise -> {
                        e.cancel();
                        sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), -999.0, mc.player.getZ(), true));
                    }
                    case Cancel -> {
                        e.cancel();
                    }
                    case Jump -> {
                        ((ISPacketEntityVelocity) pac).setMotionX((int) ((float) pac.getVelocityX() * horizontal.getValue() / 100f));
                        ((ISPacketEntityVelocity) pac).setMotionZ((int) ((float) pac.getVelocityZ() * horizontal.getValue() / 100f));
                    }
                    case OldGrim -> {
                        e.cancel();
                        grimTicks = 6;
                    }
                    case GrimNew -> {
                        e.cancel();
                        flag = true;
                    }
                }
            }
        }

        // EXPLOSION
        if (e.getPacket() instanceof ExplosionS2CPacket explosion) {
            switch (mode.getValue()) {
                case Cancel -> {
                    ((IExplosionS2CPacket) explosion).setMotionX(0);
                    ((IExplosionS2CPacket) explosion).setMotionY(0);
                    ((IExplosionS2CPacket) explosion).setMotionZ(0);
                }
                case Custom -> {
                    ((IExplosionS2CPacket) explosion).setMotionX(((IExplosionS2CPacket) explosion).getMotionX() * horizontal.getValue() / 100f);
                    ((IExplosionS2CPacket) explosion).setMotionZ(((IExplosionS2CPacket) explosion).getMotionZ() * horizontal.getValue() / 100f);
                    ((IExplosionS2CPacket) explosion).setMotionY(((IExplosionS2CPacket) explosion).getMotionY() * vertical.getValue() / 100f);
                }
                case GrimNew -> {
                    e.cancel();
                    flag = true;
                }
            }
        }

        // PING
        if (mode.getValue() == modeEn.OldGrim) {
            if (e.getPacket() instanceof CommonPingS2CPacket && grimTicks > 0) {
                e.cancel();
                grimTicks--;
            }
        }

        // LAGBACK
        if (e.getPacket() instanceof PlayerPositionLookS2CPacket) {
            if(cc.getValue() || mode.getValue() == modeEn.GrimNew)
                ccCooldown = 5;
        }
    }


    @Override
    public void onUpdate() {
        if(mc.player != null && (mc.player.isTouchingWater() || mc.player.isSubmergedInWater()) && pauseInWater.getValue())
            return;

        switch (mode.getValue()) {
            case Matrix -> {
                if (mc.player.hurtTime > 0 && !mc.player.isOnGround()) {
                    double var3 = mc.player.getYaw() * 0.017453292F;
                    double var5 = Math.sqrt(mc.player.getVelocity().x * mc.player.getVelocity().x + mc.player.getVelocity().z * mc.player.getVelocity().z);
                    mc.player.setVelocity(-Math.sin(var3) * var5, mc.player.getVelocity().y, Math.cos(var3) * var5);
                    mc.player.setSprinting(mc.player.age % 2 != 0);
                }
            }
            case Jump -> {
                if ((failJump || mc.player.hurtTime > 6) && mc.player.isOnGround()) {
                    if (failJump) failJump = false;
                    if (!doJump) skip = true;
                    if (Math.random() <= failRate.getValue() && fail.getValue()) {
                        if (Math.random() <= jumpRate.getValue()) {
                            doJump = true;
                            failJump = true;
                        } else {
                            doJump = false;
                            failJump = false;
                        }
                    } else {
                        doJump = true;
                        failJump = false;
                    }
                    if (skip) {
                        skip = false;
                        return;
                    }
                    switch (jumpMode.getValue()) {
                        case Jump -> mc.player.jump();
                        case Motion ->
                                mc.player.setVelocity(mc.player.getVelocity().getX(), motion.getValue(), mc.player.getVelocity().getZ());
                        case Both -> {
                            mc.player.jump();
                            mc.player.setVelocity(mc.player.getVelocity().getX(), motion.getValue(), mc.player.getVelocity().getZ());
                        }
                    }
                }
            }
            case GrimNew -> {
                if (flag) {
                    if(ccCooldown <= 0) {
                        sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
                        sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, BlockPos.ofFloored(mc.player.getPos()), Direction.DOWN));
                    }
                    flag = false;
                }
            }
        }
        if (grimTicks > 0)
            grimTicks--;
    }

    @Override
    public void onEnable() {
        grimTicks = 0;
    }

    public enum modeEn {
        Matrix, Cancel, Sunrise, Custom, Redirect, OldGrim, Jump, GrimNew
    }

    public enum jumpModeEn {
        Motion, Jump, Both
    }
}
