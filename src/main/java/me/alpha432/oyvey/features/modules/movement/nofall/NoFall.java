package me.alpha432.oyvey.features.modules.movement.nofall;

import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;

import com.google.common.eventbus.Subscribe;

import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.mixin.nofall.IPlayerMoveC2SPacket;

public class NoFall extends Module {
    private final Setting<Float> distance = this.register(new Setting<>("Speed", 3.0f, 0.0f, 8.0f));

    public NoFall() {
        super("NoFall", "no muerte por caida", Category.MOVEMENT, true, false, false);
    }

	@Override
	public void onUpdate() {
		if(mc.player.fallDistance >= distance.getValue() - 0.1) {
			mc.player.networkHandler.sendPacket(new OnGroundOnly(true));
		}
    }

	@Subscribe
	public void onPacketSend(PacketEvent.Send event) {
		if (nullCheck()) {
			return;
		}
		for (ItemStack is : mc.player.getArmorItems()) {
			if (is.getItem() == Items.ELYTRA) {
				return;
			}
		}
		if (event.getPacket() instanceof PlayerMoveC2SPacket packet) {
			if (mc.player.fallDistance >= (float) this.distance.getValue()) {
				((IPlayerMoveC2SPacket) packet).setOnGround(true);
			}
		}
	}
}