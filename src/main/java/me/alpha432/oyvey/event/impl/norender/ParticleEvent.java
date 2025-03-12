package me.alpha432.oyvey.event.impl.norender;

import me.alpha432.oyvey.event.Event;
import me.alpha432.oyvey.event.Stage;
import net.minecraft.client.particle.Particle;
import net.minecraft.particle.ParticleEffect;

public class ParticleEvent extends Event {
    private Stage stage = Stage.PRE;
    @SuppressWarnings("static-access")
    public ParticleEvent() {
        this.stage = stage.PRE;
        //super(stage.PRE);
    }
    public static class AddParticle extends ParticleEvent {

        public Particle particle;
        public AddParticle(Particle particle){
            this.particle = particle;
        }

    }

    public static class AddEmmiter extends ParticleEvent {
        public ParticleEffect emmiter;

        public AddEmmiter(ParticleEffect emmiter){
            this.emmiter = emmiter;
        }

    }
}