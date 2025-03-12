package me.alpha432.oyvey.features.modules.chat.antispam;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.common.eventbus.Subscribe;

import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;

public class AntiSpam extends Module {

    private final ArrayList<String> forbidden = new ArrayList<>(Arrays.asList("https:", "http:", ".com", ".ru", ".cc", ".gg", ".top", ".wtf", ".xyz", ".org", ".net"));
    private final Setting<Boolean> links = this.register(new Setting<Boolean>("Links", false));
    private final Setting<Boolean> nWords = this.register(new Setting<Boolean>("NWords", false));
    private final Setting<Boolean> customNoSpam = this.register(new Setting<Boolean>("CustomText", false));
    private final Setting<String> textCustom = this.register(new Setting<String>("Custom", "fuck"));

    public AntiSpam() {
        super("AntiSpam", "AntiSpam Domain", Module.Category.CHAT, true, false, false);
    }


    @Subscribe
    private void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof GameMessageS2CPacket packet) {
            Text text = packet.content();
            String getMessage = text.getString();
            if (links.getValue()) {
                for (String str : forbidden) {
                    if (getMessage.toString().contains(str)) {
                        event.setCancelled(true);
                        break;
                    }
                }
            }
            if (nWords.getValue() && !event.isCancelled()) {
                if (getMessage.toString().contains("nigg")) event.setCancelled(true);
            }
            if (customNoSpam.getValue() && !event.isCancelled()) {
                if (getMessage.toString().contains(textCustom.toString())) event.setCancelled(true);
            }
        }
    }
}
