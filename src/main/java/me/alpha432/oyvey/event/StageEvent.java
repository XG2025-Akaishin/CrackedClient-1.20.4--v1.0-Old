package me.alpha432.oyvey.event;

public class StageEvent extends Event {
    private Stage stage;

    public StageEvent(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}


