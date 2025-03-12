package me.alpha432.oyvey.event.impl.fakeplayerfuture;

import me.alpha432.oyvey.event.Event;
import me.alpha432.oyvey.event.Stage;
import net.minecraft.entity.Entity;

public class AttackEntityEvent extends Event {
    private Stage stage = Stage.PRE;
    @SuppressWarnings("static-access")
    public AttackEntityEvent(){
        this.stage = stage.PRE;
        //super(Stage.Pre);
    }
    private static final AttackEntityEvent INSTANCE = new AttackEntityEvent();

    public Entity entity;

    public static AttackEntityEvent get(Entity entity) {
        INSTANCE.setCancelled(true);
        INSTANCE.entity = entity;
        return INSTANCE;
    }
}
