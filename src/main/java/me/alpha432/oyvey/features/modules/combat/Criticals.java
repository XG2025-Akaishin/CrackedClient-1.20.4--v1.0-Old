package me.alpha432.oyvey.features.modules.combat;

import com.google.common.eventbus.Subscribe;

import io.netty.buffer.Unpooled;
import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.util.models.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Criticals extends Module {
    private final Timer timer = new Timer();

    private static Criticals INSTANCE = new Criticals();

    public Criticals() {
        super("Criticals", "", Category.COMBAT, true, false, false);
        this.setInstance();
    }

    public static Criticals getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Criticals();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Subscribe 
    private void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof PlayerInteractEntityC2SPacket packet && packet.type.getType() == PlayerInteractEntityC2SPacket.InteractType.ATTACK) {
            Entity entity = mc.world.getEntityById(packet.entityId);
            if (entity == null
                    || entity instanceof EndCrystalEntity
                    || !mc.player.isOnGround()
                    || !(entity instanceof LivingEntity)
                    || !timer.passedMs(0)) return;

            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + (double) 0.1f, mc.player.getZ(), false));
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), false));
            mc.player.addCritParticles(entity);
            doCritical();//test
            timer.reset();
        }
    }

    public void doCritical() {
        if (mc.player.isInLava() || mc.player.isTouchingWater()) return;

        double posX = mc.player.getX();
        double posY = mc.player.getY();
        double posZ = mc.player.getZ();
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(posX, posY + 0.0633, posZ, false));
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(posX, posY, posZ, false));
    }

    @Override 
    public String getDisplayInfo() {
        return "Packet";
    }

    /*New
    //@Subscribe
    public void sendPacket(PacketEvent.Send event) {
        if (event.packet instanceof PlayerInteractEntityC2SPacket packet) {
            if (!(getInteractType(packet) == InteractType.ATTACK && getEntity(packet) instanceof LivingEntity)) return;
            if (getEntity(packet) instanceof EndCrystalEntity) return;

            doCritical();
        }
    }

    public void doCritical() {
        if (mc.player.isInLava() || mc.player.isTouchingWater()) return;

        double posX = mc.player.getX();
        double posY = mc.player.getY();
        double posZ = mc.player.getZ();
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(posX, posY + 0.0633, posZ, false));
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(posX, posY, posZ, false));
    }

    private Entity getEntity(PlayerInteractEntityC2SPacket packet) {
        PacketByteBuf packetBuf = new PacketByteBuf(Unpooled.buffer());
        packet.write(packetBuf);

        return mc.world.getEntityById(packetBuf.readVarInt());
    }

    private InteractType getInteractType(PlayerInteractEntityC2SPacket packet) {
        PacketByteBuf packetBuf = new PacketByteBuf(Unpooled.buffer());
        packet.write(packetBuf);

        packetBuf.readVarInt();
        return packetBuf.readEnumConstant(InteractType.class);
    }

    public enum InteractType {
        INTERACT,
        ATTACK,
        INTERACT_AT
    }*/
}
