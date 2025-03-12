package me.alpha432.oyvey.features.modules.misc.mountbypass;

import me.alpha432.oyvey.features.modules.misc.mountbypass.interfaceUtil.IPlayerInteractEntityC2SPacket;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

import com.google.common.eventbus.Subscribe;

import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;

public class MountBypass extends Module {
    private boolean dontCancel;

    public MountBypass() {
        super("MountBypass", "MountBypass", Module.Category.MISC, true, false ,false);
    }

    //@EventHandler
    @Subscribe
    public void onSendPacket(PacketEvent.Send event) {
        if (dontCancel) {
            dontCancel = false;
            return;
        }

        if (event.packet instanceof IPlayerInteractEntityC2SPacket packet) {
            if (packet.getType() == PlayerInteractEntityC2SPacket.InteractType.INTERACT_AT && packet.getEntity() instanceof AbstractDonkeyEntity) event.cancel();
        }
    }
}
