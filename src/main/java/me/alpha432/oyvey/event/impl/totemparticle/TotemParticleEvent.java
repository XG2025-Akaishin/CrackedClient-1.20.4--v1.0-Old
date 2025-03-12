package me.alpha432.oyvey.event.impl.totemparticle;

import me.alpha432.oyvey.event.Event;
import java.awt.*;

public class TotemParticleEvent extends Event {
    public double velocityX, velocityY, velocityZ;
    public Color color;
    public TotemParticleEvent(double velocityX, double velocityY, double velocityZ) {
        //super(Stage.Pre);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
    }
}
