package me.alpha432.oyvey.mixin.fakevanilla;

import net.minecraft.client.ClientBrandRetriever;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import me.alpha432.oyvey.features.modules.misc.fakevanilla.FakeVanilla;

@Mixin({ClientBrandRetriever.class})
public class MixinClientBrandRetriever {
    @Inject(method = "getClientModName", at = {@At("HEAD")}, cancellable = true, remap = false)
    private static void getClientModNameHook(CallbackInfoReturnable<String> cir) {
        if(FakeVanilla.getInstance().isEnabled())
            cir.setReturnValue(FakeVanilla.getInstance().getClientName());
    }
}