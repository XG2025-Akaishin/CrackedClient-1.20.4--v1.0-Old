package me.alpha432.oyvey.event.impl.freelook;


import me.alpha432.oyvey.event.StageEventNew;

public class TickEvent extends StageEventNew {

    public static class Pre extends TickEvent {
        private static final Pre INSTANCE = new Pre();

        public static Pre get() {
            return INSTANCE;
        }
    }

    public static class Post extends TickEvent {
        private static final Post INSTANCE = new Post();

        public static Post get() {
            return INSTANCE;
        }
    }

}
