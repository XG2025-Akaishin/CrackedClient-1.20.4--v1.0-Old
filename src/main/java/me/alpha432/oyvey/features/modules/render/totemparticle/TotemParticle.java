package me.alpha432.oyvey.features.modules.render.totemparticle;

import java.awt.Color;
import java.util.Random;

import com.google.common.eventbus.Subscribe;

import me.alpha432.oyvey.event.impl.totemparticle.TotemParticleEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.render.AspectRatio;
import me.alpha432.oyvey.features.settings.Setting;

public class TotemParticle extends Module {
    private static TotemParticle INSTANCE = new TotemParticle();

    public Setting<Integer> red = this.register(new Setting<>("Red", 1, 0, 255));
    public Setting<Integer> blue = this.register(new Setting<>("Blue", 1, 0, 255));
    public Setting<Integer> alpha = this.register(new Setting<>("Alpha", 1, 0, 255));
    public Setting<Integer> green = this.register(new Setting<>("Green", 1, 0, 255));

    public Setting<Integer> red2 = this.register(new Setting<>("Red2", 1, 0, 255));
    public Setting<Integer> blue2 = this.register(new Setting<>("Blue2", 1, 0, 255));
    public Setting<Integer> alpha2 = this.register(new Setting<>("Alpha2", 1, 0, 255));
    public Setting<Integer> green2 = this.register(new Setting<>("Green2", 1, 0, 255));

	//private final C$.r iiiiiiiiIIiIiiIIIiiiIi = register(new C$.r("p$X.ij("[389357852]v]"), new $xc.$0(0, 0, max? 255?))));
	//private final V$.r IiiIiIiiIiiIIiiiIIiiIi = register(new C$.r("$c.n("[743631464]o]o]"), new $df.t( 0, 0, 0)));

    // register = $y.rb /field 

    public Setting<Integer> velocityXZ = this.register(new Setting<>("VelocityXZ", 1, 0, 255));
    public Setting<Integer> velocityY = this.register(new Setting<>("VelocityY", 1, 0, 255));

	//public final NSetting velocityXZ = add(new NSetting("VelocityXZ", 100, 0, 500, 1).setSuffix("%"));
	//public final NSetting velocityY = add(new NSetting("VelocityY", 100, 0, 500, 1).setSuffix("%"));


    public TotemParticle() {
        super("TotemParticle", "TotemParticle", Module.Category.RENDER, true, false, false);
        this.setInstance();
    }

    public static TotemParticle getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TotemParticle();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

	Random random = new Random();

	@Subscribe
	public void idk(TotemParticleEvent event) {
		event.cancel();
		event.velocityZ *= velocityXZ.getValue() / 100;
		event.velocityX *= velocityXZ.getValue() / 100;

		event.velocityY *= velocityY.getValue() / 100;

		event.color = fadeColor(new Color(red.getValue(),green.getValue(),blue.getValue(),alpha.getValue()), new Color(red2.getValue(),green2.getValue(),blue2.getValue(),alpha2.getValue()), random.nextDouble());
	}

    public static Color fadeColor(Color startColor, Color endColor, double quad) {
        int sR = startColor.getRed();
        int sG = startColor.getGreen();
        int sB = startColor.getBlue();
        int sA = startColor.getAlpha();

        int eR = endColor.getRed();
        int eG = endColor.getGreen();
        int eB = endColor.getBlue();
        int eA = endColor.getAlpha();
        return new Color((int) (sR + (eR - sR) * quad),
                (int) (sG + (eG - sG) * quad),
                (int) (sB + (eB - sB) * quad),
                (int) (sA + (eA - sA) * quad));
    }
}
