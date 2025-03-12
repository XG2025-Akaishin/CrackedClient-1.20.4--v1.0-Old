package me.alpha432.oyvey.mixin;

import me.alpha432.oyvey.CrackedClient;
import me.alpha432.oyvey.event.impl.KeyEvent;
import me.alpha432.oyvey.features.futuregui.FutureGuiOpen;
import me.alpha432.oyvey.features.gui.OyVeyGui;
import net.minecraft.client.Keyboard;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.alpha432.oyvey.util.traits.Util.EVENT_BUS;
import static me.alpha432.oyvey.util.traits.Util.mc;
import me.alpha432.oyvey.features.modules.Module;

@Mixin(Keyboard.class)
public class MixinKeyboard {

    /*
    @Inject(method = "onKey", at = @At("HEAD"))
    private void onKey(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if(Module.fullNullCheck()) return;
        boolean whitelist = mc.currentScreen == null || mc.currentScreen instanceof OyVeyGui || mc.currentScreen instanceof FutureGuiOpen;
        if (!whitelist) return;

        if (action == 0) OyVey.moduleManager.onKeyReleased(key);
        if (action == 1) OyVey.moduleManager.onKeyPressed(key);
        if (action == 2) action = 1;

        switch (action) {
            case 0 -> {
                EventKeyRelease event = new EventKeyRelease(key, scanCode);
               // mc.world.playSound(mc.player, mc.player.getBlockPos(), Thunderhack.KEYRELEASE_SOUNDEVENT, SoundCategory.BLOCKS, 1f, 1f);

                EVENT_BUS.post(event);
                if (event.isCancelled()) ci.cancel();
            }
            case 1 -> {
                EventKeyPress event = new EventKeyPress(key, scanCode);
              //  mc.world.playSound(mc.player, mc.player.getBlockPos(), Thunderhack.KEYPRESS_SOUNDEVENT, SoundCategory.BLOCKS, 1f, 1f);

                EVENT_BUS.post(event);
                if (event.isCancelled()) ci.cancel();
            }
        }
    }*/


    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKey(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        KeyEvent event = new KeyEvent(key);
        EVENT_BUS.post(event);
        if (action == GLFW.GLFW_PRESS) CrackedClient.moduleManager.onKeyPressed(key);
        if (event.isCancelled()) ci.cancel();
    }
}