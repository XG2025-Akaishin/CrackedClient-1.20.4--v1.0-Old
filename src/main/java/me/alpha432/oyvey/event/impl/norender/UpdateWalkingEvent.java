package me.alpha432.oyvey.event.impl.norender;

import me.alpha432.oyvey.event.Event;
import me.alpha432.oyvey.event.Stage;

public class UpdateWalkingEvent extends Event {
    private final Stage stage;
    public UpdateWalkingEvent(Stage stage)
    {
        this.stage = stage;
        //super(p_Era);
    }
    public Stage getStage() {
        return stage;
    }
    /*
    public UpdateWalkingEvent(Stage stage) {
        super(stage);
    }*/
}
