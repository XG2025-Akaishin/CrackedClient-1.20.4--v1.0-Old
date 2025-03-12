package me.alpha432.oyvey.features.modules.render;

import static me.alpha432.oyvey.util.traits.Util.EVENT_BUS;
import static me.alpha432.oyvey.util.traits.Util.mc;

import com.google.common.eventbus.Subscribe;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.event.impl.Render3DEvent;
import me.alpha432.oyvey.features.modules.render.popchams.utils.AnimateUtil;
import me.alpha432.oyvey.features.settings.Setting;

public class ZoomPro extends Module {
    private static ZoomPro INSTANCE = new ZoomPro();
    public double currentFov;

    public Setting<Double> animSpeed = this.register(new Setting<>("AnimSpeed", 0.1D, 0.0D, 1.0D));
    public Setting<Double> fov = this.register(new Setting<>("Fov", 60D, (-130D), 130D));

    public ZoomPro() {
        super("ZoomPro", "ZoomPro PRO Epic", Category.RENDER, true, false, true);
        EVENT_BUS.register(new ZoomAnim());
        this.setInstance();
    }

    public static ZoomPro getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ZoomPro();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        if (mc.options.getFov().getValue() == 70) {
            mc.options.getFov().setValue(71);
        }
    }

    public static boolean on = false;
    public class ZoomAnim {
        @Subscribe
        public void onRender3D(Render3DEvent event) {
            if (isOn()) {
                currentFov = AnimateUtil.animate(currentFov, fov.getValue(), animSpeed.getValue());
                on = true;
            } else if (on) {
                currentFov = AnimateUtil.animate(currentFov, 0, animSpeed.getValue());
                if ((int) currentFov == 0) {
                    on = false;
                }
            }
        }
    }
}
