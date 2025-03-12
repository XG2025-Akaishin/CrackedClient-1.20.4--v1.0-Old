package me.alpha432.oyvey.features.modules.misc.antiattack;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import org.jetbrains.annotations.NotNull;

import com.google.common.eventbus.Subscribe;

import io.netty.buffer.Unpooled;

public class AntiAttack extends Module {
    private final Setting<Boolean> friend = this.register(new Setting<>("Friend", true));
    private final Setting<Boolean> zoglin = this.register(new Setting<>("Zoglin", true));
    private final Setting<Boolean> villager = this.register(new Setting<>("Villager", false));

    public AntiAttack() {
        super("AntiAttack", "AntiAttack", Category.MISC, true, false, false);
    }

    @Subscribe//@EventHandler
    @SuppressWarnings("unused")
    private void onPacketSend(PacketEvent.@NotNull Send e) {
        if (e.getPacket() instanceof PlayerInteractEntityC2SPacket pac) {
            Entity entity = getEntity(pac);
            if (entity == null) return;
            if (CrackedClient.friendManager.isFriend(entity.getName().getString()) && friend.getValue())
                e.cancel();
            if (entity instanceof ZombifiedPiglinEntity && zoglin.getValue())
                e.cancel();
            if(entity instanceof VillagerEntity && villager.getValue()){
                e.cancel();
            }
        }
    }

    private Entity getEntity(PlayerInteractEntityC2SPacket packet) {
        PacketByteBuf packetBuf = new PacketByteBuf(Unpooled.buffer());
        packet.write(packetBuf);

        return mc.world.getEntityById(packetBuf.readVarInt());
    }
}