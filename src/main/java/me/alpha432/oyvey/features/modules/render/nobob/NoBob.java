package me.alpha432.oyvey.features.modules.render.nobob;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class NoBob extends Module {
        private static NoBob INSTANCE = new NoBob();
    public NoBob() {
        super("NoBob", "NoBob", Module.Category.RENDER, true, false, false);
        this.setInstance();
    }

    public Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Sexy));
    public Setting<Boolean> customBob = this.register(new Setting<>("CustomBob", true));

    public static void bobView(MatrixStack matrices, float tickDelta) {
        if (!(mc.getCameraEntity() instanceof PlayerEntity))
            return;

        float g = -(mc.player.horizontalSpeed + (mc.player.horizontalSpeed - mc.player.prevHorizontalSpeed) * tickDelta);
        float h = MathHelper.lerp(tickDelta, mc.player.prevStrideDistance, mc.player.strideDistance);
        matrices.translate( 0, -Math.abs(g * h * (NoBob.getInstance().mode.equals(Mode.Sexy) ? 0.00035 : 0.)) , 0);//getInstance text bobView
    }

    public static enum Mode {
        Sexy,
        Off
    }


    public static NoBob getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoBob();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}
