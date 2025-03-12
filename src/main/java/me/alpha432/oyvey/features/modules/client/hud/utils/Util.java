package me.alpha432.oyvey.features.modules.client.hud.utils;

import java.util.Objects;

import com.google.common.eventbus.EventBus;
import net.minecraft.client.MinecraftClient;

public interface Util {
    MinecraftClient mc = MinecraftClient.getInstance();
    
    public static boolean isInHell() {
        if (mc.world == null) return false;
        return Objects.equals(mc.world.getRegistryKey().getValue().getPath(), "the_nether");
    }

    public static boolean isInEnd() {
        if (mc.world == null) return false;
        return Objects.equals(mc.world.getRegistryKey().getValue().getPath(), "the_end");
    }

    public static boolean isInOver() {
        if (mc.world == null) return false;
        return Objects.equals(mc.world.getRegistryKey().getValue().getPath(), "overworld");
    }
}
