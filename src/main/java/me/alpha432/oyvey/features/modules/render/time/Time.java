package me.alpha432.oyvey.features.modules.render.time;

import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;

import java.awt.*;

import com.google.common.eventbus.Subscribe;

public class Time extends Module {
    public Time() {
        super("Time", "Time", Module.Category.RENDER, true, false, false);
        this.setInstance();
    }
    public static Time INSTANCE = new Time();
    public final Setting<Boolean> ctime = this.register(new Setting<>("ChangeTime", false));
    public final Setting<Integer> ctimeVal = this.register(new Setting<>("Time", 21, 0, 23));

    long oldTime;

    public static Time getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Time();
        }
        return INSTANCE;
    }

    public void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        oldTime = mc.world.getTime();
    }

    @Override
    public void onDisable() {
        mc.world.setTimeOfDay(oldTime);
    }

    //@EventHandler
    @Subscribe
    private void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof WorldTimeUpdateS2CPacket && ctime.getValue()) {
            oldTime = ((WorldTimeUpdateS2CPacket) event.getPacket()).getTime();
            event.cancel();
        }
    }

    @Override
    public void onUpdate() {
        if (ctime.getValue()) mc.world.setTimeOfDay(ctimeVal.getValue() * 1000);
    }
}
