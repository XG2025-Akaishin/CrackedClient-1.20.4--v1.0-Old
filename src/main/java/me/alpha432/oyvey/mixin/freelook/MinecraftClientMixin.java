package me.alpha432.oyvey.mixin.freelook;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.alpha432.oyvey.event.impl.freelook.TickEvent;
import me.alpha432.oyvey.util.accessor.IMinecraftClient;

import static me.alpha432.oyvey.util.traits.Util.EVENT_BUS;

//@Mixin(value = MinecraftClient.class, priority = 1001)
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements IMinecraftClient {

    @Shadow
    public ClientWorld world;
    @Shadow
    public ClientPlayerEntity player;

        @Shadow
    @Nullable
    public ClientPlayerInteractionManager interactionManager;

    @Shadow
    protected abstract void doItemUse();

    @Shadow
    protected abstract boolean doAttack();

    @Shadow
    protected int attackCooldown;
    @Unique
    private boolean leftClick;
    @Unique
    private boolean rightClick;
    @Unique
    private boolean doAttackCalled;
    @Unique
    private boolean doItemUseCalled;

    @Override
    public void leftClick() {
        leftClick = true;
    }

    @Override
    public void rightClick() {
        rightClick = true;
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void hookTickPre/*onPreTick*/(CallbackInfo info) {
        //OnlinePlayers.update();
        doAttackCalled = false;
        doItemUseCalled = false;
        if (player != null && world != null) {
            EVENT_BUS.post(TickEvent.Pre.get());
        }
        if (interactionManager == null) {
            return;
        }
        if (leftClick && !doAttackCalled) {
            doAttack();
        }
        if (rightClick && !doItemUseCalled) {
            doItemUse();
        }
        leftClick = false;
        rightClick = false;
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void hookTickPost/*onTick*/(CallbackInfo info) {
        if (player != null && world != null) {
            EVENT_BUS.post(TickEvent.Post.get());
        }
    }

    @Inject(method = "doItemUse", at = @At(value = "HEAD"))
    private void hookDoItemUse(CallbackInfo ci)
    {
        doItemUseCalled = true;
    }
    @Inject(method = "doAttack", at = @At(value = "HEAD"))
    private void hookDoAttack(CallbackInfoReturnable<Boolean> cir)
    {
        doAttackCalled = true;
    }
}
