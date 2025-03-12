package me.alpha432.oyvey.features.modules.render.worldtweaks;

import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import me.alpha432.oyvey.event.impl.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;

import java.awt.*;

import com.google.common.eventbus.Subscribe;

public class WorldTweaks extends Module {
    public WorldTweaks() {
        super("WorldTweaks", "WorldTweaks", Module.Category.RENDER, true, false, false);
        this.setInstance();
    }
    public static WorldTweaks INSTANCE = new WorldTweaks();
    public Setting<Boolean> fogModify = this.register(new Setting<>("FogModify", true));
    public Setting<Integer> fogStart = this.register(new Setting<>("FogStart", 0, 0, 255));
    public Setting<Integer> fogEnd = this.register(new Setting<>("FogEnd", 64, 1, 255));
    public Setting<Integer> red = this.register(new Setting<>("Red", 1, 0, 255));
    public Setting<Integer> blue = this.register(new Setting<>("Blue", 1, 0, 255));
    public Setting<Integer> alpha = this.register(new Setting<>("Alpha", 1, 0, 255));
    public Setting<Integer> green = this.register(new Setting<>("Green", 1, 0, 255));
    public final Setting<Boolean> ctime = this.register(new Setting<>("ChangeTime", false));
    public final Setting<Integer> ctimeVal = this.register(new Setting<>("Time", 21, 0, 23));

    long oldTime;

    public static WorldTweaks getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WorldTweaks();
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
