package me.alpha432.oyvey.features.modules.misc.armornoftify;

import com.google.common.eventbus.Subscribe;
import me.alpha432.oyvey.event.impl.freelook.TickEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import me.alpha432.oyvey.features.commands.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;

public class ArmorNotify extends Module {

    private final Setting<Modes> logMode = this.register(new Setting<Modes>("Modes", Modes.chat));
    private final Setting<Integer> limit = this.register(new Setting<Integer>("Limit", 2,1,200));

    public ArmorNotify() {
        super("ArmorNotify", "Armor Notify", Module.Category.CHAT, true, false, false);
    }

    public boolean logged = false;
    public int dur;
    
    @Subscribe
    private void onTick(TickEvent.Post event) {
        assert mc.player != null;
        checkArmorDurability(mc.player);
    }

    private void checkArmorDurability(PlayerEntity player) {
        for (ItemStack armorItem : player.getArmorItems()) {
            if (armorItem.isDamageable()) {
                int currentDurability = armorItem.getMaxDamage() - armorItem.getDamage();

                if (dur == currentDurability && logged) {
                    continue;
                }

                if (currentDurability <= limit.getValue()) {
                    if (logMode.getValue() == Modes.chat) sendMessage(Formatting.RED + "[!] " + Formatting.WHITE + "Your " + armorItem.getName().getString() + " is about to run out of durability " + Formatting.GRAY + "(" + currentDurability + ")");
                    if (logMode.getValue() == Modes.notification) Command.sendMessage(armorItem.getName().getString() + " low dur " + currentDurability);
                    logged = true;
                } else {
                    logged = false;
                }

                dur = currentDurability;
            }
        }
    }

    public enum Modes {
        chat,
        notification
    }
}
